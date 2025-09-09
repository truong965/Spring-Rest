package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
      @Bean
      public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
      }

      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http,
                  CustomAutheticationEntryPoint customAutheticationEntryPoint) throws Exception {
            http
                        .csrf(c -> c.disable())
                        .authorizeHttpRequests(
                                    authz -> authz
                                                .requestMatchers("/", "/login").permitAll()
                                                .anyRequest().authenticated())
                        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                                    .authenticationEntryPoint(customAutheticationEntryPoint))
                        // .exceptionHandling(
                        // exceptions -> exceptions
                        // .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
                        // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                        .formLogin(l -> l.disable())
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();

      }

}
