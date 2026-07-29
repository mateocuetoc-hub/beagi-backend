package cl.mateocuetoc.beagibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductoImagenNoEncontradaException
        extends RuntimeException {

    public ProductoImagenNoEncontradaException(
            Long productoId,
            Long imagenId) {

        super(
                "No existe la imagen con id "
                        + imagenId
                        + " para el producto con id "
                        + productoId);
    }
}