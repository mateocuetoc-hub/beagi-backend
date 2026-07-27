package cl.mateocuetoc.beagibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import cl.mateocuetoc.beagibackend.model.EstadoPedido;

@ResponseStatus(HttpStatus.CONFLICT)
public class TransicionEstadoPedidoInvalidaException
        extends RuntimeException {

    public TransicionEstadoPedidoInvalidaException(
            EstadoPedido estadoActual,
            EstadoPedido nuevoEstado) {

        super("No se puede cambiar el estado del pedido de "
                + estadoActual
                + " a "
                + nuevoEstado);
    }
}
