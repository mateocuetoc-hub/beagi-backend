package cl.mateocuetoc.beagibackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mateocuetoc.beagibackend.dto.CrearProductoImagenRequest;
import cl.mateocuetoc.beagibackend.dto.ProductoRequest;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.model.ProductoImagen;
import cl.mateocuetoc.beagibackend.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarProductoPorId(
            @PathVariable Long id) {

        Optional<Producto> productoEncontrado =
                productoService.buscarPorId(id);

        if (productoEncontrado.isPresent()) {
            return ResponseEntity.ok(productoEncontrado.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(
            @Valid @RequestBody ProductoRequest request) {

        Producto nuevoProducto =
                productoService.crearProducto(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {

        Optional<Producto> productoActualizado =
                productoService.actualizarProducto(id, request);

        if (productoActualizado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productoActualizado.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id) {

        boolean eliminado =
                productoService.eliminarProducto(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productoId}/imagenes")
    public List<ProductoImagen> listarImagenes(
            @PathVariable Long productoId) {

        return productoService.listarImagenes(productoId);
    }

    @PostMapping("/{productoId}/imagenes")
    public ResponseEntity<ProductoImagen> agregarImagen(
            @PathVariable Long productoId,
            @Valid @RequestBody CrearProductoImagenRequest request) {

        ProductoImagen nuevaImagen =
                productoService.agregarImagen(productoId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaImagen);
    }

    @DeleteMapping("/{productoId}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long productoId,
            @PathVariable Long imagenId) {

        productoService.eliminarImagen(productoId, imagenId);

        return ResponseEntity.noContent().build();
    }
}
