package cl.mateocuetoc.beagibackend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.mateocuetoc.beagibackend.dto.CrearPedidoRequest;
import cl.mateocuetoc.beagibackend.dto.DetallePedidoRequest;
import cl.mateocuetoc.beagibackend.exception.ProductoNoEncontradoException;
import cl.mateocuetoc.beagibackend.exception.StockInsuficienteException;
import cl.mateocuetoc.beagibackend.model.DetallePedido;
import cl.mateocuetoc.beagibackend.model.EstadoPedido;
import cl.mateocuetoc.beagibackend.model.Pedido;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.repository.PedidoRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setNombreCliente(request.getNombreCliente());
        pedido.setTelefonoCliente(request.getTelefonoCliente());
        pedido.setDireccionEntrega(request.getDireccionEntrega());
        pedido.setObservaciones(request.getObservaciones());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        int total = 0;

        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            Producto producto = productoRepository
                    .findById(detalleRequest.getProductoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException(
                            detalleRequest.getProductoId()));

            Integer cantidad = detalleRequest.getCantidad();

            if (producto.getStock() < cantidad) {
                throw new StockInsuficienteException(producto.getNombre());
            }

            Integer subtotal = producto.getPrecio() * cantidad;

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.agregarDetalle(detalle);

            total += subtotal;
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }
}