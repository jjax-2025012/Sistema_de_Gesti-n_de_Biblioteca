package com.josethjax.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @Column(nullable = false)
    private LocalDate fechaDevolucionEsperada;

    private LocalDate fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrestamo estado;

    @PrePersist
    public void prePersist() {
        if (this.fechaPrestamo == null) {
            this.fechaPrestamo = LocalDate.now();
        }
        if (this.fechaDevolucionEsperada == null) {
            this.fechaDevolucionEsperada = this.fechaPrestamo.plusDays(14);
        }
        if (this.estado == null) {
            this.estado = EstadoPrestamo.ACTIVO;
        }
    }
}