
package cl.mateocuetoc.beagibackend.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


import cl.mateocuetoc.beagibackend.model.Producto;

// Spring crea y administra un bean de esta clase, que puede ser inyectado en otras clases con @Autowired
@Service

public class ProductoService 
{

    // atributo final que es una lista de productos, inicializada como un ArrayList vacío
    private final List<Producto> productos = new ArrayList<>();
    public ProductoService()
    {
        // Crear un producto de ejemplo y agregarlo a la lista de productos
        Producto abrigo = new Producto(1L, "Abrigo", "Abrigo largo de mujer", 15990, 3, true, "Abrigos");
        productos.add(abrigo);

    }
    public List<Producto> listarProductos() 
    {
        // Retorna la lista de productos
        return productos;
    }
    public Optional<Producto> buscar_Por_Id(Long id) 
    {
        // for each para recorrer la lista de productos y buscar un producto por su id
        for(Producto p: productos)
        {
            if(p.getId().equals(id))
            {
                return Optional.of(p);
            }

        }
        return Optional.empty();
        // Para que explicar xd

    }


    
} 
