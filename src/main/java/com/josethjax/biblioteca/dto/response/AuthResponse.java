package com.josethjax.biblioteca.dto.response;

import com.josethjax.biblioteca.entity.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
}