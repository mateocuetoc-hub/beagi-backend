package cl.mateocuetoc.beagibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mateocuetoc.beagibackend.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}