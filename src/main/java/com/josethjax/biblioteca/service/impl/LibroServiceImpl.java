package com.josethjax.biblioteca.service.impl;

import com.josethjax.biblioteca.dto.request.LibroRequest;
import com.josethjax.biblioteca.dto.response.LibroResponse;
import com.josethjax.biblioteca.entity.Libro;
import com.josethjax.biblioteca.exception.BadRequestException;
import com.josethjax.biblioteca.exception.ResourceNotFoundException;
import com.josethjax.biblioteca.repository.LibroRepository;
import com.josethjax.biblioteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    @Override
    public LibroResponse crearLibro(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Ya existe un libro registrado con el ISBN: " + request.getIsbn());
        }

        Libro libro = Libro.builder()
                .titulo(request.getTitulo())
                .autor(request.getAutor())
                .isbn(request.getIsbn())
                .categoria(request.getCategoria())
                .stockTotal(request.getStockTotal())
                .stockDisponible(request.getStockTotal())
                .build();

        Libro guardado = libroRepository.save(libro);
        return mapToResponse(guardado);
    }

    @Override
    public LibroResponse obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
        return mapToResponse(libro);
    }

    @Override
    public LibroResponse obtenerPorIsbn(String isbn) {
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ISBN: " + isbn));
        return mapToResponse(libro);
    }

    @Override
    public List<LibroResponse> obtenerTodos() {
        return libroRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibroResponse> obtenerDisponibles() {
        return libroRepository.findByStockDisponibleGreaterThan(0)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibroResponse> buscarPorTituloOCategoria(String criterio) {
        return libroRepository.findByTituloContainingIgnoreCaseOrCategoriaContainingIgnoreCase(criterio, criterio)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LibroResponse actualizarLibro(Long id, LibroRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        int diferenciaStock = request.getStockTotal() - libro.getStockTotal();
        int nuevoStockDisponible = libro.getStockDisponible() + diferenciaStock;

        if (nuevoStockDisponible < 0) {
            throw new BadRequestException("El stock total no se puede reducir por debajo del número de préstamos activos actuales.");
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
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + id);
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