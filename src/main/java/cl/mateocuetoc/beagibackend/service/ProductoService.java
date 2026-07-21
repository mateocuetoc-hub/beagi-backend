package cl.mateocuetoc.beagibackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.mateocuetoc.beagibackend.exception.CategoriaNoEncontradaException;
import cl.mateocuetoc.beagibackend.model.Categoria;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        Categoria categoria = obtenerCategoria(producto);

        producto.setId(null);
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    public Optional<Producto> actualizarProducto(
            Long id,
            Producto datosActualizados) {

        if (!productoRepository.existsById(id)) {
            return Optional.empty();
        }

        Categoria categoria = obtenerCategoria(datosActualizados);

        datosActualizados.setId(id);
        datosActualizados.setCategoria(categoria);

        return Optional.of(productoRepository.save(datosActualizados));
    }

    public boolean eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }

    private Categoria obtenerCategoria(Producto producto) {
        if (producto.getCategoria() == null
                || producto.getCategoria().getId() == null) {

            throw new CategoriaNoEncontradaException(null);
        }

        Long categoriaId = producto.getCategoria().getId();

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(
                        () -> new CategoriaNoEncontradaException(categoriaId));
    }
}