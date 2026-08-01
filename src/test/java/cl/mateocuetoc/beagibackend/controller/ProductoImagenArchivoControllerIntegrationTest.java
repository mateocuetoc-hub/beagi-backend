package cl.mateocuetoc.beagibackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.mateocuetoc.beagibackend.model.Categoria;
import cl.mateocuetoc.beagibackend.model.Producto;
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;
import cl.mateocuetoc.beagibackend.repository.PedidoRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoImagenRepository;
import cl.mateocuetoc.beagibackend.repository.ProductoRepository;
import cl.mateocuetoc.beagibackend.service.AlmacenamientoImagenService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoImagenArchivoControllerIntegrationTest {

    private static final String ADMIN_USERNAME = "admin-test";
    private static final String ADMIN_PASSWORD = "ClaveTest123!";

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

    @MockitoBean
    private AlmacenamientoImagenService almacenamientoImagenService;

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
    void subirImagenesSinArchivosDevuelve400() throws Exception {
        Producto producto = guardarProducto("Chaqueta");

        mockMvc.perform(multipart(
                        "/api/productos/{productoId}/imagenes/archivos",
                        producto.getId())
                        .with(httpBasic(
                                ADMIN_USERNAME,
                                ADMIN_PASSWORD)))
                .andExpect(status().isBadRequest());

        assertEquals(0, productoImagenRepository.count());
    }

    @Test
    void subirMasDeCincoImagenesDevuelve400() throws Exception {
        Producto producto = guardarProducto("Abrigo");

        mockMvc.perform(multipart(
                        "/api/productos/{productoId}/imagenes/archivos",
                        producto.getId())
                        .file(crearImagen("foto-1.jpg", "image/jpeg"))
                        .file(crearImagen("foto-2.jpg", "image/jpeg"))
                        .file(crearImagen("foto-3.jpg", "image/jpeg"))
                        .file(crearImagen("foto-4.jpg", "image/jpeg"))
                        .file(crearImagen("foto-5.jpg", "image/jpeg"))
                        .file(crearImagen("foto-6.jpg", "image/jpeg"))
                        .with(httpBasic(
                                ADMIN_USERNAME,
                                ADMIN_PASSWORD)))
                .andExpect(status().isBadRequest());

        assertEquals(0, productoImagenRepository.count());
    }

    @Test
    void subirArchivoConFormatoInvalidoDevuelve400()
            throws Exception {

        Producto producto = guardarProducto("Vestido");

        doThrow(new IllegalArgumentException(
                "Solo se permiten imágenes JPG, PNG o WebP."))
                .when(almacenamientoImagenService)
                .validarImagen(any());

        MockMultipartFile archivoInvalido =
                crearImagen("documento.txt", "text/plain");

        mockMvc.perform(multipart(
                        "/api/productos/{productoId}/imagenes/archivos",
                        producto.getId())
                        .file(archivoInvalido)
                        .with(httpBasic(
                                ADMIN_USERNAME,
                                ADMIN_PASSWORD)))
                .andExpect(status().isBadRequest());

        assertEquals(0, productoImagenRepository.count());
    }

    @Test
    void subirImagenesValidasDevuelve201YLasAsocia()
            throws Exception {

        Producto producto = guardarProducto("Chaqueta circular");

        when(almacenamientoImagenService.subirImagen(any()))
                .thenReturn(
                        "https://res.cloudinary.com/beagi/frente.jpg",
                        "https://res.cloudinary.com/beagi/detalle.webp");

        MockMultipartFile imagenFrente =
                crearImagen("frente.jpg", "image/jpeg");

        MockMultipartFile imagenDetalle =
                crearImagen("detalle.webp", "image/webp");

        mockMvc.perform(multipart(
                        "/api/productos/{productoId}/imagenes/archivos",
                        producto.getId())
                        .file(imagenFrente)
                        .file(imagenDetalle)
                        .with(httpBasic(
                                ADMIN_USERNAME,
                                ADMIN_PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].url").value(
                        "https://res.cloudinary.com/beagi/frente.jpg"))
                .andExpect(jsonPath("$[0].orden").value(0))
                .andExpect(jsonPath("$[1].url").value(
                        "https://res.cloudinary.com/beagi/detalle.webp"))
                .andExpect(jsonPath("$[1].orden").value(1));

        assertEquals(2, productoImagenRepository.count());
    }

    private MockMultipartFile crearImagen(
            String nombre,
            String tipoContenido) {

        return new MockMultipartFile(
                "archivos",
                nombre,
                tipoContenido,
                new byte[] {1, 2, 3});
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
}