package cl.mateocuetoc.beagibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CrearProductoImagenRequest {

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(
            max = 1000,
            message = "La URL de la imagen no puede superar los 1000 caracteres")
    private String url;

    @NotNull(message = "El orden de la imagen es obligatorio")
    @PositiveOrZero(message = "El orden no puede ser negativo")
    private Integer orden;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}