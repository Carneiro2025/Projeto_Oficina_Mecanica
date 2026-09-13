package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.RecebimentoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.RecebimentoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Recebimento;
import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusRecebimento;
import com.example.Projeto_Oficina_Mecanica.repository.RecebimentoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link RecebimentoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecebimentoServiceImpl")
class RecebimentoServiceImplTest {

    @Mock
    private RecebimentoRepository recebimentoRepository;

    @InjectMocks
    private RecebimentoServiceImpl recebimentoService;

    private Recebimento recebimentoPendente;
    private OrdemServico ordemServico;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        ordemServico = OrdemServico.builder().id(10L).build();
        cliente = Cliente.builder().id(1L).nome("João da Silva").build();

        recebimentoPendente = Recebimento.builder()
                .id(1L)
                .ordemServico(ordemServico)
                .cliente(cliente)
                .valor(new BigDecimal("250.00"))
                .status(StatusRecebimento.PENDENTE)
                .build();
    }

    @Nested
    @DisplayName("registrarPagamento()")
    class RegistrarPagamento {

        @Test
        @DisplayName("deve registrar o pagamento quando o recebimento da OS existe")
        void deveRegistrarPagamento_quandoRecebimentoExiste() {
            RecebimentoRequestDTO dto = new RecebimentoRequestDTO();
            dto.setOrdemServicoId(10L);
            dto.setFormaPagamento(FormaPagamento.PIX);

            when(recebimentoRepository.findByOrdemServicoId(10L))
                    .thenReturn(Optional.of(recebimentoPendente));
            when(recebimentoRepository.save(recebimentoPendente)).thenReturn(recebimentoPendente);

            RecebimentoResponseDTO resultado = recebimentoService.registrarPagamento(dto);

            assertThat(resultado.getOrdemServico()).isEqualTo(10L);
            assertThat(resultado.getCliente()).isEqualTo("João da Silva");
            verify(recebimentoRepository).save(recebimentoPendente);
        }

        @Test
        @DisplayName("deve lançar exceção quando não existe recebimento para a OS informada")
        void deveLancarExcecao_quandoRecebimentoNaoExiste() {
            RecebimentoRequestDTO dto = new RecebimentoRequestDTO();
            dto.setOrdemServicoId(999L);

            when(recebimentoRepository.findByOrdemServicoId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> recebimentoService.registrarPagamento(dto))
                    .isInstanceOf(RuntimeException.class);

            verify(recebimentoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o recebimento quando o ID existe")
        void deveRetornar_quandoExiste() {
            when(recebimentoRepository.findById(1L)).thenReturn(Optional.of(recebimentoPendente));

            RecebimentoResponseDTO resultado = recebimentoService.buscarPorId(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getValor()).isEqualByComparingTo("250.00");
        }

        @Test
        @DisplayName("deve lançar exceção quando o ID não existe")
        void deveLancarExcecao_quandoNaoExiste() {
            when(recebimentoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> recebimentoService.buscarPorId(99L))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar todos os recebimentos convertidos para DTO")
        void deveListarTodos() {
            when(recebimentoRepository.findAll()).thenReturn(List.of(recebimentoPendente));

            List<RecebimentoResponseDTO> resultado = recebimentoService.listar();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("listarPendentes()")
    class ListarPendentes {

        @Test
        @DisplayName("deve retornar apenas os recebimentos com status PENDENTE")
        void deveListarPendentes() {
            when(recebimentoRepository.findByStatus(StatusRecebimento.PENDENTE))
                    .thenReturn(List.of(recebimentoPendente));

            List<RecebimentoResponseDTO> resultado = recebimentoService.listarPendentes();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getStatus()).isEqualTo(StatusRecebimento.PENDENTE);
        }
    }

    @Nested
    @DisplayName("listarPorCliente()")
    class ListarPorCliente {

        @Test
        @DisplayName("deve retornar os recebimentos do cliente informado")
        void deveListarPorCliente() {
            when(recebimentoRepository.findByClienteId(1L)).thenReturn(List.of(recebimentoPendente));

            List<RecebimentoResponseDTO> resultado = recebimentoService.listarPorCliente(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getCliente()).isEqualTo("João da Silva");
        }
    }
}
