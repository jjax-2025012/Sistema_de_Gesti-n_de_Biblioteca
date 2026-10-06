package com.josethjax.biblioteca.service.impl;

import com.josethjax.biblioteca.dto.request.UsuarioRequest;
import com.josethjax.biblioteca.dto.response.UsuarioResponse;
import com.josethjax.biblioteca.entity.EstadoUsuario;
import com.josethjax.biblioteca.entity.Rol;
import com.josethjax.biblioteca.entity.Usuario;
import com.josethjax.biblioteca.exception.BadRequestException;
import com.josethjax.biblioteca.exception.ResourceNotFoundException;
import com.josethjax.biblioteca.repository.UsuarioRepository;
import com.josethjax.biblioteca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya se encuentra registrado");
        }

        Rol rol = Rol.LECTOR;
        if (request.getRol() != null && !request.getRol().isBlank()) {
            try {
                rol = Rol.valueOf(request.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El rol especificado no es válido");
            }
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .estado(EstadoUsuario.ACTIVO)
                .rol(rol)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToResponse(usuario);
    }

    @Override
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return mapToResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuario.setNombre(request.getNombre());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRol() != null && !request.getRol().isBlank()) {
            try {
                usuario.setRol(Rol.valueOf(request.getRol().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El rol especificado no es válido");
            }
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return mapToResponse(actualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}