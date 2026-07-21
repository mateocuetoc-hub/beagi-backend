package cl.mateocuetoc.beagibackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.mateocuetoc.beagibackend.model.Categoria;
import cl.mateocuetoc.beagibackend.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria crearCategoria(Categoria categoria) {
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> actualizarCategoria(
            Long id,
            Categoria datosActualizados) {

        Optional<Categoria> categoriaExistente =
                categoriaRepository.findById(id);

        if (categoriaExistente.isEmpty()) {
            return Optional.empty();
        }

        Categoria categoria = categoriaExistente.get();
        categoria.setNombre(datosActualizados.getNombre());

        return Optional.of(categoriaRepository.save(categoria));
    }

    public boolean eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }

        categoriaRepository.deleteById(id);
        return true;
    }
}