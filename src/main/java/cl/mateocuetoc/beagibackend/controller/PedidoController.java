package cl.mateocuetoc.beagibackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import cl.mateocuetoc.beagibackend.dto.CrearPedidoRequest;
import cl.mateocuetoc.beagibackend.model.Pedido;
import cl.mateocuetoc.beagibackend.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request) {

        Pedido nuevoPedido = pedidoService.crearPedido(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPedido);
    }
}