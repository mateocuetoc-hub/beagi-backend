package cl.mateocuetoc.beagibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoriaNoEncontradaException extends RuntimeException {

    public CategoriaNoEncontradaException(Long id) {
        super(id == null
                ? "Debes enviar el id de la categoria"
                : "No existe una categoria con id " + id);
    }
}