package cl.mateocuetoc.beagibackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
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
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;
import cl.mateocuetoc.beagibackend.repository.PedidoRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void prepararBaseDePruebas() {
        limpiarBaseDePruebas();
    }

    @AfterEach
    void limpiarDespuesDeCadaPrueba() {
        limpiarBaseDePruebas();
    }

    private void limpiarBaseDePruebas() {
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void crearPedidoCalculaTotalDescuentaStockYLoGuarda() throws Exception {
        Producto producto = crearProducto(
                "Abrigo negro",
                15990,
                3);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCliente": "Cliente prueba",
                                  "telefonoCliente": "+56911112222",
                                  "direccionEntrega": "San Felipe",
                                  "observaciones": "Pedido automatico",
                                  "detalles": [
                                    {
                                      "productoId": %d,
                                      "cantidad": 2
                                    }
                                  ]
                                }
                                """.formatted(producto.getId())))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.nombreCliente")
                        .value("Cliente prueba"))
                .andExpect(jsonPath("$.total").value(31980))
                .andExpect(jsonPath("$.detalles[0].cantidad").value(2))
                .andExpect(jsonPath("$.detalles[0].precioUnitario")
                        .value(15990))
                .andExpect(jsonPath("$.detalles[0].subtotal")
                        .value(31980));

        Producto productoActualizado = productoRepository
                .findById(producto.getId())
                .orElseThrow();

        assertEquals(1, productoActualizado.getStock());
        assertEquals(1, pedidoRepository.count());
    }

    @Test
    void stockInsuficienteDevuelve400YRevierteTodoElPedido()
            throws Exception {

        Producto productoDisponible = crearProducto(
                "Abrigo beige",
                18990,
                5);

        Producto productoSinStock = crearProducto(
                "Chaqueta negra",
                19990,
                1);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCliente": "Prueba rollback",
                                  "telefonoCliente": "+56911112222",
                                  "direccionEntrega": "San Felipe",
                                  "detalles": [
                                    {
                                      "productoId": %d,
                                      "cantidad": 2
                                    },
                                    {
                                      "productoId": %d,
                                      "cantidad": 3
                                    }
                                  ]
                                }
                                """.formatted(
                                        productoDisponible.getId(),
                                        productoSinStock.getId())))
                .andExpect(status().isBadRequest());

        Producto primerProducto = productoRepository
                .findById(productoDisponible.getId())
                .orElseThrow();

        Producto segundoProducto = productoRepository
                .findById(productoSinStock.getId())
                .orElseThrow();

        assertEquals(5, primerProducto.getStock());
        assertEquals(1, segundoProducto.getStock());
        assertEquals(0, pedidoRepository.count());
    }

    @Test
    void productoInexistenteDevuelve404YNoGuardaPedido()
            throws Exception {

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCliente": "Producto inexistente",
                                  "telefonoCliente": "+56911112222",
                                  "direccionEntrega": "San Felipe",
                                  "detalles": [
                                    {
                                      "productoId": 999999,
                                      "cantidad": 1
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isNotFound());

        assertEquals(0, pedidoRepository.count());
    }

    private Producto crearProducto(
            String nombre,
            Integer precio,
            Integer stock) {

        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria de " + nombre);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Producto para prueba automatica");
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDisponible(true);
        producto.setCategoria(categoriaGuardada);

        return productoRepository.save(producto);
    }
}