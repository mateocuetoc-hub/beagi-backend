package cl.mateocuetoc.beagibackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import cl.mateocuetoc.beagibackend.model.Categoria;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.model.ProductoImagen;
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;
import cl.mateocuetoc.beagibackend.repository.PedidoRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoImagenRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoImagenRepository productoImagenRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Categoria categoria;

    @BeforeEach
    void prepararBaseDePruebas() {
        pedidoRepository.deleteAll();
        productoImagenRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre("Ropa");

        categoria = categoriaRepository.save(nuevaCategoria);
    }

    @Test
    void agregarImagenDevuelve201YLaGuarda() throws Exception {
        Producto producto = guardarProducto("Chaqueta");

        mockMvc.perform(post(
                        "/api/productos/{productoId}/imagenes",
                        producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "url": "https://imagenes.beagi.cl/chaqueta.jpg",
                                  "orden": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.url")
                        .value("https://imagenes.beagi.cl/chaqueta.jpg"))
                .andExpect(jsonPath("$.orden").value(1))
                .andExpect(jsonPath("$.producto").doesNotExist());

        assertEquals(1, productoImagenRepository.count());
    }

    @Test
    void agregarImagenConDatosInvalidosDevuelve400() throws Exception {
        Producto producto = guardarProducto("Polera");

        mockMvc.perform(post(
                        "/api/productos/{productoId}/imagenes",
                        producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "url": " ",
                                  "orden": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url")
                        .value("La URL de la imagen es obligatoria"))
                .andExpect(jsonPath("$.orden")
                        .value("El orden no puede ser negativo"));

        assertEquals(0, productoImagenRepository.count());
    }

    @Test
    void listarImagenesLasDevuelveOrdenadas() throws Exception {
        Producto producto = guardarProducto("Vestido");

        guardarImagen(producto, "https://imagenes.beagi.cl/tercera.jpg", 3);
        guardarImagen(producto, "https://imagenes.beagi.cl/primera.jpg", 1);
        guardarImagen(producto, "https://imagenes.beagi.cl/segunda.jpg", 2);

        mockMvc.perform(get(
                        "/api/productos/{productoId}/imagenes",
                        producto.getId()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].orden").value(1))
                .andExpect(jsonPath("$[0].url")
                        .value("https://imagenes.beagi.cl/primera.jpg"))
                .andExpect(jsonPath("$[1].orden").value(2))
                .andExpect(jsonPath("$[2].orden").value(3));
    }

    @Test
    void listarImagenesDeProductoInexistenteDevuelve404()
            throws Exception {

        mockMvc.perform(get(
                        "/api/productos/{productoId}/imagenes",
                        999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarImagenDevuelve204YLaBorra() throws Exception {
        Producto producto = guardarProducto("Abrigo");

        ProductoImagen imagen = guardarImagen(
                producto,
                "https://imagenes.beagi.cl/abrigo.jpg",
                0);

        mockMvc.perform(delete(
                        "/api/productos/{productoId}/imagenes/{imagenId}",
                        producto.getId(),
                        imagen.getId()))
                .andExpect(status().isNoContent());

        assertFalse(productoImagenRepository.existsById(imagen.getId()));
    }

    @Test
    void eliminarImagenDeOtroProductoDevuelve404() throws Exception {
        Producto primerProducto = guardarProducto("Falda");
        Producto segundoProducto = guardarProducto("Pantalon");

        ProductoImagen imagen = guardarImagen(
                primerProducto,
                "https://imagenes.beagi.cl/falda.jpg",
                0);

        mockMvc.perform(delete(
                        "/api/productos/{productoId}/imagenes/{imagenId}",
                        segundoProducto.getId(),
                        imagen.getId()))
                .andExpect(status().isNotFound());

        assertTrue(productoImagenRepository.existsById(imagen.getId()));
    }

    @Test
    void eliminarImagenInexistenteDevuelve404() throws Exception {
        Producto producto = guardarProducto("Blusa");

        mockMvc.perform(delete(
                        "/api/productos/{productoId}/imagenes/{imagenId}",
                        producto.getId(),
                        999999L))
                .andExpect(status().isNotFound());
    }

    private Producto guardarProducto(String nombre) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Producto para pruebas");
        producto.setPrecio(15000);
        producto.setStock(10);
        producto.setDisponible(true);
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    private ProductoImagen guardarImagen(
            Producto producto,
            String url,
            Integer orden) {

        ProductoImagen imagen = new ProductoImagen();
        imagen.setUrl(url);
        imagen.setOrden(orden);
        imagen.setProducto(producto);

        return productoImagenRepository.save(imagen);
    }
}