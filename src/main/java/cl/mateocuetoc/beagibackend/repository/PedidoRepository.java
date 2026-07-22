package cl.mateocuetoc.beagibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mateocuetoc.beagibackend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}