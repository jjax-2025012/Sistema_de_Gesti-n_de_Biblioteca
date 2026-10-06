package com.josethjax.biblioteca.service.impl;

import com.josethjax.biblioteca.dto.request.PrestamoRequest;
import com.josethjax.biblioteca.dto.response.PrestamoResponse;
import com.josethjax.biblioteca.entity.EstadoPrestamo;
import com.josethjax.biblioteca.entity.Libro;
import com.josethjax.biblioteca.entity.Prestamo;
import com.josethjax.biblioteca.entity.Usuario;
import com.josethjax.biblioteca.exception.BadRequestException;
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
    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + request.getLibroId()));

        if (libro.getStockDisponible() <= 0) {
            throw new BadRequestException("El libro no tiene unidades disponibles para préstamo.");
        }

        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucionEsperada = request.getFechaDevolucionEsperada() != null
                ? request.getFechaDevolucionEsperada()
                : fechaPrestamo.plusDays(14);

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
            throw new BadRequestException("Este préstamo ya ha sido devuelto anteriormente.");
        }

        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Prestamo actualizado = prestamoRepository.save(prestamo);
        return mapToResponse(actualizado);
    }

    @Override
    public PrestamoResponse obtenerPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));
        return mapToResponse(prestamo);
    }

    @Override
    public List<PrestamoResponse> obtenerTodos() {
        return prestamoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestamoResponse> obtenerPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return prestamoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestamoResponse> obtenerActivos() {
        return prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO)
                .stream()
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