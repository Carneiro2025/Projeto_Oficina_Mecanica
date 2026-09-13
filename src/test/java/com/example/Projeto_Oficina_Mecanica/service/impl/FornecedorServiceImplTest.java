package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.FornecedorMapper;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link FornecedorServiceImpl}.
 *
 * Não sobe contexto Spring nem banco de dados: repositório e mapper são
 * substituídos por mocks (Mockito).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FornecedorServiceImpl")
class FornecedorServiceImplTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private FornecedorServiceImpl fornecedorService;

    private Fornecedor fornecedorAtivo;
    private CriarFornecedorRequestDTO criarDto;

    @BeforeEach
    void setUp() {
        fornecedorAtivo = Fornecedor.builder()
                .id(1L)
                .razaoSocial("Auto Peças LTDA")
                .cnpj("12.345.678/0001-90")
                .email("contato@autopecas.com")
                .ativo(true)
                .build();

        criarDto = new CriarFornecedorRequestDTO();
        criarDto.setRazaoSocial("Auto Peças LTDA");
        criarDto.setCnpj("12.345.678/0001-90");
        criarDto.setEmail("contato@autopecas.com");
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve salvar o fornecedor quando CNPJ, e-mail e IE ainda não existem")
        void deveSalvarFornecedor_quandoDadosSaoUnicos() {
            Fornecedor novoFornecedor = Fornecedor.builder()
                    .razaoSocial("Auto Peças LTDA")
                    .cnpj("12.345.678/0001-90")
                    .build();

            FornecedorResponseDTO responseDTO = criarResponseDTO(1L, "Auto Peças LTDA", true);

            when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);
            when(fornecedorRepository.existsByEmail("contato@autopecas.com")).thenReturn(false);
            when(fornecedorMapper.toEntity(criarDto)).thenReturn(novoFornecedor);
            when(fornecedorRepository.save(novoFornecedor)).thenReturn(fornecedorAtivo);
            when(fornecedorMapper.toResponseDTO(fornecedorAtivo)).thenReturn(responseDTO);

            FornecedorResponseDTO resultado = fornecedorService.criar(criarDto);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(novoFornecedor.getAtivo()).isTrue();
            verify(fornecedorRepository).save(novoFornecedor);
            verify(auditoriaService).registrar(isNull(), eq("CRIAR"), eq("Fornecedor"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o CNPJ já está cadastrado")
        void deveLancarBusinessException_quandoCnpjJaExiste() {
            when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(true);

            assertThatThrownBy(() -> fornecedorService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CNPJ");

            verify(fornecedorRepository, never()).save(any());
            verifyNoInteractions(fornecedorMapper);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o e-mail já está cadastrado")
        void deveLancarBusinessException_quandoEmailJaExiste() {
            when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);
            when(fornecedorRepository.existsByEmail("contato@autopecas.com")).thenReturn(true);

            assertThatThrownBy(() -> fornecedorService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("e-mail");

            verify(fornecedorRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a Inscrição Estadual já está cadastrada")
        void deveLancarBusinessException_quandoIeJaExiste() {
            criarDto.setInscricaoEstadual("123456789");

            when(fornecedorRepository.existsByCnpj(anyString())).thenReturn(false);
            when(fornecedorRepository.existsByEmail(anyString())).thenReturn(false);
            when(fornecedorRepository.existsByInscricaoEstadual("123456789")).thenReturn(true);

            assertThatThrownBy(() -> fornecedorService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Inscrição Estadual");

            verify(fornecedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o fornecedor quando o ID existe")
        void deveRetornarFornecedor_quandoExiste() {
            FornecedorResponseDTO responseDTO = criarResponseDTO(1L, "Auto Peças LTDA", null);

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));
            when(fornecedorMapper.toResponseDTO(fornecedorAtivo)).thenReturn(responseDTO);

            FornecedorResponseDTO resultado = fornecedorService.buscarPorId(1L);

            assertThat(resultado.getRazaoSocial()).isEqualTo("Auto Peças LTDA");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fornecedorService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar os campos informados quando o fornecedor existe")
        void deveAtualizar_quandoFornecedorExiste() {
            AtualizarFornecedorRequestDTO dto = new AtualizarFornecedorRequestDTO();
            dto.setRazaoSocial("Auto Peças Silva LTDA");

            FornecedorResponseDTO responseDTO = criarResponseDTO(1L, "Auto Peças Silva LTDA", null);

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));
            when(fornecedorRepository.save(fornecedorAtivo)).thenReturn(fornecedorAtivo);
            when(fornecedorMapper.toResponseDTO(fornecedorAtivo)).thenReturn(responseDTO);

            FornecedorResponseDTO resultado = fornecedorService.atualizar(1L, dto);

            assertThat(resultado.getRazaoSocial()).isEqualTo("Auto Peças Silva LTDA");
            assertThat(fornecedorAtivo.getRazaoSocial()).isEqualTo("Auto Peças Silva LTDA");
            verify(auditoriaService).registrar(isNull(), eq("ATUALIZAR"), eq("Fornecedor"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o novo CNPJ já pertence a outro fornecedor")
        void deveLancarBusinessException_quandoNovoCnpjJaExiste() {
            AtualizarFornecedorRequestDTO dto = new AtualizarFornecedorRequestDTO();
            dto.setCnpj("99.999.999/0001-99");

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));
            when(fornecedorRepository.existsByCnpj("99.999.999/0001-99")).thenReturn(true);

            assertThatThrownBy(() -> fornecedorService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CNPJ");

            verify(fornecedorRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o fornecedor não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            AtualizarFornecedorRequestDTO dto = new AtualizarFornecedorRequestDTO();

            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fornecedorService.atualizar(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar o fornecedor quando ele está ativo")
        void deveDesativar_quandoAtivo() {
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));
            when(fornecedorRepository.save(fornecedorAtivo)).thenReturn(fornecedorAtivo);

            fornecedorService.desativar(1L);

            assertThat(fornecedorAtivo.getAtivo()).isFalse();
            verify(fornecedorRepository).save(fornecedorAtivo);
            verify(auditoriaService).registrar(isNull(), eq("EXCLUIR"), eq("Fornecedor"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o fornecedor já está inativo")
        void deveLancarBusinessException_quandoJaInativo() {
            fornecedorAtivo.setAtivo(false);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));

            assertThatThrownBy(() -> fornecedorService.desativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está inativo");

            verify(fornecedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("reativar()")
    class Reativar {

        @Test
        @DisplayName("deve reativar o fornecedor quando ele está inativo")
        void deveReativar_quandoInativo() {
            fornecedorAtivo.setAtivo(false);

            FornecedorResponseDTO responseDTO = criarResponseDTO(1L, null, true);

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));
            when(fornecedorRepository.save(fornecedorAtivo)).thenReturn(fornecedorAtivo);
            when(fornecedorMapper.toResponseDTO(fornecedorAtivo)).thenReturn(responseDTO);

            FornecedorResponseDTO resultado = fornecedorService.reativar(1L);

            assertThat(resultado.getAtivo()).isTrue();
            assertThat(fornecedorAtivo.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o fornecedor já está ativo")
        void deveLancarBusinessException_quandoJaAtivo() {
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorAtivo));

            assertThatThrownBy(() -> fornecedorService.reativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está ativo");

            verify(fornecedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar uma página de fornecedores convertidos para DTO")
        void deveRetornarPaginaDeFornecedores() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Fornecedor> paginaEntidades = new PageImpl<>(List.of(fornecedorAtivo));

            FornecedorResponseDTO responseDTO = criarResponseDTO(1L, "Auto Peças LTDA", null);

            when(fornecedorRepository.buscarComFiltros("Auto", null, null, pageable))
                    .thenReturn(paginaEntidades);
            when(fornecedorMapper.toResponseDTO(fornecedorAtivo)).thenReturn(responseDTO);

            Page<FornecedorResponseDTO> resultado =
                    fornecedorService.listar("Auto", null, null, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getRazaoSocial()).isEqualTo("Auto Peças LTDA");
        }
    }

    /**
     * FornecedorResponseDTO é anotado apenas com @Data (sem @Builder).
     */
    private FornecedorResponseDTO criarResponseDTO(Long id, String razaoSocial, Boolean ativo) {
        FornecedorResponseDTO dto = new FornecedorResponseDTO();
        dto.setId(id);
        dto.setRazaoSocial(razaoSocial);
        dto.setAtivo(ativo);
        return dto;
    }
}
