package cl.mateocuetoc.beagibackend.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("production")
public class FiltroSoloLecturaProduccion extends OncePerRequestFilter {

    private static final Set<String> METODOS_PERMITIDOS =
            Set.of("GET", "HEAD", "OPTIONS");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (!METODOS_PERMITIDOS.contains(request.getMethod())) {
            response.sendError(
                    HttpStatus.METHOD_NOT_ALLOWED.value(),
                    "La API publica esta configurada como solo lectura.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}