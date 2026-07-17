package cl.mateocuetoc.beagibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mateocuetoc.beagibackend.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}