package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.ItemNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.NotaFiscalEntradaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.NotaFiscalEntradaMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.NotaFiscalEntradaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;

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
 * Testes unitários de {@link NotaFiscalEntradaServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotaFiscalEntradaServiceImpl")
class NotaFiscalEntradaServiceImplTest {

    @Mock
    private NotaFiscalEntradaRepository repository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private NotaFiscalEntradaMapper mapper;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private ContaPagarRepository contaPagarRepository;

    @InjectMocks
    private NotaFiscalEntradaServiceImpl notaFiscalService;

    private Fornecedor fornecedor;
    private Produto produto;

    @BeforeEach
    void setUp() {
        fornecedor = Fornecedor.builder().id(1L).razaoSocial("Auto Peças LTDA").build();
        produto = Produto.builder().id(5L).descricao("Filtro de óleo").build();
    }

    private CriarNotaFiscalEntradaRequestDTO montarDto() {
        ItemNotaFiscalEntradaRequestDTO item = new ItemNotaFiscalEntradaRequestDTO();
        item.setProdutoId(5L);
        item.setQuantidade(10);
        item.setValorUnitario(new BigDecimal("8.00"));

        CriarNotaFiscalEntradaRequestDTO dto = new CriarNotaFiscalEntradaRequestDTO();
        dto.setNumero("NF-001");
        dto.setFornecedorId(1L);
        dto.setDataEmissao(LocalDate.now());
        dto.setItens(List.of(item));
        return dto;
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar a nota, salvar os itens e movimentar entrada de estoque para cada item")
        void deveCriarNota_eMovimentarEstoque() {
            CriarNotaFiscalEntradaRequestDTO dto = montarDto();

            when(repository.existsByNumero("NF-001")).thenReturn(false);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));
            when(repository.save(any(NotaFiscalEntrada.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any(NotaFiscalEntrada.class)))
                    .thenReturn(new NotaFiscalEntradaResponseDTO());

            notaFiscalService.criar(dto);

            ArgumentCaptor<CriarMovimentacaoEstoqueRequestDTO> captor =
                    ArgumentCaptor.forClass(CriarMovimentacaoEstoqueRequestDTO.class);
            verify(estoqueService).movimentar(captor.capture());

            assertThat(captor.getValue().getProdutoId()).isEqualTo(5L);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(10);
            assertThat(captor.getValue().getTipo()).isEqualTo(TipoMovimentacaoEstoque.ENTRADA);

            ArgumentCaptor<NotaFiscalEntrada> notaCaptor = ArgumentCaptor.forClass(NotaFiscalEntrada.class);
            verify(repository).save(notaCaptor.capture());
            assertThat(notaCaptor.getValue().getItens()).hasSize(1);

            ArgumentCaptor<ContaPagar> contaCaptor = ArgumentCaptor.forClass(ContaPagar.class);
            verify(contaPagarRepository).save(contaCaptor.capture());

            ContaPagar conta = contaCaptor.getValue();
            assertThat(conta.getFornecedor()).isEqualTo(fornecedor);
            assertThat(conta.getNotaFiscalEntrada()).isEqualTo(notaCaptor.getValue());
            // 10 unidades x 8.00 = 80.00 (valorTotal calculado por NotaFiscalEntrada.calcularTotal())
            assertThat(conta.getValor()).isEqualByComparingTo("80.00");
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o número da nota já existe")
        void deveLancarBusinessException_quandoNumeroJaExiste() {
            CriarNotaFiscalEntradaRequestDTO dto = montarDto();

            when(repository.existsByNumero("NF-001")).thenReturn(true);

            assertThatThrownBy(() -> notaFiscalService.criar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("NF-001");

            verify(repository, never()).save(any());
            verifyNoInteractions(fornecedorRepository, estoqueService);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o fornecedor não existe")
        void deveLancarResourceNotFoundException_quandoFornecedorNaoExiste() {
            CriarNotaFiscalEntradaRequestDTO dto = montarDto();

            when(repository.existsByNumero("NF-001")).thenReturn(false);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notaFiscalService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando um produto do item não existe")
        void deveLancarResourceNotFoundException_quandoProdutoDoItemNaoExiste() {
            CriarNotaFiscalEntradaRequestDTO dto = montarDto();

            when(repository.existsByNumero("NF-001")).thenReturn(false);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(produtoRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notaFiscalService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
            verifyNoInteractions(estoqueService);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar a nota fiscal quando o ID existe")
        void deveRetornar_quandoExiste() {
            NotaFiscalEntrada nota = NotaFiscalEntrada.builder().id(1L).numero("NF-001").build();

            when(repository.findById(1L)).thenReturn(Optional.of(nota));
            when(mapper.toResponseDTO(nota)).thenReturn(new NotaFiscalEntradaResponseDTO());

            assertThat(notaFiscalService.buscarPorId(1L)).isNotNull();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notaFiscalService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar o número quando ele muda e ainda não existe")
        void deveAtualizarNumero_quandoNumeroMudaENaoExiste() {
            NotaFiscalEntrada nota = NotaFiscalEntrada.builder().id(1L).numero("NF-001").build();

            AtualizarNotaFiscalEntradaRequestDTO dto = new AtualizarNotaFiscalEntradaRequestDTO();
            dto.setNumero("NF-002");

            when(repository.findById(1L)).thenReturn(Optional.of(nota));
            when(repository.existsByNumero("NF-002")).thenReturn(false);
            when(repository.save(nota)).thenReturn(nota);
            when(mapper.toResponseDTO(nota)).thenReturn(new NotaFiscalEntradaResponseDTO());

            notaFiscalService.atualizar(1L, dto);

            assertThat(nota.getNumero()).isEqualTo("NF-002");
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o novo número já pertence a outra nota")
        void deveLancarBusinessException_quandoNovoNumeroJaExiste() {
            NotaFiscalEntrada nota = NotaFiscalEntrada.builder().id(1L).numero("NF-001").build();

            AtualizarNotaFiscalEntradaRequestDTO dto = new AtualizarNotaFiscalEntradaRequestDTO();
            dto.setNumero("NF-999");

            when(repository.findById(1L)).thenReturn(Optional.of(nota));
            when(repository.existsByNumero("NF-999")).thenReturn(true);

            assertThatThrownBy(() -> notaFiscalService.atualizar(1L, dto))
                    .isInstanceOf(BusinessException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorFornecedor()")
    class BuscarPorFornecedor {

        @Test
        @DisplayName("deve retornar as notas fiscais do fornecedor informado")
        void deveRetornarNotasDoFornecedor() {
            var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
            NotaFiscalEntrada nota = NotaFiscalEntrada.builder().id(1L).build();
            var pagina = new org.springframework.data.domain.PageImpl<>(List.of(nota));

            when(repository.findByFornecedorId(1L, pageable)).thenReturn(pagina);
            when(mapper.toResponseDTO(nota)).thenReturn(new NotaFiscalEntradaResponseDTO());

            var resultado = notaFiscalService.buscarPorFornecedor(1L, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }
    }
}
