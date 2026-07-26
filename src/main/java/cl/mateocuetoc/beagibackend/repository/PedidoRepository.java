package cl.mateocuetoc.beagibackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.mateocuetoc.beagibackend.model.EstadoPedido;
import cl.mateocuetoc.beagibackend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "detalles",
            "detalles.producto",
            "detalles.producto.categoria"
    })
    List<Pedido> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "detalles",
            "detalles.producto",
            "detalles.producto.categoria"
    })
    Optional<Pedido> findById(Long id);

    @EntityGraph(attributePaths = {
            "detalles",
            "detalles.producto",
            "detalles.producto.categoria"
    })
    List<Pedido> findByEstado(EstadoPedido estado);
}