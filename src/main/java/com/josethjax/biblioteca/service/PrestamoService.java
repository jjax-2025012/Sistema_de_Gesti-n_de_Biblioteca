package com.josethjax.biblioteca.service;

import com.josethjax.biblioteca.dto.request.PrestamoRequest;
import com.josethjax.biblioteca.dto.response.PrestamoResponse;

import java.util.List;

public interface PrestamoService {

    PrestamoResponse registrarPrestamo(PrestamoRequest request);

    PrestamoResponse registrarDevolucion(Long id);

    PrestamoResponse obtenerPorId(Long id);

    List<PrestamoResponse> obtenerTodos();

    List<PrestamoResponse> obtenerPorUsuario(Long usuarioId);

    List<PrestamoResponse> obtenerActivos();
}