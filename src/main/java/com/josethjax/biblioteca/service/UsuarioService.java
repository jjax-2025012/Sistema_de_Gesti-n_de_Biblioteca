package com.josethjax.biblioteca.service;

import com.josethjax.biblioteca.dto.request.UsuarioRequest;
import com.josethjax.biblioteca.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse registrarUsuario(UsuarioRequest request);

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse obtenerPorEmail(String email);

    List<UsuarioResponse> obtenerTodos();

    UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request);

    void eliminarUsuario(Long id);
}