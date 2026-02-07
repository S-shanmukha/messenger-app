package com.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class Appconfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.sessionManagement(management->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorize->authorize.requestMatchers("/api/**").authenticated()
                            .anyRequest().permitAll())
                    .addFilterBefore(new jwtValidator(), BasicAuthenticationFilter.class)
                    .csrf(csrf->csrf.disable())
                    .cors(cors->cors.configurationSource(new CorsConfigurationSource() {
//                        @SuppressWarnings("null")
                        @Nullable
                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();

                            corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                            corsConfiguration.setAllowedMethods(Arrays.asList("*"));
                            corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
                            corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));

                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.setMaxAge(3600L);

                            return corsConfiguration;

                        }
                    }));
            return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
