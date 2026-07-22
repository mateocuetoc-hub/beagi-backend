package cl.mateocuetoc.beagibackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void limpiarBaseDePruebas() {
        categoriaRepository.deleteAll();
    }

    @Test
    void crearCategoriaDevuelve201YGuardaLosDatos() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Chaquetas"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Chaquetas"));

        assertEquals(1, categoriaRepository.count());
    }

    @Test
    void listarCategoriasDevuelve200YLosDatosGuardados() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Abrigos");
        categoriaRepository.save(categoria);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nombre").value("Abrigos"));
    }

    @Test
    void buscarCategoriaPorIdDevuelve200YLaCategoria() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Pantalones");

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        mockMvc.perform(get(
                        "/api/categorias/{id}",
                        categoriaGuardada.getId()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(categoriaGuardada.getId()))
                .andExpect(jsonPath("$.nombre")
                        .value("Pantalones"));
    }

    @Test
    void buscarCategoriaInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/categorias/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarCategoriaDevuelve200YModificaLosDatos() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Accesorios");

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        mockMvc.perform(put(
                        "/api/categorias/{id}",
                        categoriaGuardada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Accesorios premium"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(categoriaGuardada.getId()))
                .andExpect(jsonPath("$.nombre")
                        .value("Accesorios premium"));

        Categoria categoriaActualizada = categoriaRepository
                .findById(categoriaGuardada.getId())
                .orElseThrow();

        assertEquals("Accesorios premium", categoriaActualizada.getNombre());
    }

    @Test
    void eliminarCategoriaDevuelve204YLaBorra() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Calzado");

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        mockMvc.perform(delete(
                        "/api/categorias/{id}",
                        categoriaGuardada.getId()))
                .andExpect(status().isNoContent());

        assertFalse(categoriaRepository.existsById(categoriaGuardada.getId()));
    }
}