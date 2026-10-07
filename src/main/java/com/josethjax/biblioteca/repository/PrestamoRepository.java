package com.josethjax.biblioteca.repository;

import com.josethjax.biblioteca.entity.EstadoPrestamo;
import com.josethjax.biblioteca.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    List<Prestamo> findByUsuarioEmail(String email);

    boolean existsByLibroId(Long libroId);

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    @Query("SELECT p FROM Prestamo p WHERE p.estado = com.josethjax.biblioteca.entity.EstadoPrestamo.ATRASADO " +
           "OR (p.estado = com.josethjax.biblioteca.entity.EstadoPrestamo.ACTIVO AND p.fechaDevolucionEsperada < CURRENT_DATE)")
    List<Prestamo> findAtrasados();

    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId " +
           "AND (p.estado = com.josethjax.biblioteca.entity.EstadoPrestamo.ATRASADO " +
           "OR (p.estado = com.josethjax.biblioteca.entity.EstadoPrestamo.ACTIVO AND p.fechaDevolucionEsperada < CURRENT_DATE))")
    List<Prestamo> findAtrasadosByUsuarioId(@Param("usuarioId") Long usuarioId);
}