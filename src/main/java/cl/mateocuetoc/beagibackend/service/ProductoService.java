package cl.mateocuetoc.beagibackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.mateocuetoc.beagibackend.dto.CrearProductoImagenRequest;
import cl.mateocuetoc.beagibackend.exception.CategoriaNoEncontradaException;
import cl.mateocuetoc.beagibackend.exception.ProductoImagenNoEncontradaException;
import cl.mateocuetoc.beagibackend.exception.ProductoNoEncontradoException;
import cl.mateocuetoc.beagibackend.model.Categoria;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.model.ProductoImagen;
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoImagenRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoImagenRepository productoImagenRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            ProductoImagenRepository productoImagenRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoImagenRepository = productoImagenRepository;
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

    public List<ProductoImagen> listarImagenes(Long productoId) {
        verificarProductoExistente(productoId);

        return productoImagenRepository
                .findByProducto_IdOrderByOrdenAsc(productoId);
    }

    public ProductoImagen agregarImagen(
            Long productoId,
            CrearProductoImagenRequest request) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(
                        () -> new ProductoNoEncontradoException(productoId));

        ProductoImagen imagen = new ProductoImagen();
        imagen.setUrl(request.getUrl());
        imagen.setOrden(request.getOrden());
        imagen.setProducto(producto);

        return productoImagenRepository.save(imagen);
    }

    public void eliminarImagen(Long productoId, Long imagenId) {
        verificarProductoExistente(productoId);

        ProductoImagen imagen = productoImagenRepository
                .findByIdAndProducto_Id(imagenId, productoId)
                .orElseThrow(
                        () -> new ProductoImagenNoEncontradaException(
                                productoId,
                                imagenId));

        productoImagenRepository.delete(imagen);
    }

    private void verificarProductoExistente(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ProductoNoEncontradoException(productoId);
        }
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