package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
                String[] whiteList = {
                                "/",
                                "/api/v1/auth/login", "/api/v1/auth/refresh", "/storage/**",
                                "/api/v1/companies/**", "/api/v1/jobs/**"
                };
                http
                                .csrf(c -> c.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(
                                                requests -> requests
                                                                .requestMatchers(whiteList)
                                                                .permitAll()
                                                                .anyRequest().authenticated())
                                // .anyRequest().permitAll())
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                // bao ve endpoint API bang token
                                .formLogin(f -> f.disable())
                                // .exceptionHandling(
                                // exceptions -> exceptions
                                // .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
                                // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                return http.build();
        }
}
