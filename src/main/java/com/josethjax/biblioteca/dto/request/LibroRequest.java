package com.josethjax.biblioteca.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibroRequest {

    @NotBlank(message = "El ISBN no puede estar vacío")
    @Pattern(regexp = "^(?:[0-9]{9}[0-9Xx]|[0-9]{13}|[0-9-]{10,17})$", message = "El ISBN debe tener un formato válido de 10 o 13 dígitos")
    private String isbn;

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El autor no puede estar vacío")
    private String autor;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;

    @NotNull(message = "El stock total es obligatorio")
    @Min(value = 1, message = "El stock total debe ser al menos 1")
    private Integer stockTotal;

    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;
}