package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.ItemOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.OrdemServicoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link OrdemServicoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrdemServicoServiceImpl")
class OrdemServicoServiceImplTest {

    @Mock
    private OrdemServicoRepository repository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private OrdemServicoMapper mapper;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private ContaReceberRepository contaReceberRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private OrdemServicoServiceImpl ordemServicoService;

    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico ordemAberta;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nome("João da Silva").build();
        veiculo = Veiculo.builder().id(1L).placa("ABC1D23").build();

        ordemAberta = OrdemServico.builder()
                .id(1L)
                .numero("OS-001")
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.ABERTA)
                .build();
    }

    private CriarOrdemServicoRequestDTO montarDtoComItemServico() {
        ItemOrdemServicoRequestDTO item = new ItemOrdemServicoRequestDTO();
        item.setTipoItem(TipoItemOrdemServico.SERVICO);
        item.setDescricaoServico("Troca de óleo");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("80.00"));

        CriarOrdemServicoRequestDTO dto = new CriarOrdemServicoRequestDTO();
        dto.setNumero("OS-001");
        dto.setClienteId(1L);
        dto.setVeiculoId(1L);
        dto.setValorDesconto(BigDecimal.ZERO);
        dto.setItens(List.of(item));
        return dto;
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar a OS com item de serviço sem movimentar estoque")
        void deveCriarOS_comItemServico() {
            CriarOrdemServicoRequestDTO dto = montarDtoComItemServico();

            OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO();

            when(repository.existsByNumero("OS-001")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
            when(repository.save(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

            OrdemServicoResponseDTO resultado = ordemServicoService.criar(dto);

            assertThat(resultado).isNotNull();
            verifyNoInteractions(estoqueService);

            ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
            verify(repository).save(captor.capture());

            OrdemServico salva = captor.getValue();
            assertThat(salva.getItens()).hasSize(1);
            assertThat(salva.getStatus()).isEqualTo(StatusOrdemServico.ABERTA);
        }

        @Test
        @DisplayName("deve buscar o produto e movimentar o estoque quando o item é uma peça")
        void deveMovimentarEstoque_quandoItemPeca() {
            ItemOrdemServicoRequestDTO item = new ItemOrdemServicoRequestDTO();
            item.setTipoItem(TipoItemOrdemServico.PECA);
            item.setProdutoId(5L);
            item.setQuantidade(2);
            item.setValorUnitario(new BigDecimal("25.00"));

            CriarOrdemServicoRequestDTO dto = new CriarOrdemServicoRequestDTO();
            dto.setNumero("OS-001");
            dto.setClienteId(1L);
            dto.setVeiculoId(1L);
            dto.setValorDesconto(BigDecimal.ZERO);
            dto.setItens(List.of(item));

            Produto produto = Produto.builder().id(5L).descricao("Filtro").build();

            when(repository.existsByNumero("OS-001")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
            when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));
            when(repository.save(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDTO(any(OrdemServico.class))).thenReturn(new OrdemServicoResponseDTO());

            ordemServicoService.criar(dto);

            ArgumentCaptor<CriarMovimentacaoEstoqueRequestDTO> captor =
                    ArgumentCaptor.forClass(CriarMovimentacaoEstoqueRequestDTO.class);
            verify(estoqueService).movimentar(captor.capture());

            assertThat(captor.getValue().getProdutoId()).isEqualTo(5L);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o número da OS já existe")
        void deveLancarBusinessException_quandoNumeroJaExiste() {
            CriarOrdemServicoRequestDTO dto = montarDtoComItemServico();

            when(repository.existsByNumero("OS-001")).thenReturn(true);

            assertThatThrownBy(() -> ordemServicoService.criar(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Número");

            verify(repository, never()).save(any());
            verifyNoInteractions(clienteRepository, veiculoRepository);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o cliente não existe")
        void deveLancarResourceNotFoundException_quandoClienteNaoExiste() {
            CriarOrdemServicoRequestDTO dto = montarDtoComItemServico();

            when(repository.existsByNumero("OS-001")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ordemServicoService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o veículo não existe")
        void deveLancarResourceNotFoundException_quandoVeiculoNaoExiste() {
            CriarOrdemServicoRequestDTO dto = montarDtoComItemServico();

            when(repository.existsByNumero("OS-001")).thenReturn(false);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ordemServicoService.criar(dto))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar a OS quando o ID existe")
        void deveRetornar_quandoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(ordemAberta));
            when(mapper.toResponseDTO(ordemAberta)).thenReturn(new OrdemServicoResponseDTO());

            OrdemServicoResponseDTO resultado = ordemServicoService.buscarPorId(1L);

            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o ID não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ordemServicoService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar os campos informados e recalcular o total")
        void deveAtualizarCamposInformados() {
            AtualizarOrdemServicoRequestDTO dto = new AtualizarOrdemServicoRequestDTO();
            dto.setObservacoes("Cliente pediu revisão extra");
            dto.setStatus(StatusOrdemServico.EM_ANDAMENTO);

            when(repository.findById(1L)).thenReturn(Optional.of(ordemAberta));
            when(repository.save(ordemAberta)).thenReturn(ordemAberta);
            when(mapper.toResponseDTO(ordemAberta)).thenReturn(new OrdemServicoResponseDTO());

            ordemServicoService.atualizar(1L, dto);

            assertThat(ordemAberta.getObservacoes()).isEqualTo("Cliente pediu revisão extra");
            assertThat(ordemAberta.getStatus()).isEqualTo(StatusOrdemServico.EM_ANDAMENTO);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando a OS não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            AtualizarOrdemServicoRequestDTO dto = new AtualizarOrdemServicoRequestDTO();

            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ordemServicoService.atualizar(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("finalizar()")
    class Finalizar {

        @Test
        @DisplayName("deve alterar o status para FINALIZADA, definir a data de conclusão e gerar a conta a receber")
        void deveFinalizarOS_eGerarContaReceber() {
            ordemAberta.setValorTotal(new BigDecimal("350.00"));

            when(repository.findById(1L)).thenReturn(Optional.of(ordemAberta));
            when(repository.save(ordemAberta)).thenReturn(ordemAberta);
            when(mapper.toResponseDTO(ordemAberta)).thenReturn(new OrdemServicoResponseDTO());

            ordemServicoService.finalizar(1L);

            assertThat(ordemAberta.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
            assertThat(ordemAberta.getDataConclusao()).isNotNull();

            ArgumentCaptor<ContaReceber> captor = ArgumentCaptor.forClass(ContaReceber.class);
            verify(contaReceberRepository).save(captor.capture());

            ContaReceber conta = captor.getValue();
            assertThat(conta.getCliente()).isEqualTo(cliente);
            assertThat(conta.getOrdemServico()).isEqualTo(ordemAberta);
            assertThat(conta.getValor()).isEqualByComparingTo("350.00");

            verify(auditoriaService).registrar(
                    isNull(), eq("OS_FINALIZADA"), eq("OrdemServico"), eq(1L), anyString(), anyString()
            );
        }

        @Test
        @DisplayName("deve lançar BusinessException e não gerar conta a receber quando a OS já está finalizada")
        void deveLancarBusinessException_quandoJaFinalizada() {
            ordemAberta.setStatus(StatusOrdemServico.FINALIZADA);

            when(repository.findById(1L)).thenReturn(Optional.of(ordemAberta));

            assertThatThrownBy(() -> ordemServicoService.finalizar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está finalizada");

            verify(repository, never()).save(any());
            verifyNoInteractions(contaReceberRepository);
        }
    }

    @Nested
    @DisplayName("cancelar()")
    class Cancelar {

        @Test
        @DisplayName("deve alterar o status para CANCELADA e desativar a OS")
        void deveCancelarOS() {
            when(repository.findById(1L)).thenReturn(Optional.of(ordemAberta));
            when(repository.save(ordemAberta)).thenReturn(ordemAberta);

            ordemServicoService.cancelar(1L);

            assertThat(ordemAberta.getStatus()).isEqualTo(StatusOrdemServico.CANCELADA);
            assertThat(ordemAberta.getAtivo()).isFalse();
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando a OS não existe")
        void deveLancarResourceNotFoundException_quandoNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ordemServicoService.cancelar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar uma página de ordens de serviço convertidas para DTO")
        void deveListarOrdens() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<OrdemServico> pagina = new PageImpl<>(List.of(ordemAberta));

            when(repository.findAll(pageable)).thenReturn(pagina);
            when(mapper.toResponseDTO(ordemAberta)).thenReturn(new OrdemServicoResponseDTO());

            Page<OrdemServicoResponseDTO> resultado = ordemServicoService.listar(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }
    }
}
