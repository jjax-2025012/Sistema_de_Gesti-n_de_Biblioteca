package com.josethjax.biblioteca.controller;

import com.josethjax.biblioteca.dto.request.LibroRequest;
import com.josethjax.biblioteca.dto.response.LibroResponse;
import com.josethjax.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibroResponse> crearLibro(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crearLibro(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LibroResponse> obtenerPorIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(libroService.obtenerPorIsbn(isbn));
    }

    @GetMapping
    public ResponseEntity<List<LibroResponse>> obtenerTodos() {
        return ResponseEntity.ok(libroService.obtenerTodos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<LibroResponse>> obtenerDisponibles() {
        return ResponseEntity.ok(libroService.obtenerDisponibles());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<LibroResponse>> buscarPorTituloOCategoria(@RequestParam String criterio) {
        return ResponseEntity.ok(libroService.buscarPorTituloOCategoria(criterio));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibroResponse> actualizarLibro(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}