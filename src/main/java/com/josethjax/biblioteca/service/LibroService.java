package com.josethjax.biblioteca.service;

import com.josethjax.biblioteca.dto.request.LibroRequest;
import com.josethjax.biblioteca.dto.response.LibroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LibroService {

    LibroResponse crearLibro(LibroRequest request);

    LibroResponse obtenerPorId(Long id);

    LibroResponse obtenerPorIsbn(String isbn);

    List<LibroResponse> obtenerTodos();

    Page<LibroResponse> obtenerLibrosPaginados(String titulo, String categoria, String criterio, Pageable pageable);

    List<LibroResponse> obtenerDisponibles();

    List<LibroResponse> buscarPorTituloOCategoria(String criterio);

    LibroResponse actualizarLibro(Long id, LibroRequest request);

    void eliminarLibro(Long id);
}