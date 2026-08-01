package cl.mateocuetoc.beagibackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import cl.mateocuetoc.beagibackend.dto.CrearProductoImagenRequest;
import cl.mateocuetoc.beagibackend.model.ProductoImagen;
import cl.mateocuetoc.beagibackend.service.AlmacenamientoImagenService;
import cl.mateocuetoc.beagibackend.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoImagenArchivoController {

    private static final int MAXIMO_IMAGENES = 5;

    private final ProductoService productoService;
    private final AlmacenamientoImagenService almacenamientoImagenService;

    public ProductoImagenArchivoController(
            ProductoService productoService,
            AlmacenamientoImagenService almacenamientoImagenService) {

        this.productoService = productoService;
        this.almacenamientoImagenService =
                almacenamientoImagenService;
    }

    @PostMapping(
            value = "/{productoId}/imagenes/archivos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProductoImagen>> subirImagenes(
            @PathVariable Long productoId,
            @RequestParam("archivos") List<MultipartFile> archivos) {

        if (archivos == null || archivos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar al menos una imagen.");
        }

        List<ProductoImagen> imagenesExistentes =
                productoService.listarImagenes(productoId);

        if (archivos.size() > MAXIMO_IMAGENES
                || imagenesExistentes.size() + archivos.size()
                        > MAXIMO_IMAGENES) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cada producto puede tener como máximo 5 imágenes.");
        }

        try {
            archivos.forEach(
                    almacenamientoImagenService::validarImagen);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    exception);
        }

        int siguienteOrden = imagenesExistentes.stream()
                .map(ProductoImagen::getOrden)
                .max(Integer::compareTo)
                .orElse(-1) + 1;

        List<ProductoImagen> imagenesGuardadas =
                new ArrayList<>();

        for (MultipartFile archivo : archivos) {
            String url =
                    almacenamientoImagenService.subirImagen(archivo);

            CrearProductoImagenRequest request =
                    new CrearProductoImagenRequest();

            request.setUrl(url);
            request.setOrden(siguienteOrden++);

            ProductoImagen imagenGuardada =
                    productoService.agregarImagen(
                            productoId,
                            request);

            imagenesGuardadas.add(imagenGuardada);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imagenesGuardadas);
    }
}