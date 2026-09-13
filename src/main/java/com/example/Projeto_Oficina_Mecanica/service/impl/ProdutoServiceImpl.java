package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ProdutoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.ProdutoService;
import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de ProdutoServiceImplTest,
 * já que o ProdutoServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em criar().
 */
@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final FornecedorRepository fornecedorRepository;

    private final ProdutoMapper produtoMapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ProdutoResponseDTO criar(CriarProdutoRequestDTO dto) {

        if (produtoRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Já existe um produto cadastrado com este código.");
        }

        Produto produto = produtoMapper.toEntity(dto);
        produto.setAtivo(true);
        produto.setEstoqueAtual(0);

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", dto.getFornecedorId()));
            produto.setFornecedor(fornecedor);
        }

        Produto salvo = produtoRepository.save(produto);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "Produto",
                salvo.getId(),
                "Produto cadastrado: " + salvo.getCodigo(),
                obterIp()
        );

        return enriquecer(produtoMapper.toResponseDTO(salvo), salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = buscarEntidadePorId(id);
        return enriquecer(produtoMapper.toResponseDTO(produto), produto);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, AtualizarProdutoRequestDTO dto) {

        Produto produto = buscarEntidadePorId(id);

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", dto.getFornecedorId()));
            produto.setFornecedor(fornecedor);
        }

        produtoMapper.updateEntity(dto, produto);

        Produto salvo = produtoRepository.save(produto);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATUALIZAR",
                "Produto",
                salvo.getId(),
                "Produto atualizado: " + salvo.getCodigo(),
                obterIp()
        );

        return enriquecer(produtoMapper.toResponseDTO(salvo), salvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        Produto produto = buscarEntidadePorId(id);

        if (!produto.getAtivo()) {
            throw new BusinessException("Produto já está desativado.");
        }

        produto.setAtivo(false);
        produtoRepository.save(produto);

        auditoriaService.registrar(
                usuarioLogado(),
                "EXCLUIR",
                "Produto",
                produto.getId(),
                "Produto desativado: " + produto.getCodigo(),
                obterIp()
        );
    }

    @Override
    @Transactional
    public ProdutoResponseDTO reativar(Long id) {

        Produto produto = buscarEntidadePorId(id);

        if (produto.getAtivo()) {
            throw new BusinessException("Produto já está ativo.");
        }

        produto.setAtivo(true);

        Produto salvo = produtoRepository.save(produto);

        return enriquecer(produtoMapper.toResponseDTO(salvo), salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(String descricao, String codigo, CategoriaProduto categoria,
                                            Long fornecedorId, Pageable pageable) {
        return produtoRepository.buscarComFiltros(descricao, codigo, categoria, fornecedorId, pageable)
                .map(produto -> enriquecer(produtoMapper.toResponseDTO(produto), produto));
    }

    private Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }

    /**
     * Preenche os campos calculados (não vêm do mapper): margem de lucro
     * e sinalização de estoque abaixo do mínimo.
     */
    private ProdutoResponseDTO enriquecer(ProdutoResponseDTO responseDTO, Produto produto) {

        BigDecimal custo = produto.getPrecoCusto();
        BigDecimal venda = produto.getPrecoVenda();

        BigDecimal margem = BigDecimal.ZERO;
        if (custo != null && custo.compareTo(BigDecimal.ZERO) > 0 && venda != null) {
            margem = venda.subtract(custo)
                    .divide(custo, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        responseDTO.setMargemLucroPercent(margem);
        responseDTO.setEstoqueAbaixoMinimo(
                produto.getEstoqueAtual() != null
                        && produto.getEstoqueMinimo() != null
                        && produto.getEstoqueAtual() <= produto.getEstoqueMinimo()
        );

        return responseDTO;
    }

    private String usuarioLogado() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (principal instanceof Usuario) ? ((Usuario) principal).getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String obterIp() {
        try {
            var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            return (attrs != null) ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
