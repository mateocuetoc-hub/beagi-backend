package cl.mateocuetoc.beagibackend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${beagi.security.admin.username}") String username,
            @Value("${beagi.security.admin.password}") String password,
            PasswordEncoder passwordEncoder) {

        UserDetails administrador = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(administrador);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/productos/**",
                                "/api/categorias/**")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.HEAD,
                                "/api/productos/**",
                                "/api/categorias/**")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/pedidos")
                        .permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .httpBasic(withDefaults());

        return http.build();
    }
}