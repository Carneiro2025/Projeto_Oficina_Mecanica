package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ClienteMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link ClienteServiceImpl}.
 *
 * Não sobe contexto Spring nem banco de dados: o repositório e o mapper
 * são substituídos por mocks (Mockito), isolando apenas a lógica de negócio
 * da classe de serviço.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteServiceImpl")
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente clienteAtivo;
    private CriarClienteRequestDTO criarDto;

    @BeforeEach
    void setUp() {
        clienteAtivo = Cliente.builder()
                .id(1L)
                .nome("João da Silva")
                .cpfCnpj("123.456.789-09")
                .tipo(TipoPessoa.PF)
                .email("joao@email.com")
                .ativo(true)
                .build();

        criarDto = new CriarClienteRequestDTO();
        criarDto.setNome("João da Silva");
        criarDto.setCpfCnpj("123.456.789-09");
        criarDto.setTipo(TipoPessoa.PF);
        criarDto.setEmail("joao@email.com");
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve salvar o cliente quando o CPF/CNPJ ainda não existe")
        void deveSalvarCliente_quandoCpfCnpjNaoExiste() {
            Cliente novoCliente = Cliente.builder()
                    .nome("João da Silva")
                    .cpfCnpj("123.456.789-09")
                    .tipo(TipoPessoa.PF)
                    .build();

            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .cpfCnpj("123.456.789-09")
                    .ativo(true)
                    .build();

            when(clienteRepository.existsByCpfCnpj(criarDto.getCpfCnpj())).thenReturn(false);
            when(clienteMapper.toEntity(criarDto)).thenReturn(novoCliente);
            when(clienteRepository.save(novoCliente)).thenReturn(clienteAtivo);
            when(clienteMapper.toResponseDTO(clienteAtivo)).thenReturn(responseDTO);

            ClienteResponseDTO resultado = clienteService.criar(criarDto);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo("João da Silva");

            // Garante que o cliente é sempre criado como ativo
            assertThat(novoCliente.getAtivo()).isTrue();

            verify(clienteRepository).existsByCpfCnpj("123.456.789-09");
            verify(clienteRepository).save(novoCliente);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o CPF/CNPJ já está cadastrado")
        void deveLancarBusinessException_quandoCpfCnpjJaExiste() {
            when(clienteRepository.existsByCpfCnpj(criarDto.getCpfCnpj())).thenReturn(true);

            assertThatThrownBy(() -> clienteService.criar(criarDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CPF/CNPJ já cadastrado");

            // Não deve tentar salvar quando a validação falha
            verify(clienteRepository, never()).save(any());
            verifyNoInteractions(clienteMapper);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o cliente quando o ID existe")
        void deveRetornarCliente_quandoExiste() {
            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(clienteMapper.toResponseDTO(clienteAtivo)).thenReturn(responseDTO);

            ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo("João da Silva");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(clienteMapper);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar quando o cliente existe e o CPF/CNPJ não mudou")
        void deveAtualizar_quandoCpfCnpjNaoMudou() {
            AtualizarClienteRequestDTO dto = new AtualizarClienteRequestDTO();
            dto.setNome("João S. Silva");
            dto.setCpfCnpj("123.456.789-09"); // igual ao já cadastrado

            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João S. Silva")
                    .build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(clienteRepository.save(clienteAtivo)).thenReturn(clienteAtivo);
            when(clienteMapper.toResponseDTO(clienteAtivo)).thenReturn(responseDTO);

            ClienteResponseDTO resultado = clienteService.atualizar(1L, dto);

            assertThat(resultado.getNome()).isEqualTo("João S. Silva");
            verify(clienteMapper).updateEntity(dto, clienteAtivo);
            // Não deve nem consultar duplicidade, pois o CPF/CNPJ não mudou
            verify(clienteRepository, never()).existsByCpfCnpj(anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o novo CPF/CNPJ já pertence a outro cliente")
        void deveLancarBusinessException_quandoNovoCpfCnpjJaExiste() {
            AtualizarClienteRequestDTO dto = new AtualizarClienteRequestDTO();
            dto.setCpfCnpj("987.654.321-00"); // diferente do atual

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(clienteRepository.existsByCpfCnpj("987.654.321-00")).thenReturn(true);

            assertThatThrownBy(() -> clienteService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CPF/CNPJ já cadastrado");

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o cliente não existe")
        void deveLancarResourceNotFoundException_quandoClienteNaoExiste() {
            AtualizarClienteRequestDTO dto = new AtualizarClienteRequestDTO();

            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.atualizar(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar o cliente quando ele está ativo")
        void deveDesativar_quandoAtivo() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(clienteRepository.save(clienteAtivo)).thenReturn(clienteAtivo);

            clienteService.desativar(1L);

            assertThat(clienteAtivo.getAtivo()).isFalse();
            verify(clienteRepository).save(clienteAtivo);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o cliente já está inativo")
        void deveLancarBusinessException_quandoJaInativo() {
            clienteAtivo.setAtivo(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));

            assertThatThrownBy(() -> clienteService.desativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está inativo");

            verify(clienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("reativar()")
    class Reativar {

        @Test
        @DisplayName("deve reativar o cliente quando ele está inativo")
        void deveReativar_quandoInativo() {
            clienteAtivo.setAtivo(false);

            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .ativo(true)
                    .build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(clienteRepository.save(clienteAtivo)).thenReturn(clienteAtivo);
            when(clienteMapper.toResponseDTO(clienteAtivo)).thenReturn(responseDTO);

            ClienteResponseDTO resultado = clienteService.reativar(1L);

            assertThat(resultado.getAtivo()).isTrue();
            assertThat(clienteAtivo.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o cliente já está ativo")
        void deveLancarBusinessException_quandoJaAtivo() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));

            assertThatThrownBy(() -> clienteService.reativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está ativo");

            verify(clienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar uma página de clientes convertidos para DTO")
        void deveRetornarPaginaDeClientes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cliente> paginaEntidades = new PageImpl<>(List.of(clienteAtivo));

            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .build();

            when(clienteRepository.buscarComFiltros("João", null, true, pageable))
                    .thenReturn(paginaEntidades);
            when(clienteMapper.toResponseDTO(clienteAtivo)).thenReturn(responseDTO);

            Page<ClienteResponseDTO> resultado =
                    clienteService.listar("João", null, true, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getNome()).isEqualTo("João da Silva");
        }
    }
}
