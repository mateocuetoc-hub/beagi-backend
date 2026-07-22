package cl.mateocuetoc.beagibackend.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CrearPedidoRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotBlank(message = "El telefono del cliente es obligatorio")
    private String telefonoCliente;

    @NotBlank(message = "La direccion de entrega es obligatoria")
    private String direccionEntrega;

    private String observaciones;

    
    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<@Valid DetallePedidoRequest> detalles;

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DetallePedidoRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoRequest> detalles) {
        this.detalles = detalles;
    }
}