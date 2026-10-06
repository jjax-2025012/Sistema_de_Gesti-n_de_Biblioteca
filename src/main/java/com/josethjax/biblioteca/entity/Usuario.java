package com.josethjax.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.LECTOR;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoUsuario.ACTIVO;
        }
        if (this.rol == null) {
            this.rol = Rol.LECTOR;
        }
    }
}