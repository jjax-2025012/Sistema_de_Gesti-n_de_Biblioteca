package com.josethjax.biblioteca.service.impl;

import com.josethjax.biblioteca.dto.request.PrestamoRequest;
import com.josethjax.biblioteca.dto.response.PrestamoResponse;
import com.josethjax.biblioteca.entity.EstadoPrestamo;
import com.josethjax.biblioteca.entity.EstadoUsuario;
import com.josethjax.biblioteca.entity.Libro;
import com.josethjax.biblioteca.entity.Prestamo;
import com.josethjax.biblioteca.entity.Rol;
import com.josethjax.biblioteca.entity.Usuario;
import com.josethjax.biblioteca.exception.BusinessRuleException;
import com.josethjax.biblioteca.exception.ResourceNotFoundException;
import com.josethjax.biblioteca.repository.LibroRepository;
import com.josethjax.biblioteca.repository.PrestamoRepository;
import com.josethjax.biblioteca.repository.UsuarioRepository;
import com.josethjax.biblioteca.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        // Verificar si tiene préstamos atrasados y sancionar al usuario
        List<Prestamo> prestamosAtrasados = prestamoRepository.findAtrasadosByUsuarioId(usuario.getId());
        if (!prestamosAtrasados.isEmpty()) {
            for (Prestamo p : prestamosAtrasados) {
                if (p.getEstado() == EstadoPrestamo.ACTIVO) {
                    p.setEstado(EstadoPrestamo.ATRASADO);
                    prestamoRepository.save(p);
                }
            }
            usuario.setEstado(EstadoUsuario.SANCIONADO);
            usuarioRepository.save(usuario);
            throw new BusinessRuleException("El usuario tiene préstamos atrasados y su estado ha sido cambiado a SANCIONADO. No puede realizar nuevos préstamos.");
        }

        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            throw new BusinessRuleException("El usuario se encuentra en estado SANCIONADO y no puede realizar préstamos.");
        }

        // No permitir más de 3 préstamos activos por LECTOR
        if (usuario.getRol() == Rol.LECTOR) {
            long prestamosActivos = prestamoRepository.countByUsuarioIdAndEstado(usuario.getId(), EstadoPrestamo.ACTIVO);
            if (prestamosActivos >= 3) {
                throw new BusinessRuleException("El lector ya cuenta con 3 préstamos activos. Límite máximo alcanzado.");
            }
        }

        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + request.getLibroId()));

        // No prestar si stockDisponible es 0
        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException("El libro no tiene unidades disponibles para préstamo.");
        }

        // Actualizar stock disponible
        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        // Asignar 14 días como plazo de entrega
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucionEsperada = fechaPrestamo.plusDays(14);

        Prestamo prestamo = Prestamo.builder()
                .usuario(usuario)
                .libro(libro)
                .fechaPrestamo(fechaPrestamo)
                .fechaDevolucionEsperada(fechaDevolucionEsperada)
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public PrestamoResponse registrarDevolucion(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("Este préstamo ya ha sido devuelto anteriormente.");
        }

        // Al devolver, actualizar el stock disponible y cambiar el estado a DEVUELTO
        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        Prestamo actualizado = prestamoRepository.save(prestamo);

        // Si el usuario estaba sancionado y ya no tiene más préstamos atrasados, restaurar estado ACTIVO
        Usuario usuario = prestamo.getUsuario();
        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            List<Prestamo> restantesAtrasados = prestamoRepository.findAtrasadosByUsuarioId(usuario.getId());
            if (restantesAtrasados.isEmpty()) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
                usuarioRepository.save(usuario);
            }
        }

        return mapToResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoResponse obtenerPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));
        return mapToResponse(prestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerTodos() {
        return prestamoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return prestamoRepository.findByUsuarioIdOrderByIdDesc(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> findByUsuarioEmail(String email) {
        return prestamoRepository.findByUsuarioEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerMisPrestamos(String email) {
        return findByUsuarioEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerActivos() {
        return prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<PrestamoResponse> obtenerAtrasados() {
        List<Prestamo> atrasados = prestamoRepository.findAtrasados();
        for (Prestamo p : atrasados) {
            if (p.getEstado() == EstadoPrestamo.ACTIVO) {
                p.setEstado(EstadoPrestamo.ATRASADO);
                prestamoRepository.save(p);
            }
        }
        return atrasados.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrestamoResponse mapToResponse(Prestamo prestamo) {
        return PrestamoResponse.builder()
                .id(prestamo.getId())
                .usuarioId(prestamo.getUsuario().getId())
                .nombreUsuario(prestamo.getUsuario().getNombre())
                .libroId(prestamo.getLibro().getId())
                .tituloLibro(prestamo.getLibro().getTitulo())
                .fechaPrestamo(prestamo.getFechaPrestamo())
                .fechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada())
                .fechaDevolucionReal(prestamo.getFechaDevolucionReal())
                .estado(prestamo.getEstado().name())
                .build();
    }
}