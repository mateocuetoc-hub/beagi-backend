package cl.mateocuetoc.beagibackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class ConfiguracionCloudinary {

    @Bean
    public Cloudinary cloudinary(
            @Value("${beagi.cloudinary.cloud-name}") String cloudName,
            @Value("${beagi.cloudinary.api-key}") String apiKey,
            @Value("${beagi.cloudinary.api-secret}") String apiSecret) {

        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "secure", true));
    }
}