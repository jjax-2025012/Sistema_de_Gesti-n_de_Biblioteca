package com.josethjax.biblioteca.repository;

import com.josethjax.biblioteca.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Libro> findByStockDisponibleGreaterThan(int cantidad);

    List<Libro> findByTituloContainingIgnoreCaseOrCategoriaContainingIgnoreCase(String titulo, String categoria);

    @Query("SELECT l FROM Libro l WHERE " +
           "(:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:categoria IS NULL OR LOWER(l.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))")
    Page<Libro> findByFiltros(@Param("titulo") String titulo, @Param("categoria") String categoria, Pageable pageable);

    @Query("SELECT l FROM Libro l WHERE " +
           "(:criterio IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :criterio, '%')) OR LOWER(l.categoria) LIKE LOWER(CONCAT('%', :criterio, '%')))")
    Page<Libro> findByCriterio(@Param("criterio") String criterio, Pageable pageable);
}