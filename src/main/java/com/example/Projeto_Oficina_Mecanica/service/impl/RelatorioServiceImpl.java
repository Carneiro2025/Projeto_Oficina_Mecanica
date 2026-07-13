package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.RelatorioClienteDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioEstoqueDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioFinanceiroDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioOSDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioServiceImpl implements RelatorioService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    @Override
    public List<RelatorioOSDTO> relatorioOrdensServico() {

        return ordemServicoRepository.findAll()
                .stream()
                .map(this::converterOS)
                .toList();
    }

    @Override
    public RelatorioFinanceiroDTO relatorioFinanceiro() {

        List<OrdemServico> ordens = ordemServicoRepository.findAll();

        BigDecimal receitas = ordens.stream()
                .map(os -> os.getValorTotal() == null ? BigDecimal.ZERO : os.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RelatorioFinanceiroDTO.builder()
                .totalReceitas(receitas)
                .totalDespesas(BigDecimal.ZERO)
                .lucro(receitas)
                .quantidadeRecebimentos(ordens.size())
                .quantidadePagamentos(0)
                .build();
    }

    @Override
    public List<RelatorioEstoqueDTO> relatorioEstoque() {

        return produtoRepository.findAll()
                .stream()
                .map(this::converterProduto)
                .toList();
    }

    @Override
    public List<RelatorioClienteDTO> relatorioClientes() {

        return clienteRepository.findAll()
                .stream()
                .map(this::converterCliente)
                .toList();
    }

    // ==========================
    // MÉTODOS AUXILIARES
    // ==========================

    private RelatorioOSDTO converterOS(OrdemServico os) {

        return RelatorioOSDTO.builder()
                .numero(os.getNumero())
                .cliente(os.getCliente().getNome())
                .veiculo(os.getVeiculo().getMarca() + " " + os.getVeiculo().getModelo())
                .mecanico(os.getMecanico() != null ? os.getMecanico().getNome() : "")
                .status(os.getStatus().name())
                .dataAbertura(os.getDataAbertura())
                .dataFechamento(os.getDataFechamento())
                .valorTotal(os.getValorTotal())
                .build();
    }

    private RelatorioEstoqueDTO converterProduto(Produto produto) {

        return RelatorioEstoqueDTO.builder()
                .codigoProduto(produto.getCodigo())
                .descricao(produto.getDescricao())
                .estoqueAtual(produto.getEstoqueAtual())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .abaixoMinimo(produto.isEstoqueAbaixoMinimo())
                .build();
    }

    private RelatorioClienteDTO converterCliente(Cliente cliente) {

        return RelatorioClienteDTO.builder()
                .clienteId(cliente.getId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .quantidadeVeiculos(cliente.getVeiculos().size())
                .quantidadeOrdensServico(0)
                .build();
    }
}
