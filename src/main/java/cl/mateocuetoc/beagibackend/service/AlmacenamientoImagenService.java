package cl.mateocuetoc.beagibackend.service;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class AlmacenamientoImagenService {

    private static final long TAMANO_MAXIMO =
            8L * 1024L * 1024L;

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp");

    private final Cloudinary cloudinary;

    public AlmacenamientoImagenService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subirImagen(MultipartFile archivo) {
        validarImagen(archivo);

        try {
            Map<?, ?> resultado = cloudinary
                    .uploader()
                    .upload(
                            archivo.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", "beagi/productos",
                                    "resource_type", "image",
                                    "unique_filename", true,
                                    "overwrite", false));

            Object urlSegura = resultado.get("secure_url");

            if (urlSegura == null) {
                throw new IllegalStateException(
                        "Cloudinary no devolvió la URL de la imagen.");
            }

            return urlSegura.toString();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No fue posible subir la imagen.",
                    exception);
        }
    }

    public void validarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una imagen.");
        }

        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 8 MB.");
        }

        String tipoContenido = archivo.getContentType();

        if (tipoContenido == null
                || !TIPOS_PERMITIDOS.contains(
                        tipoContenido.toLowerCase(Locale.ROOT))) {

            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG, PNG o WebP.");
        }
    }
}