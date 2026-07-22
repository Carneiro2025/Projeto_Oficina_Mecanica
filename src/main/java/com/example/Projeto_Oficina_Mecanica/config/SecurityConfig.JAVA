package com.example.Projeto_Oficina_Mecanica.config;

import com.example.Projeto_Oficina_Mecanica.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthFilter jwtAuthFilter;

    private final UserDetailsService userDetailsService;



    // ==========================================================
    // ENDPOINTS PÚBLICOS
    // ==========================================================

    private static final String[] PUBLIC_ENDPOINTS = {

            "/api/auth/**",

            "/v3/api-docs/**",

            "/swagger-ui/**",

            "/swagger-ui.html"

    };




    // ==========================================================
    // CONFIGURAÇÃO PRINCIPAL DE SEGURANÇA
    // ==========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        http

                // API REST usando JWT
                .csrf(AbstractHttpConfigurer::disable)


                // Não utiliza sessão
                .sessionManagement(session ->

                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                .authorizeHttpRequests(auth -> auth



                        // ===============================
                        // ROTAS PÚBLICAS
                        // ===============================

                        .requestMatchers(PUBLIC_ENDPOINTS)
                        .permitAll()



                        // ===============================
                        // USUÁRIOS
                        // ===============================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios"
                        )
                        .hasRole("ADMIN")


                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/usuarios/**"
                        )
                        .hasRole("ADMIN")


                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/usuarios/**"
                        )
                        .hasRole("ADMIN")


                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE"
                        )



                        // ===============================
                        // ORDEM DE SERVIÇO
                        // ===============================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/ordens-servico/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE",
                                "MECANICO"
                        )


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/ordens-servico/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE",
                                "ATENDENTE"
                        )



                        // ===============================
                        // CLIENTES
                        // ===============================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/clientes/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE",
                                "ATENDENTE"
                        )


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/clientes"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE",
                                "ATENDENTE"
                        )


                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/clientes/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE"
                        )


                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/clientes/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "GERENTE"
                        )



                        // ===============================
                        // QUALQUER OUTRA ROTA
                        // ===============================

                        .anyRequest()
                        .authenticated()

                )



                // Provedor de autenticação
                .authenticationProvider(
                        authenticationProvider()
                )


                // Filtro JWT
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );



        return http.build();

    }




    // ==========================================================
    // AUTHENTICATION PROVIDER
    // ==========================================================

    @Bean
    public AuthenticationProvider authenticationProvider() {


        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();


        provider.setUserDetailsService(
                userDetailsService
        );


        provider.setPasswordEncoder(
                passwordEncoder()
        );


        return provider;

    }




    // ==========================================================
    // AUTHENTICATION MANAGER
    // ==========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {


        return configuration.getAuthenticationManager();

    }




    // ==========================================================
    // CRIPTOGRAFIA DE SENHA
    // ==========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {


        return new BCryptPasswordEncoder();

    }


}