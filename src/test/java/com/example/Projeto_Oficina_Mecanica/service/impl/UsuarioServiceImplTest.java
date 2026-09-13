package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.AlterarSenhaDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link UsuarioServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setUp() {
        usuarioAtivo = Usuario.builder()
                .id(1L)
                .nome("Rafael")
                .email("rafael@oficina.com")
                .senha("senhaCriptografada")
                .perfil(PerfilUsuario.ADMIN)
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criptografar a senha e salvar quando o e-mail ainda não existe")
        void deveSalvarUsuario_quandoEmailNaoExiste() {
            CriarUsuarioRequestDTO dto = new CriarUsuarioRequestDTO();
            dto.setNome("Rafael");
            dto.setEmail("rafael@oficina.com");
            dto.setSenha("123456");
            dto.setPerfil(PerfilUsuario.ADMIN);

            Usuario novoUsuario = new Usuario();

            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .email("rafael@oficina.com")
                    .build();

            when(usuarioRepository.existsByEmail("rafael@oficina.com")).thenReturn(false);
            when(usuarioMapper.toEntity(dto)).thenReturn(novoUsuario);
            when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
            when(usuarioRepository.save(novoUsuario)).thenReturn(usuarioAtivo);
            when(usuarioMapper.toResponseDTO(usuarioAtivo)).thenReturn(responseDTO);

            UsuarioResponseDTO resultado = usuarioService.criar(dto);

            assertThat(resultado.getEmail()).isEqualTo("rafael@oficina.com");
            assertThat(novoUsuario.getSenha()).isEqualTo("senhaCriptografada");
            assertThat(novoUsuario.getAtivo()).isTrue();
            verify(auditoriaService).registrar(isNull(), eq("CRIAR"), eq("Usuario"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o e-mail já está cadastrado")
        void deveLancarBusinessException_quandoEmailJaExiste() {
            CriarUsuarioRequestDTO dto = new CriarUsuarioRequestDTO();
            dto.setEmail("rafael@oficina.com");

            when(usuarioRepository.existsByEmail("rafael@oficina.com")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.criar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("E-mail já cadastrado");

            verify(usuarioRepository, never()).save(any());
            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o usuário quando o ID existe")
        void deveRetornar_quandoExiste() {
            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder().id(1L).build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(usuarioMapper.toResponseDTO(usuarioAtivo)).thenReturn(responseDTO);

            UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar nome e perfil sem tocar no e-mail quando ele não muda")
        void deveAtualizar_quandoEmailNaoMuda() {
            AtualizarUsuarioRequestDTO dto = new AtualizarUsuarioRequestDTO();
            dto.setNome("Rafael Carneiro");
            dto.setEmail("rafael@oficina.com");

            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .nome("Rafael Carneiro")
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(usuarioRepository.save(usuarioAtivo)).thenReturn(usuarioAtivo);
            when(usuarioMapper.toResponseDTO(usuarioAtivo)).thenReturn(responseDTO);

            UsuarioResponseDTO resultado = usuarioService.atualizar(1L, dto);

            assertThat(resultado.getNome()).isEqualTo("Rafael Carneiro");
            verify(usuarioRepository, never()).existsByEmail(anyString());
            verify(auditoriaService).registrar(isNull(), eq("ATUALIZAR"), eq("Usuario"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o novo e-mail já pertence a outro usuário")
        void deveLancarBusinessException_quandoNovoEmailJaExiste() {
            AtualizarUsuarioRequestDTO dto = new AtualizarUsuarioRequestDTO();
            dto.setEmail("outro@oficina.com");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(usuarioRepository.existsByEmail("outro@oficina.com")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("E-mail já cadastrado");

            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar o usuário quando ele está ativo")
        void deveDesativar_quandoAtivo() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(usuarioRepository.save(usuarioAtivo)).thenReturn(usuarioAtivo);

            usuarioService.desativar(1L);

            assertThat(usuarioAtivo.getAtivo()).isFalse();
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o usuário já está inativo")
        void deveLancarBusinessException_quandoJaInativo() {
            usuarioAtivo.setAtivo(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));

            assertThatThrownBy(() -> usuarioService.desativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está inativo");
        }
    }

    @Nested
    @DisplayName("alterarSenha()")
    class AlterarSenha {

        @Test
        @DisplayName("deve alterar a senha quando a senha atual confere")
        void deveAlterarSenha_quandoSenhaAtualConfere() {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("123456");
            dto.setNovaSenha("novaSenha123");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);
            when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaCriptografada");

            usuarioService.alterarSenha(1L, dto);

            assertThat(usuarioAtivo.getSenha()).isEqualTo("novaSenhaCriptografada");
            verify(usuarioRepository).save(usuarioAtivo);
            verify(auditoriaService).registrar(isNull(), eq("EXCLUIR"), eq("Usuario"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a senha atual está incorreta")
        void deveLancarBusinessException_quandoSenhaAtualIncorreta() {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("errada");
            dto.setNovaSenha("novaSenha123");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
            when(passwordEncoder.matches("errada", "senhaCriptografada")).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.alterarSenha(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Senha atual inválida");

            verify(usuarioRepository, never()).save(any());
        }
    }
}
