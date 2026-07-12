package cl.mateocuetoc.beagibackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}