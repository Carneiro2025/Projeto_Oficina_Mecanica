package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.VeiculoMapper;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;

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
 * Testes unitários de {@link VeiculoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoServiceImpl")
class VeiculoServiceImplTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private VeiculoServiceImpl veiculoService;

    private Cliente clienteAtivo;
    private Veiculo veiculoAtivo;
    private CriarVeiculoRequestDTO criarDto;

    @BeforeEach
    void setUp() {
        clienteAtivo = Cliente.builder()
                .id(1L)
                .nome("João da Silva")
                .ativo(true)
                .build();

        veiculoAtivo = Veiculo.builder()
                .id(1L)
                .cliente(clienteAtivo)
                .placa("ABC1D23")
                .modelo("Onix")
                .quilometragem(10000)
                .ativo(true)
                .build();

        criarDto = new CriarVeiculoRequestDTO();
        criarDto.setClienteId(1L);
        criarDto.setPlaca("ABC1D23");
        criarDto.setModelo("Onix");
        criarDto.setQuilometragem(10000);
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve salvar o veículo quando placa/chassi/renavam são únicos e o cliente está ativo")
        void deveSalvarVeiculo_quandoDadosValidos() {
            Veiculo novoVeiculo = Veiculo.builder()
                    .placa("ABC1D23")
                    .build();

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .placa("ABC1D23")
                    .build();

            when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(veiculoMapper.toEntity(criarDto)).thenReturn(novoVeiculo);
            when(veiculoRepository.save(novoVeiculo)).thenReturn(veiculoAtivo);
            when(veiculoMapper.toResponseDTO(veiculoAtivo)).thenReturn(responseDTO);

            VeiculoResponseDTO resultado = veiculoService.criar(criarDto);

            assertThat(resultado.getPlaca()).isEqualTo("ABC1D23");
            assertThat(novoVeiculo.getCliente()).isEqualTo(clienteAtivo);
            assertThat(novoVeiculo.getAtivo()).isTrue();
            verify(auditoriaService).registrar(isNull(), eq("CRIAR"), eq("Veiculo"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a placa já está cadastrada")
        void deveLancarBusinessException_quandoPlacaJaExiste() {
            when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(true);

            assertThatThrownBy(() -> veiculoService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("placa");

            verify(veiculoRepository, never()).save(any());
            verifyNoInteractions(clienteRepository);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o chassi já está cadastrado")
        void deveLancarBusinessException_quandoChassiJaExiste() {
            criarDto.setChassi("9BWZZZ377VT004251");

            when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(false);
            when(veiculoRepository.existsByChassi("9BWZZZ377VT004251")).thenReturn(true);

            assertThatThrownBy(() -> veiculoService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("chassi");

            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o cliente informado está inativo")
        void deveLancarBusinessException_quandoClienteInativo() {
            Cliente clienteInativo = Cliente.builder().id(1L).ativo(false).build();

            when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteInativo));

            assertThatThrownBy(() -> veiculoService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("inativo");

            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o cliente não existe")
        void deveLancarResourceNotFoundException_quandoClienteNaoExiste() {
            when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> veiculoService.criar(criarDto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o veículo quando o ID existe")
        void deveRetornarVeiculo_quandoExiste() {
            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .placa("ABC1D23")
                    .build();

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));
            when(veiculoMapper.toResponseDTO(veiculoAtivo)).thenReturn(responseDTO);

            VeiculoResponseDTO resultado = veiculoService.buscarPorId(1L);

            assertThat(resultado.getPlaca()).isEqualTo("ABC1D23");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> veiculoService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar a quilometragem quando o novo valor é maior que o atual")
        void deveAtualizarQuilometragem_quandoValorMaior() {
            AtualizarVeiculoRequestDTO dto = new AtualizarVeiculoRequestDTO();
            dto.setQuilometragem(15000);

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .quilometragem(15000)
                    .build();

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));
            when(veiculoRepository.save(veiculoAtivo)).thenReturn(veiculoAtivo);
            when(veiculoMapper.toResponseDTO(veiculoAtivo)).thenReturn(responseDTO);

            VeiculoResponseDTO resultado = veiculoService.atualizar(1L, dto);

            assertThat(resultado.getQuilometragem()).isEqualTo(15000);
            assertThat(veiculoAtivo.getQuilometragem()).isEqualTo(15000);
            verify(auditoriaService).registrar(isNull(), eq("ATUALIZAR"), eq("Veiculo"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a nova quilometragem é menor que a atual")
        void deveLancarBusinessException_quandoQuilometragemMenor() {
            AtualizarVeiculoRequestDTO dto = new AtualizarVeiculoRequestDTO();
            dto.setQuilometragem(5000);

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));

            assertThatThrownBy(() -> veiculoService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("quilometragem");

            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException ao vincular o veículo a um cliente inativo")
        void deveLancarBusinessException_quandoNovoClienteInativo() {
            AtualizarVeiculoRequestDTO dto = new AtualizarVeiculoRequestDTO();
            dto.setClienteId(2L);

            Cliente outroClienteInativo = Cliente.builder().id(2L).ativo(false).build();

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));
            when(clienteRepository.findById(2L)).thenReturn(Optional.of(outroClienteInativo));

            assertThatThrownBy(() -> veiculoService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("inativo");

            verify(veiculoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar o veículo quando ele está ativo")
        void deveDesativar_quandoAtivo() {
            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));
            when(veiculoRepository.save(veiculoAtivo)).thenReturn(veiculoAtivo);

            veiculoService.desativar(1L);

            assertThat(veiculoAtivo.getAtivo()).isFalse();
            verify(auditoriaService).registrar(isNull(), eq("EXCLUIR"), eq("Veiculo"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o veículo já está inativo")
        void deveLancarBusinessException_quandoJaInativo() {
            veiculoAtivo.setAtivo(false);
            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));

            assertThatThrownBy(() -> veiculoService.desativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está inativo");
        }
    }

    @Nested
    @DisplayName("reativar()")
    class Reativar {

        @Test
        @DisplayName("deve reativar o veículo quando ele está inativo e o cliente está ativo")
        void deveReativar_quandoInativoEClienteAtivo() {
            veiculoAtivo.setAtivo(false);

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .ativo(true)
                    .build();

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));
            when(veiculoRepository.save(veiculoAtivo)).thenReturn(veiculoAtivo);
            when(veiculoMapper.toResponseDTO(veiculoAtivo)).thenReturn(responseDTO);

            VeiculoResponseDTO resultado = veiculoService.reativar(1L);

            assertThat(resultado.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o veículo já está ativo")
        void deveLancarBusinessException_quandoJaAtivo() {
            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));

            assertThatThrownBy(() -> veiculoService.reativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está ativo");
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o cliente do veículo está inativo")
        void deveLancarBusinessException_quandoClienteDoVeiculoInativo() {
            veiculoAtivo.setAtivo(false);
            clienteAtivo.setAtivo(false);

            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoAtivo));

            assertThatThrownBy(() -> veiculoService.reativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("cliente inativo");

            verify(veiculoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar uma página de veículos convertidos para DTO")
        void deveRetornarPaginaDeVeiculos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Veiculo> paginaEntidades = new PageImpl<>(List.of(veiculoAtivo));

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .placa("ABC1D23")
                    .build();

            when(veiculoRepository.buscarComFiltros("ABC1D23", null, null, true, pageable))
                    .thenReturn(paginaEntidades);
            when(veiculoMapper.toResponseDTO(veiculoAtivo)).thenReturn(responseDTO);

            Page<VeiculoResponseDTO> resultado =
                    veiculoService.listar("ABC1D23", null, null, true, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getPlaca()).isEqualTo("ABC1D23");
        }
    }
}
