package cl.mateocuetoc.beagibackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mateocuetoc.beagibackend.model.Producto;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @GetMapping
    public List<Producto> listarProductos() {

        Producto abrigo = new Producto(
                1L,
                "Abrigo negro",
                "Abrigo largo de mujer",
                15990,
                3,
                true,
                "Abrigos"
        );

        return List.of(abrigo);
    }
}