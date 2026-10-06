package com.josethjax.biblioteca.repository;

import com.josethjax.biblioteca.entity.EstadoPrestamo;
import com.josethjax.biblioteca.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);
}