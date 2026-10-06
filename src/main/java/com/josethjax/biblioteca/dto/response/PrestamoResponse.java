package com.josethjax.biblioteca.dto.response;

import com.josethjax.biblioteca.entity.EstadoPrestamo;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long libroId;
    private String libroTitulo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
}