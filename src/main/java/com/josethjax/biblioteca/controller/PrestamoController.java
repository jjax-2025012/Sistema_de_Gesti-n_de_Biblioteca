package com.josethjax.biblioteca.controller;

import com.josethjax.biblioteca.dto.request.PrestamoRequest;
import com.josethjax.biblioteca.dto.response.PrestamoResponse;
import com.josethjax.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.registrarPrestamo(request));
    }

    @PutMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoResponse> registrarDevolucion(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrestamoResponse>> obtenerTodos() {
        return ResponseEntity.ok(prestamoService.obtenerTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestamoResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(prestamoService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrestamoResponse>> obtenerActivos() {
        return ResponseEntity.ok(prestamoService.obtenerActivos());
    }
}