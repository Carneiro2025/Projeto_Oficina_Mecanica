package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaPagarMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.NotaFiscalEntradaRepository;
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
 * Testes unitários de {@link ContaPagarServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContaPagarServiceImpl")
class ContaPagarServiceImplTest {

    @Mock
    private ContaPagarRepository repository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private NotaFiscalEntradaRepository notaFiscalRepository;

    @Mock
    private FluxoCaixaRepository fluxoCaixaRepository;

    @Mock
    private ContaPagarMapper mapper;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ContaPagarServiceImpl contaPagarService;

    private Fornecedor fornecedor;
    private ContaPagar contaPendente;

    @BeforeEach
    void setUp() {
        fornecedor = Fornecedor.builder().id(1L).razaoSocial("Auto Peças LTDA").build();

        contaPendente = ContaPagar.builder()
                .id(1L)
                .fornecedor(fornecedor)
                .descricao("Compra de peças")
                .valor(new BigDecimal("500.00"))
                .status(StatusContaPagar.PENDENTE)
                .build();
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar a conta a pagar quando o fornecedor existe e não há nota fiscal vinculada")
        void deveCriarConta_semNotaFiscal() {
            CriarContaPagarRequestDTO dto = new CriarContaPagarRequestDTO();
            dto.setFornecedorId(1L);
            dto.setDescricao("Compra de peças");
            dto.setValor(new BigDecimal("500.00"));
            dto.setDataVencimento(LocalDate.now().plusDays(30));

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(repository.save(any(ContaPagar.class))).thenReturn(contaPendente);
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaPagarResponseDTO());

            ContaPagarResponseDTO resultado = contaPagarService.criar(dto);

            assertThat(resultado).isNotNull();
            verifyNoInteractions(notaFiscalRepository);
        }

        @Test
        @DisplayName("deve vincular a nota fiscal quando o ID é informado")
        void deveVincularNotaFiscal_quandoIdInformado() {
            CriarContaPagarRequestDTO dto = new CriarContaPagarRequestDTO();
            dto.setFornecedorId(1L);
            dto.setNotaFiscalEntradaId(10L);
            dto.setValor(new BigDecimal("500.00"));

            NotaFiscalEntrada nota = NotaFiscalEntrada.builder().id(10L).build();

            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(notaFiscalRepository.findById(10L)).thenReturn(Optional.of(nota));
            when(repository.save(any(ContaPagar.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any(ContaPagar.class))).thenReturn(new ContaPagarResponseDTO());

            ArgumentCaptor<ContaPagar> captor = ArgumentCaptor.forClass(ContaPagar.class);

            contaPagarService.criar(dto);

            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getNotaFiscalEntrada()).isEqualTo(nota);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o fornecedor não existe")
        void deveLancarResourceNotFoundException_quandoFornecedorNaoExiste() {
            CriarContaPagarRequestDTO dto = new CriarContaPagarRequestDTO();
            dto.setFornecedorId(99L);

            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaPagarService.criar(dto))
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
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaPagarResponseDTO());

            assertThat(contaPagarService.buscarPorId(1L)).isNotNull();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaPagarService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar somente os campos informados")
        void deveAtualizarCamposInformados() {
            AtualizarContaPagarRequestDTO dto = new AtualizarContaPagarRequestDTO();
            dto.setDescricao("Compra de peças - revisada");

            when(repository.findById(1L)).thenReturn(Optional.of(contaPendente));
            when(repository.save(contaPendente)).thenReturn(contaPendente);
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaPagarResponseDTO());

            contaPagarService.atualizar(1L, dto);

            assertThat(contaPendente.getDescricao()).isEqualTo("Compra de peças - revisada");
        }
    }

    @Nested
    @DisplayName("registrarPagamento()")
    class RegistrarPagamento {

        @Test
        @DisplayName("deve marcar a conta como PAGO e lançar a saída no fluxo de caixa")
        void deveRegistrarPagamento_eLancarNoFluxoDeCaixa() {
            AtualizarContaPagarRequestDTO dto = new AtualizarContaPagarRequestDTO();
            dto.setDataPagamento(LocalDate.now());
            dto.setFormaPagamento(FormaPagamento.PIX);

            when(repository.findById(1L)).thenReturn(Optional.of(contaPendente));
            when(repository.save(contaPendente)).thenReturn(contaPendente);
            when(fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc())
                    .thenReturn(Optional.empty());
            when(mapper.toResponseDTO(contaPendente)).thenReturn(new ContaPagarResponseDTO());

            contaPagarService.registrarPagamento(1L, dto);

            assertThat(contaPendente.getStatus()).isEqualTo(StatusContaPagar.PAGO);
            assertThat(contaPendente.getFormaPagamento()).isEqualTo(FormaPagamento.PIX);

            ArgumentCaptor<FluxoCaixa> captor = ArgumentCaptor.forClass(FluxoCaixa.class);
            verify(fluxoCaixaRepository).save(captor.capture());

            // saldo anterior 0 (nenhum lançamento prévio) - valor da conta = -500
            assertThat(captor.getValue().getSaldoAtual())
                    .isEqualByComparingTo(new BigDecimal("-500.00"));

            verify(auditoriaService).registrar(
                    isNull(), eq("PAGAMENTO"), eq("ContaPagar"), eq(1L), anyString(), anyString()
            );
        }
    }

    @Nested
    @DisplayName("excluir()")
    class Excluir {

        @Test
        @DisplayName("deve remover a conta quando ela existe")
        void deveRemover_quandoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(contaPendente));

            contaPagarService.excluir(1L);

            verify(repository).delete(contaPendente);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando a conta não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaPagarService.excluir(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("listarPendentes()")
    class ListarPendentes {

        @Test
        @DisplayName("deve retornar apenas as contas com status PENDENTE")
        void deveListarPendentes() {
            when(repository.findByStatus(StatusContaPagar.PENDENTE))
                    .thenReturn(List.of(contaPendente));
            when(mapper.toResponseDTOList(List.of(contaPendente)))
                    .thenReturn(List.of(new ContaPagarResponseDTO()));

            List<ContaPagarResponseDTO> resultado = contaPagarService.listarPendentes();

            assertThat(resultado).hasSize(1);
        }
    }
}
