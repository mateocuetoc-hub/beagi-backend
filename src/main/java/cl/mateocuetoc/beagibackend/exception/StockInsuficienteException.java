package cl.mateocuetoc.beagibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String nombreProducto) {
        super("Stock insuficiente para el producto: " + nombreProducto);
    }
}