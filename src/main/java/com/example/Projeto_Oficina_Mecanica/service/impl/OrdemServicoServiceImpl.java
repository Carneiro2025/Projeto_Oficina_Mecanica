package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AdicionarItemOSRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.OrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Mecanico;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.entity.ServicoRealizado;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ItemOrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.MecanicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ServicoRealizadoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRealizadoRepository servicoRepository;
    private final ItemOrdemServicoRepository itemRepository;

    @Override
    public OrdemServicoResponseDTO abrir(OrdemServicoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        Mecanico mecanico = null;

        if (dto.getMecanicoId() != null) {
            mecanico = mecanicoRepository.findById(dto.getMecanicoId())
                    .orElseThrow(() -> new RuntimeException("Mecânico não encontrado"));
        }

        Long numeroOS = System.currentTimeMillis();

        OrdemServico os = OrdemServico.builder()
                .numero(numeroOS)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .problemaRelatado(dto.getProblemaRelatado())
                .observacoes(dto.getObservacoes())
                .status(StatusOrdemServico.ABERTA)
                .dataAbertura(LocalDateTime.now())
                .build();

        os = ordemServicoRepository.save(os);

        return converter(os);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(Long id) {

        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        return converter(os);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listar() {

        return ordemServicoRepository.findAll()
                .stream()
                .map(this::converter)
                .toList();
    }

    @Override
    public OrdemServicoResponseDTO finalizar(Long id) {

        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        os.setStatus(StatusOrdemServico.FINALIZADA);
        os.setDataFechamento(LocalDateTime.now());

        ordemServicoRepository.save(os);

        return converter(os);
    }

    @Override
    public void cancelar(Long id) {

        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        os.setStatus(StatusOrdemServico.CANCELADA);

        ordemServicoRepository.save(os);
    }

    private OrdemServicoResponseDTO converter(OrdemServico os) {

        return OrdemServicoResponseDTO.builder()
                .id(os.getId())
                .numero(os.getNumero())
                .cliente(os.getCliente().getNome())
                .veiculo(
                        os.getVeiculo().getMarca()
                        + " "
                        + os.getVeiculo().getModelo()
                )
                .mecanico(
                        os.getMecanico() != null
                                ? os.getMecanico().getNome()
                                : null
                )
                .status(os.getStatus())
                .dataAbertura(os.getDataAbertura())
                .dataFechamento(os.getDataFechamento())
                .valorTotal(os.getValorTotal())
                .build();
    }

     
     private void recalcularTotalOS(OrdemServico os) {

    os.setValorTotal(
            os.getItens()
                    .stream()
                    .map(ItemOrdemServico::getSubtotal)
                    .reduce(
                            java.math.BigDecimal.ZERO,
                            java.math.BigDecimal::add
                    )
    );
   
    }


    @Override
    public OrdemServicoResponseDTO adicionarItem(Long ordemServicoId, AdicionarItemOSRequestDTO dto) {

    OrdemServico os = ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("OS não encontrada"));

    ItemOrdemServico item = new ItemOrdemServico();

    item.setOrdemServico(os);

    if (dto.getProdutoId() != null) {

        Produto produto = produtoRepository
                .findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getEstoqueAtual() < dto.getQuantidade()) {
            throw new RuntimeException(
                    "Estoque insuficiente"
            );
        }

        produto.setEstoqueAtual(
                produto.getEstoqueAtual()
                        - dto.getQuantidade()
        );

        produtoRepository.save(produto);

        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setValorUnitario(produto.getPrecoVenda());

        item.calcularSubtotal();
    }

    if (dto.getServicoId() != null) {

        ServicoRealizado servico =
                servicoRepository.findById(dto.getServicoId())
                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        item.setServico(servico);
        item.setQuantidade(1);
        item.setValorUnitario(servico.getValorPadrao());

        item.calcularSubtotal();
    }

    itemRepository.save(item);

    os.getItens().add(item);

    recalcularTotalOS(os);

    ordemServicoRepository.save(os);

    return converter(os);
}
 

}
