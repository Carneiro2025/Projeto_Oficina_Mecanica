package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.MovimentacaoEstoqueResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.MovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.MovimentacaoEstoqueMapper;
import com.example.Projeto_Oficina_Mecanica.repository.MovimentacaoEstoqueRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link EstoqueServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EstoqueServiceImpl")
class EstoqueServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private MovimentacaoEstoqueMapper mapper;

    @InjectMocks
    private EstoqueServiceImpl estoqueService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
                .descricao("Filtro de óleo")
                .estoqueAtual(10)
                .build();
    }

    @Nested
    @DisplayName("movimentar()")
    class Movimentar {

        @Test
        @DisplayName("deve somar ao estoque quando o tipo é ENTRADA")
        void deveSomarEstoque_quandoEntrada() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(TipoMovimentacaoEstoque.ENTRADA);
            dto.setQuantidade(5);

            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            MovimentacaoEstoqueResponseDTO responseDTO = new MovimentacaoEstoqueResponseDTO();

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(mapper.toEntity(dto)).thenReturn(movimentacao);
            when(movimentacaoRepository.save(movimentacao)).thenReturn(movimentacao);
            when(mapper.toResponseDTO(movimentacao)).thenReturn(responseDTO);

            estoqueService.movimentar(dto);

            assertThat(produto.getEstoqueAtual()).isEqualTo(15);
            verify(produtoRepository).save(produto);
            assertThat(movimentacao.getProduto()).isEqualTo(produto);
        }

        @Test
        @DisplayName("deve subtrair do estoque quando o tipo é SAIDA e há saldo suficiente")
        void deveSubtrairEstoque_quandoSaidaComSaldo() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(TipoMovimentacaoEstoque.SAIDA);
            dto.setQuantidade(4);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(mapper.toEntity(dto)).thenReturn(new MovimentacaoEstoque());
            when(movimentacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any())).thenReturn(new MovimentacaoEstoqueResponseDTO());

            estoqueService.movimentar(dto);

            assertThat(produto.getEstoqueAtual()).isEqualTo(6);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a SAIDA é maior que o estoque disponível")
        void deveLancarBusinessException_quandoSaidaMaiorQueEstoque() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(TipoMovimentacaoEstoque.SAIDA);
            dto.setQuantidade(50);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> estoqueService.movimentar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("insuficiente");

            verify(produtoRepository, never()).save(any());
            verify(movimentacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve definir o estoque diretamente quando o tipo é AJUSTE")
        void deveDefinirEstoque_quandoAjuste() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(TipoMovimentacaoEstoque.AJUSTE);
            dto.setQuantidade(100);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(mapper.toEntity(dto)).thenReturn(new MovimentacaoEstoque());
            when(movimentacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any())).thenReturn(new MovimentacaoEstoqueResponseDTO());

            estoqueService.movimentar(dto);

            assertThat(produto.getEstoqueAtual()).isEqualTo(100);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando a quantidade é zero ou negativa")
        void deveLancarBusinessException_quandoQuantidadeInvalida() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(TipoMovimentacaoEstoque.ENTRADA);
            dto.setQuantidade(0);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> estoqueService.movimentar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("maior que zero");

            verify(movimentacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o produto não existe")
        void deveLancarResourceNotFoundException_quandoProdutoNaoExiste() {
            CriarMovimentacaoEstoqueRequestDTO dto = new CriarMovimentacaoEstoqueRequestDTO();
            dto.setProdutoId(99L);

            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> estoqueService.movimentar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar a movimentação quando o ID existe")
        void deveRetornar_quandoExiste() {
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            MovimentacaoEstoqueResponseDTO responseDTO = new MovimentacaoEstoqueResponseDTO();

            when(movimentacaoRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(mapper.toResponseDTO(movimentacao)).thenReturn(responseDTO);

            MovimentacaoEstoqueResponseDTO resultado = estoqueService.buscarPorId(1L);

            assertThat(resultado).isEqualTo(responseDTO);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(movimentacaoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> estoqueService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("buscarPorTipo()")
    class BuscarPorTipo {

        @Test
        @DisplayName("deve retornar as movimentações do tipo informado")
        void deveRetornarMovimentacoes_quandoTipoValido() {
            Pageable pageable = PageRequest.of(0, 10);
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

            when(movimentacaoRepository.findByTipo(TipoMovimentacaoEstoque.ENTRADA))
                    .thenReturn(List.of(movimentacao));
            when(mapper.toResponseDTO(movimentacao)).thenReturn(new MovimentacaoEstoqueResponseDTO());

            var resultado = estoqueService.buscarPorTipo("entrada", pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o tipo informado é inválido")
        void deveLancarBusinessException_quandoTipoInvalido() {
            Pageable pageable = PageRequest.of(0, 10);

            assertThatThrownBy(() -> estoqueService.buscarPorTipo("INEXISTENTE", pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("inválido");
        }
    }
}
