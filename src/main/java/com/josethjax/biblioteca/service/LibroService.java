package com.josethjax.biblioteca.service;

import com.josethjax.biblioteca.dto.request.LibroRequest;
import com.josethjax.biblioteca.dto.response.LibroResponse;

import java.util.List;

public interface LibroService {

    LibroResponse crearLibro(LibroRequest request);

    LibroResponse obtenerPorId(Long id);

    LibroResponse obtenerPorIsbn(String isbn);

    List<LibroResponse> obtenerTodos();

    List<LibroResponse> obtenerDisponibles();

    List<LibroResponse> buscarPorTituloOCategoria(String criterio);

    LibroResponse actualizarLibro(Long id, LibroRequest request);

    void eliminarLibro(Long id);
}