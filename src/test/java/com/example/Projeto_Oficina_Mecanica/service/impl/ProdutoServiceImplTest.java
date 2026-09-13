package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ProdutoMapper;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link ProdutoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoServiceImpl")
class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Produto produtoAtivo;
    private ProdutoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        produtoAtivo = Produto.builder()
                .id(1L)
                .codigo("PRD001")
                .descricao("Filtro de óleo")
                .precoCusto(new BigDecimal("10.00"))
                .precoVenda(new BigDecimal("20.00"))
                .estoqueAtual(10)
                .estoqueMinimo(5)
                .ativo(true)
                .build();

        // resposta "crua" que o mapper devolve, antes do serviço enriquecer
        // com margemLucroPercent/estoqueAbaixoMinimo
        responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCodigo("PRD001");
        responseDTO.setDescricao("Filtro de óleo");
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve salvar o produto quando o código ainda não existe")
        void deveSalvarProduto_quandoCodigoNaoExiste() {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");
            dto.setDescricao("Filtro de óleo");

            Produto novoProduto = Produto.builder()
                    .codigo("PRD001")
                    .precoCusto(new BigDecimal("10.00"))
                    .precoVenda(new BigDecimal("20.00"))
                    .build();

            when(produtoRepository.existsByCodigo("PRD001")).thenReturn(false);
            when(produtoMapper.toEntity(dto)).thenReturn(novoProduto);
            when(produtoRepository.save(novoProduto)).thenReturn(produtoAtivo);
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            ProdutoResponseDTO resultado = produtoService.criar(dto);

            assertThat(resultado.getCodigo()).isEqualTo("PRD001");
            assertThat(novoProduto.getAtivo()).isTrue();
            assertThat(novoProduto.getEstoqueAtual()).isZero();
            verify(auditoriaService).registrar(isNull(), eq("CRIAR"), eq("Produto"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve vincular o fornecedor quando o ID é informado")
        void deveVincularFornecedor_quandoIdInformado() {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");
            dto.setFornecedorId(5L);

            Produto novoProduto = new Produto();
            Fornecedor fornecedor = Fornecedor.builder().id(5L).razaoSocial("Auto Peças").build();

            when(produtoRepository.existsByCodigo("PRD001")).thenReturn(false);
            when(produtoMapper.toEntity(dto)).thenReturn(novoProduto);
            when(fornecedorRepository.findById(5L)).thenReturn(Optional.of(fornecedor));
            when(produtoRepository.save(novoProduto)).thenReturn(produtoAtivo);
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            produtoService.criar(dto);

            assertThat(novoProduto.getFornecedor()).isEqualTo(fornecedor);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o fornecedor informado não existe")
        void deveLancarResourceNotFoundException_quandoFornecedorNaoExiste() {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");
            dto.setFornecedorId(99L);

            when(produtoRepository.existsByCodigo("PRD001")).thenReturn(false);
            when(produtoMapper.toEntity(dto)).thenReturn(new Produto());
            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(produtoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o código já está cadastrado")
        void deveLancarBusinessException_quandoCodigoJaExiste() {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");

            when(produtoRepository.existsByCodigo("PRD001")).thenReturn(true);

            assertThatThrownBy(() -> produtoService.criar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("código");

            verify(produtoRepository, never()).save(any());
            verifyNoInteractions(produtoMapper);
        }
    }

    @Nested
    @DisplayName("buscarPorId() / margem e estoque mínimo")
    class BuscarPorId {

        @Test
        @DisplayName("deve calcular a margem de lucro a partir do preço de custo e venda")
        void deveCalcularMargemDeLucro() {
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

            // custo 10.00, venda 20.00 -> margem de 100%
            assertThat(resultado.getMargemLucroPercent()).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("deve retornar margem zero quando o preço de custo é zero")
        void deveRetornarMargemZero_quandoPrecoCustoZero() {
            produtoAtivo.setPrecoCusto(BigDecimal.ZERO);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

            assertThat(resultado.getMargemLucroPercent()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve sinalizar estoque abaixo do mínimo quando o estoque atual é menor ou igual ao mínimo")
        void deveSinalizarEstoqueAbaixoMinimo() {
            produtoAtivo.setEstoqueAtual(3);
            produtoAtivo.setEstoqueMinimo(5);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

            assertThat(resultado.getEstoqueAbaixoMinimo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar o produto delegando os campos simples ao mapper")
        void deveAtualizarProduto() {
            AtualizarProdutoRequestDTO dto = new AtualizarProdutoRequestDTO();

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoRepository.save(produtoAtivo)).thenReturn(produtoAtivo);
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            ProdutoResponseDTO resultado = produtoService.atualizar(1L, dto);

            assertThat(resultado.getCodigo()).isEqualTo("PRD001");
            verify(produtoMapper).updateEntity(dto, produtoAtivo);
            verify(auditoriaService).registrar(isNull(), eq("ATUALIZAR"), eq("Produto"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o novo fornecedor não existe")
        void deveLancarResourceNotFoundException_quandoNovoFornecedorNaoExiste() {
            AtualizarProdutoRequestDTO dto = new AtualizarProdutoRequestDTO();
            dto.setFornecedorId(99L);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.atualizar(1L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(produtoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar o produto quando ele está ativo")
        void deveDesativar_quandoAtivo() {
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoRepository.save(produtoAtivo)).thenReturn(produtoAtivo);

            produtoService.desativar(1L);

            assertThat(produtoAtivo.getAtivo()).isFalse();
            verify(auditoriaService).registrar(isNull(), eq("EXCLUIR"), eq("Produto"), eq(1L), anyString(), anyString());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o produto já está desativado")
        void deveLancarBusinessException_quandoJaDesativado() {
            produtoAtivo.setAtivo(false);
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));

            assertThatThrownBy(() -> produtoService.desativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está desativado");
        }
    }

    @Nested
    @DisplayName("reativar()")
    class Reativar {

        @Test
        @DisplayName("deve reativar o produto quando ele está inativo")
        void deveReativar_quandoInativo() {
            produtoAtivo.setAtivo(false);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));
            when(produtoRepository.save(produtoAtivo)).thenReturn(produtoAtivo);
            when(produtoMapper.toResponseDTO(produtoAtivo)).thenReturn(responseDTO);

            produtoService.reativar(1L);

            assertThat(produtoAtivo.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o produto já está ativo")
        void deveLancarBusinessException_quandoJaAtivo() {
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAtivo));

            assertThatThrownBy(() -> produtoService.reativar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está ativo");
        }
    }
}
