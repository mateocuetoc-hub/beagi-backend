package cl.mateocuetoc.beagibackend.dto;

import cl.mateocuetoc.beagibackend.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoPedidoRequest {

    @NotNull(message = "El estado del pedido es obligatorio")
    private EstadoPedido estado;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}