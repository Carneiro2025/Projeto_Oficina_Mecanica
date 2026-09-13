package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link UsuarioRepository} contra H2 em memória.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão —
 * reconstrução a partir do que já foi confirmado em sessões anteriores
 * (existsByEmail, findByAtivoTrue(Pageable),
 * findByPerfilAndAtivoTrue(PerfilUsuario, Pageable),
 * buscarComFiltros(nome, perfil, Pageable) — esta última já filtra por
 * ativo=true internamente, confirmado no @Query original).
 */
@DataJpaTest
@DisplayName("UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario admin;
    private Usuario mecanico;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        admin = usuarioRepository.save(
                Usuario.builder()
                        .nome("Rafael")
                        .email("rafael@oficina.com")
                        .senha("hash")
                        .perfil(PerfilUsuario.ADMIN)
                        .ativo(true)
                        .build()
        );

        mecanico = usuarioRepository.save(
                Usuario.builder()
                        .nome("Carlos")
                        .email("carlos@oficina.com")
                        .senha("hash")
                        .perfil(PerfilUsuario.MECANICO)
                        .ativo(false)
                        .build()
        );
    }

    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmail {

        @Test
        @DisplayName("deve retornar true quando o e-mail já está cadastrado")
        void deveRetornarTrue_quandoJaExiste() {
            assertThat(usuarioRepository.existsByEmail("rafael@oficina.com")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o e-mail não está cadastrado")
        void deveRetornarFalse_quandoNaoExiste() {
            assertThat(usuarioRepository.existsByEmail("inexistente@oficina.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("findByAtivoTrue()")
    class FindByAtivoTrue {

        @Test
        @DisplayName("deve retornar apenas usuários ativos")
        void deveRetornarApenasAtivos() {
            Page<Usuario> resultado = usuarioRepository.findByAtivoTrue(PageRequest.of(0, 10));

            assertThat(resultado.getContent()).containsExactly(admin);
        }
    }

    @Nested
    @DisplayName("findByPerfilAndAtivoTrue()")
    class FindByPerfilAndAtivoTrue {

        @Test
        @DisplayName("deve retornar vazio quando o único usuário do perfil está inativo")
        void deveRetornarVazio_quandoUnicoUsuarioInativo() {
            Page<Usuario> resultado = usuarioRepository.findByPerfilAndAtivoTrue(
                    PerfilUsuario.MECANICO, PageRequest.of(0, 10)
            );

            assertThat(resultado.getContent()).isEmpty();
        }

        @Test
        @DisplayName("deve retornar o usuário quando perfil e status ativo batem")
        void deveRetornar_quandoPerfilEAtivoBatem() {
            Page<Usuario> resultado = usuarioRepository.findByPerfilAndAtivoTrue(
                    PerfilUsuario.ADMIN, PageRequest.of(0, 10)
            );

            assertThat(resultado.getContent()).containsExactly(admin);
        }
    }

    @Nested
    @DisplayName("buscarComFiltros()")
    class BuscarComFiltros {

        @Test
        @DisplayName("deve filtrar por nome, apenas entre os ativos")
        void deveFiltrarPorNome() {
            Page<Usuario> resultado =
                    usuarioRepository.buscarComFiltros("Rafael", null, PageRequest.of(0, 10));

            assertThat(resultado.getContent()).containsExactly(admin);
        }
    }
}
