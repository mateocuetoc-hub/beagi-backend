package cl.mateocuetoc.beagibackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cl.mateocuetoc.beagibackend.service.ProductoService;

import cl.mateocuetoc.beagibackend.model.Producto;

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
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) 
    {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }
    
}