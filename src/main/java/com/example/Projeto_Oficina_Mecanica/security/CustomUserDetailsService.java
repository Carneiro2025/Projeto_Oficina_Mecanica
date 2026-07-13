package com.example.Projeto_Oficina_Mecanica.security;

import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por carregar os dados do usuário para autenticação.
 *
 * O Spring Security utiliza esta classe durante o processo de login
 * para localizar o usuário pelo e-mail informado.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado: " + email
                        ));

        return usuario;
    }
}