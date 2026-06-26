package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.RelatorioClienteDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioEstoqueDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioFinanceiroDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioOSDTO;

import java.util.List;

public interface RelatorioService {

    List<RelatorioOSDTO> relatorioOrdensServico();

    RelatorioFinanceiroDTO relatorioFinanceiro();

    List<RelatorioEstoqueDTO> relatorioEstoque();

    List<RelatorioClienteDTO> relatorioClientes();

}
