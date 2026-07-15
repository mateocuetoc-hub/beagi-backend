package cl.mateocuetoc.beagibackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        producto.setId(null);
        return productoRepository.save(producto);
    }

    public Optional<Producto> actualizarProducto(Long id, Producto datosActualizados) {
        if (productoRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }

        datosActualizados.setId(id);
        return Optional.of(productoRepository.save(datosActualizados));
    }

    public boolean eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }
}