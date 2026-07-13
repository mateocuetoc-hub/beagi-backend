package cl.mateocuetoc.beagibackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController 
{
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) 
    {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() 
    {

        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarProductoPorId(@PathVariable Long id)
    {
        
        Optional<Producto> productoEncontrado = productoService.buscarPorId(id);
        if (productoEncontrado.isPresent())
        {
            return ResponseEntity.ok(productoEncontrado.get());

        }
        return ResponseEntity.notFound().build();


    }
    @PostMapping
    public ResponseEntity<Producto> crearProducto(
        @Valid @RequestBody Producto producto)
    {
        Producto nuevoProducto = productoService.crearProducto(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id,@Valid @RequestBody Producto datosActualizados)
    {
        Optional<Producto> productoActualizado = productoService.actualizarProducto(id, datosActualizados);
        if (productoActualizado.isEmpty()) 
        {
            return ResponseEntity.notFound().build();
        } 

        return ResponseEntity.ok(productoActualizado.get());

        


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id)
    {
        
        boolean eliminado = productoService.eliminarProducto(id);
        if (!eliminado) 
        {
            return ResponseEntity.notFound().build();
        } 
        return ResponseEntity.noContent().build();
    }
    
}