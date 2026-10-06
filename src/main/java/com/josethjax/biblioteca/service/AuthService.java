package com.josethjax.biblioteca.service;

import com.josethjax.biblioteca.dto.request.AuthLoginRequest;
import com.josethjax.biblioteca.dto.request.AuthRegisterRequest;
import com.josethjax.biblioteca.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthLoginRequest request);

    AuthResponse registrar(AuthRegisterRequest request);
}