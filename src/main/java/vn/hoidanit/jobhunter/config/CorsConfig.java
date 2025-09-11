package vn.hoidanit.jobhunter.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
      @Bean
      public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            // cho phep url nao ket noi toi backend , 4173 , 5173 (port production)
            configuration.setAllowedOrigins(
                        Arrays.asList("http://localhost:3000", "http://localhost:4173", "http://localhost:5173"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
                        "OPTIONS")); // Allowed methods nao duoc ket nnot
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                        "Accept", "x-no-retry"));
            // cho phep gui kem cookies hay khong
            configuration.setAllowCredentials(true);
            //
            configuration.setMaxAge(3600L);
            // How long the response from a pre-flight request can be cached by clients
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
            return source;
      }

}
