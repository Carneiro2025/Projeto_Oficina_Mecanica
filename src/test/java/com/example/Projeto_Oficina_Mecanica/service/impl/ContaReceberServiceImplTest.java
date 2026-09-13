package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaReceberResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaReceberMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link ContaReceberServiceImpl}. Escrito nesta sessão
 * junto com a reconstrução do service (não existia teste anterior para
 * travar esse contrato) — ver ATENÇÃO no topo de ContaReceberServiceImpl.java.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContaReceberServiceImpl")
class ContaReceberServiceImplTest {

    @Mock
    private ContaReceberRepository repository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private FluxoCaixaRepository fluxoCaixaRepository;

    @Mock
    private ContaReceberMapper mapper;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ContaReceberServiceImpl contaReceberService;

    private Cliente cliente;
    private ContaReceber contaPendente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nome("João da Silva").build();

        contaPendente = ContaReceber.builder()
                .id(1L)
                .cliente(cliente)
                .valor(new BigDecimal("350.00"))
                .status(StatusContaReceber.PENDENTE)
                .build();
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar a conta a receber quando o cliente existe")
        void deveCriarConta_quandoClienteExiste() {
            CriarContaReceberRequestDTO dto = new CriarContaReceberRequestDTO();
            dto.setClienteId(1L);
            dto.setValor(new BigDecimal("350.00"));
            dto.setDataVencimento(LocalDate.now().plusDays(30));

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(repository.save(any(ContaReceber.class))).thenReturn(contaPendente);
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaReceberResponseDTO());

            ContaReceberResponseDTO resultado = contaReceberService.criar(dto);

            assertThat(resultado).isNotNull();
            verifyNoInteractions(ordemServicoRepository);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o cliente não existe")
        void deveLancarResourceNotFoundException_quandoClienteNaoExiste() {
            CriarContaReceberRequestDTO dto = new CriarContaReceberRequestDTO();
            dto.setClienteId(99L);

            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaReceberService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar a conta quando o ID existe")
        void deveRetornar_quandoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(contaPendente));
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaReceberResponseDTO());

            assertThat(contaReceberService.buscarPorId(1L)).isNotNull();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaReceberService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("registrarPagamento()")
    class RegistrarPagamento {

        @Test
        @DisplayName("deve marcar a conta como PAGO, lançar a entrada no fluxo de caixa e registrar auditoria")
        void deveRegistrarPagamento_eLancarNoFluxoDeCaixa() {
            AtualizarContaReceberRequestDTO dto = new AtualizarContaReceberRequestDTO();
            dto.setDataPagamento(LocalDate.now());
            dto.setFormaPagamento(FormaPagamento.PIX);

            when(repository.findById(1L)).thenReturn(Optional.of(contaPendente));
            when(repository.save(contaPendente)).thenReturn(contaPendente);
            when(fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc())
                    .thenReturn(Optional.empty());
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaReceberResponseDTO());

            contaReceberService.registrarPagamento(1L, dto);

            assertThat(contaPendente.getStatus()).isEqualTo(StatusContaReceber.PAGO);
            assertThat(contaPendente.getFormaPagamento()).isEqualTo(FormaPagamento.PIX);

            ArgumentCaptor<FluxoCaixa> captor = ArgumentCaptor.forClass(FluxoCaixa.class);
            verify(fluxoCaixaRepository).save(captor.capture());

            // saldo anterior 0 (nenhum lançamento prévio) + valor da conta = +350
            assertThat(captor.getValue().getSaldoAtual())
                    .isEqualByComparingTo(new BigDecimal("350.00"));

            verify(auditoriaService).registrar(
                    isNull(), eq("RECEBIMENTO"), eq("ContaReceber"), eq(1L), anyString(), anyString()
            );
        }
    }

    @Nested
    @DisplayName("buscarPorStatus()")
    class BuscarPorStatus {

        @Test
        @DisplayName("deve retornar apenas as contas com o status informado")
        void deveRetornarContasPorStatus() {
            when(repository.findByStatus(StatusContaReceber.PENDENTE))
                    .thenReturn(List.of(contaPendente));
            when(mapper.toResponseDTOList(List.of(contaPendente)))
                    .thenReturn(List.of(new ContaReceberResponseDTO()));

            List<ContaReceberResponseDTO> resultado =
                    contaReceberService.buscarPorStatus(StatusContaReceber.PENDENTE);

            assertThat(resultado).hasSize(1);
        }
    }
}
