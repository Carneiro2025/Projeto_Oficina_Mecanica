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
@RequiredArgsConstructor // Cria automaticamente o construtor para injeção de dependência dos atributos 'final'
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // Lista de endpoints que não necessitam de autenticação (Acesso Público / Documentação)
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @SuppressWarnings("null")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desabilita o CSRF, pois em APIs REST baseadas em Tokens (JWT) ele não é necessário
                .csrf(AbstractHttpConfigurer::disable)

                // Define a política de sessão como STATELESS (A API não guarda estado no servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura as regras de autorizações de requisições HTTP
                .authorizeHttpRequests(auth -> auth

                        // Permite acesso total e irrestrito aos endpoints públicos mapeados acima
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // Regras de controle de acesso para o gerenciamento de Usuários
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMIN", "GERENTE")

                        // Regras de controle de acesso para as Ordens de Serviço (OS)
                        .requestMatchers(HttpMethod.GET, "/api/ordens-servico/**").hasAnyRole("ADMIN", "GERENTE", "MECANICO")
                        .requestMatchers(HttpMethod.POST, "/api/ordens-servico/**").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")

                        // Qualquer outro endpoint que não foi explicitado acima exige autenticação prévia
                        .anyRequest().authenticated()
                )

                // Define o provedor de autenticação customizado da aplicação
                .authenticationProvider(authenticationProvider())

                // Adiciona o filtro JWT customizado antes do filtro padrão de autenticação por Username/Password
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Define o provedor de dados de autenticação.
     * Configura o serviço de busca do usuário e o algoritmo de criptografia de senhas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Vincula o serviço que busca as credenciais no Banco de Dados
        provider.setPasswordEncoder(passwordEncoder());     // Vincula o codificador BCrypt para checagem da senha
        return provider;
    }

    /**
     * Expõe o gerenciador de autenticação oficial do Spring Security para ser injetado nos Controllers (ex: login).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Define o componente responsável por criptografar e validar as senhas utilizando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    .requestMatchers(HttpMethod.GET,
        "/api/clientes/**")
.hasAnyRole(
        "ADMIN",
        "GERENTE",
        "ATENDENTE"
)

.requestMatchers(HttpMethod.POST,
        "/api/clientes")
.hasAnyRole(
        "ADMIN",
        "GERENTE",
        "ATENDENTE"
)

.requestMatchers(HttpMethod.PUT,
        "/api/clientes/**")
.hasAnyRole(
        "ADMIN",
        "GERENTE"
)

.requestMatchers(HttpMethod.PATCH,
        "/api/clientes/**")
.hasAnyRole(
        "ADMIN",
        "GERENTE"
)

}