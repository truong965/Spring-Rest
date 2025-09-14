package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

//swagger: http://localhost:8080/v3/api-docs
//swagger-ui: http://localhost:8080/swagger-ui/index.html
// test api: http://localhost:8080/swagger-ui/index.html#/
@Configuration
public class OpenAPIConfig {

      // Helper method để tạo SecurityScheme, bạn đã viết đúng rồi
      private SecurityScheme createAPIKeyScheme() {
            return new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer");
      }

      @Bean
      public OpenAPI openAPI() {
            return new OpenAPI()
                        // Thêm thông tin chung cho API (tùy chọn nhưng nên có)
                        .info(new Info().title("JobHunter API")
                                    .version("1.0")
                                    .description("API Documentation for JobHunter Application."))

                        // Thêm yêu cầu bảo mật chung cho tất cả các API
                        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))

                        // Định nghĩa các security schemes
                        .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
      }
}