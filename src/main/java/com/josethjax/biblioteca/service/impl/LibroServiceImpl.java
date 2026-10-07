package com.josethjax.biblioteca.service.impl;

import com.josethjax.biblioteca.dto.request.LibroRequest;
import com.josethjax.biblioteca.dto.response.LibroResponse;
import com.josethjax.biblioteca.entity.Libro;
import com.josethjax.biblioteca.exception.BadRequestException;
import com.josethjax.biblioteca.exception.ResourceNotFoundException;
import com.josethjax.biblioteca.repository.LibroRepository;
import com.josethjax.biblioteca.repository.PrestamoRepository;
import com.josethjax.biblioteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    @Override
    @Transactional
    public LibroResponse crearLibro(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Ya existe un libro registrado con el ISBN: " + request.getIsbn());
        }

        int stockDisponible = request.getStockDisponible() != null ? request.getStockDisponible() : request.getStockTotal();
        if (stockDisponible > request.getStockTotal()) {
            throw new BadRequestException("El stock disponible no puede ser mayor que el stock total.");
        }

        Libro libro = Libro.builder()
                .titulo(request.getTitulo())
                .autor(request.getAutor())
                .isbn(request.getIsbn())
                .categoria(request.getCategoria())
                .stockTotal(request.getStockTotal())
                .stockDisponible(stockDisponible)
                .build();

        Libro guardado = libroRepository.save(libro);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponse obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
        return mapToResponse(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponse obtenerPorIsbn(String isbn) {
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ISBN: " + isbn));
        return mapToResponse(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> obtenerTodos() {
        return libroRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponse> obtenerLibrosPaginados(String titulo, String categoria, String criterio, Pageable pageable) {
        Page<Libro> page;
        if (criterio != null && !criterio.isBlank()) {
            page = libroRepository.findByCriterio(criterio.trim(), pageable);
        } else if ((titulo != null && !titulo.isBlank()) || (categoria != null && !categoria.isBlank())) {
            String tit = (titulo != null && !titulo.isBlank()) ? titulo.trim() : null;
            String cat = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;
            page = libroRepository.findByFiltros(tit, cat, pageable);
        } else {
            page = libroRepository.findAll(pageable);
        }
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> obtenerDisponibles() {
        return libroRepository.findByStockDisponibleGreaterThan(0)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> buscarPorTituloOCategoria(String criterio) {
        return libroRepository.findByTituloContainingIgnoreCaseOrCategoriaContainingIgnoreCase(criterio, criterio)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LibroResponse actualizarLibro(Long id, LibroRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        if (!libro.getIsbn().equals(request.getIsbn()) && libroRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Ya existe otro libro registrado con el ISBN: " + request.getIsbn());
        }

        int nuevoStockDisponible;
        if (request.getStockDisponible() != null) {
            if (request.getStockDisponible() > request.getStockTotal()) {
                throw new BadRequestException("El stock disponible no puede ser mayor que el stock total.");
            }
            nuevoStockDisponible = request.getStockDisponible();
        } else {
            int diferenciaStock = request.getStockTotal() - libro.getStockTotal();
            nuevoStockDisponible = libro.getStockDisponible() + diferenciaStock;
            if (nuevoStockDisponible < 0) {
                throw new BadRequestException("El stock total no se puede reducir por debajo del número de préstamos activos actuales.");
            }
        }

        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setIsbn(request.getIsbn());
        libro.setCategoria(request.getCategoria());
        libro.setStockTotal(request.getStockTotal());
        libro.setStockDisponible(nuevoStockDisponible);

        Libro actualizado = libroRepository.save(libro);
        return mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + id);
        }
        if (prestamoRepository.existsByLibroId(id)) {
            throw new BadRequestException("No se puede eliminar el libro porque tiene préstamos asociados.");
        }
        libroRepository.deleteById(id);
    }

    private LibroResponse mapToResponse(Libro libro) {
        return LibroResponse.builder()
                .id(libro.getId())
                .titulo(libro.getTitulo())
                .autor(libro.getAutor())
                .isbn(libro.getIsbn())
                .categoria(libro.getCategoria())
                .stockTotal(libro.getStockTotal())
                .stockDisponible(libro.getStockDisponible())
                .build();
    }
}