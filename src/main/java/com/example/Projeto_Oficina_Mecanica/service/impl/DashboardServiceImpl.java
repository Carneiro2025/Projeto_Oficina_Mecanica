package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.DashboardDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ProdutoRepository produtoRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public DashboardDTO obterDashboard() {

        DashboardDTO dto = new DashboardDTO();

        dto.setTotalClientes(clienteRepository.count());

        dto.setTotalVeiculos(veiculoRepository.count());

        dto.setTotalProdutos(produtoRepository.count());

        dto.setOrdensAbertas(
                ordemServicoRepository.countByStatus(
                        StatusOrdemServico.ABERTA
                )
        );

        dto.setOrdensFinalizadas(
                ordemServicoRepository.countByStatus(
                        StatusOrdemServico.FINALIZADA
                )
        );

        dto.setOrdensCanceladas(
                ordemServicoRepository.countByStatus(
                        StatusOrdemServico.CANCELADA
                )
        );

        return dto;
    }
}