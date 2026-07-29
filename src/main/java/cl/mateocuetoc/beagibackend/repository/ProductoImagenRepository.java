package cl.mateocuetoc.beagibackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mateocuetoc.beagibackend.model.ProductoImagen;

public interface ProductoImagenRepository
        extends JpaRepository<ProductoImagen, Long> {

    List<ProductoImagen> findByProducto_IdOrderByOrdenAsc(
            Long productoId);

    Optional<ProductoImagen> findByIdAndProducto_Id(
            Long imagenId,
            Long productoId);
}