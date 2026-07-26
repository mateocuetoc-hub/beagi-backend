package cl.mateocuetoc.beagibackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

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
        void buscarPedidoPorIdExistenteDevuelve200() throws Exception {
                Producto producto = crearProducto(
                                "Poleron negro",
                                24990,
                                4);

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombreCliente": "Cliente busqueda",
                                                  "telefonoCliente": "+56911112222",
                                                  "direccionEntrega": "San Felipe",
                                                  "detalles": [
                                                    {
                                                      "productoId": %d,
                                                      "cantidad": 1
                                                    }
                                                  ]
                                                }
                                                """.formatted(producto.getId())))
                                .andExpect(status().isCreated());

                Long pedidoId = pedidoRepository.findAll().get(0).getId();

                mockMvc.perform(get("/api/pedidos/{id}", pedidoId))
                                .andExpect(status().isOk())
                                .andExpect(content()
                                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(pedidoId))
                                .andExpect(jsonPath("$.nombreCliente")
                                                .value("Cliente busqueda"))
                                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                                .andExpect(jsonPath("$.total").value(24990));
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
        void actualizarEstadoPedidoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(patch("/api/pedidos/{id}/estado", 999999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "estado": "CONFIRMADO"
                        }
                        """))
                .andExpect(status().isNotFound());
        }

        @Test
        void buscarPedidoPorIdInexistenteDevuelve404() throws Exception {
                mockMvc.perform(get("/api/pedidos/{id}", 999999L))
                                .andExpect(status().isNotFound());
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
        @Test
        void actualizarEstadoPedidoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(patch("/api/pedidos/{id}/estado", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "estado": "NO_EXISTE"
                        }
                        """))
                .andExpect(status().isBadRequest());
        }
        @Test
        void actualizarEstadoPedidoExistenteDevuelve200() throws Exception {
        Producto producto = crearProducto(
                "Polera azul",
                12990,
                2);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "nombreCliente": "Cliente estado",
                        "telefonoCliente": "+56911112222",
                        "direccionEntrega": "San Felipe",
                        "detalles": [
                                {
                                "productoId": %d,
                                "cantidad": 1
                                }
                        ]
                        }
                        """.formatted(producto.getId())))
                .andExpect(status().isCreated());

        Long pedidoId = pedidoRepository.findAll().get(0).getId();

        mockMvc.perform(patch("/api/pedidos/{id}/estado", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "estado": "CONFIRMADO"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pedidoId))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        assertEquals(
                "CONFIRMADO",
                pedidoRepository.findById(pedidoId)
                        .orElseThrow()
                        .getEstado()
                        .name());
        }

        @Test
        void listarPedidosSinFiltroDevuelveTodosLosPedidos() throws Exception {
                Producto producto = crearProducto(
                                "Chaqueta gris",
                                19990,
                                5);

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombreCliente": "Cliente uno",
                                                  "telefonoCliente": "+56911111111",
                                                  "direccionEntrega": "San Felipe",
                                                  "detalles": [
                                                    {
                                                      "productoId": %d,
                                                      "cantidad": 1
                                                    }
                                                  ]
                                                }
                                                """.formatted(producto.getId())))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombreCliente": "Cliente dos",
                                                  "telefonoCliente": "+56922222222",
                                                  "direccionEntrega": "Putaendo",
                                                  "detalles": [
                                                    {
                                                      "productoId": %d,
                                                      "cantidad": 1
                                                    }
                                                  ]
                                                }
                                                """.formatted(producto.getId())))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/pedidos"))
                                .andExpect(status().isOk())
                                .andExpect(content()
                                                .contentTypeCompatibleWith(
                                                                MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void filtrarPedidosPorEstadoDevuelveSoloLasCoincidencias()
                        throws Exception {

                Producto producto = crearProducto(
                                "Abrigo cafe",
                                22990,
                                5);

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombreCliente": "Cliente pendiente",
                                                  "telefonoCliente": "+56933333333",
                                                  "direccionEntrega": "San Felipe",
                                                  "detalles": [
                                                    {
                                                      "productoId": %d,
                                                      "cantidad": 1
                                                    }
                                                  ]
                                                }
                                                """.formatted(producto.getId())))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombreCliente": "Cliente confirmado",
                                                  "telefonoCliente": "+56944444444",
                                                  "direccionEntrega": "San Felipe",
                                                  "detalles": [
                                                    {
                                                      "productoId": %d,
                                                      "cantidad": 1
                                                    }
                                                  ]
                                                }
                                                """.formatted(producto.getId())))
                                .andExpect(status().isCreated());

                Long pedidoConfirmadoId = pedidoRepository.findAll().stream()
                                .filter(pedido -> pedido.getNombreCliente()
                                                .equals("Cliente confirmado"))
                                .findFirst()
                                .orElseThrow()
                                .getId();

                mockMvc.perform(patch(
                                "/api/pedidos/{id}/estado",
                                pedidoConfirmadoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "estado": "CONFIRMADO"
                                                }
                                                """))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/pedidos")
                                .param("estado", "CONFIRMADO"))
                                .andExpect(status().isOk())
                                .andExpect(content()
                                                .contentTypeCompatibleWith(
                                                                MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id")
                                                .value(pedidoConfirmadoId))
                                .andExpect(jsonPath("$[0].nombreCliente")
                                                .value("Cliente confirmado"))
                                .andExpect(jsonPath("$[0].estado")
                                                .value("CONFIRMADO"));
        }
        @Test
        void filtrarPedidosPorEstadoInvalidoDevuelve400() throws Exception {
                mockMvc.perform(get("/api/pedidos")
                                .param("estado", "NO_EXISTE"))
                                .andExpect(status().isBadRequest());
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