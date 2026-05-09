package com.AppRecrutement.AppRecrutement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/candidats/**", "/api/recruteurs/**").permitAll()
                        // GET /api/offres: Public (les candidats peuvent voir les offres)
                        // POST/PUT/DELETE /api/offres: Auth requis (seuls les recruteurs peuvent créer/modifier/supprimer)
                        .requestMatchers(HttpMethod.GET, "/api/offres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/offres/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/offres/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/offres/**").authenticated()
                        // GET /api/candidatures: Public pour voir les candidatures (si nécessaire)
                        // POST /api/candidatures: Auth requis pour créer une candidature
                        // PUT /api/candidatures: Auth requis pour mettre à jour le statut
                        .requestMatchers(HttpMethod.GET, "/api/candidatures/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/candidatures/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/candidatures/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/candidatures/**").authenticated()
                        // POST /api/cvs/upload: Auth requis pour uploader un CV
                        // GET /api/cvs/mon-cv: Auth requis pour voir son CV
                        // GET /api/cvs/download/**: Auth requis pour télécharger/voir un CV
                        // DELETE /api/cvs/mon-cv: Auth requis pour supprimer son CV
                        .requestMatchers(HttpMethod.POST, "/api/cvs/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/cvs/mon-cv").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/cvs/download/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cvs/mon-cv").authenticated()
                        // GET /api/candidats/mon-profil: Auth requis pour voir son profil
                        // GET /api/recruteurs/mon-profil: Auth requis pour voir son profil
                        .requestMatchers(HttpMethod.GET, "/api/candidats/mon-profil").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/recruteurs/mon-profil").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
