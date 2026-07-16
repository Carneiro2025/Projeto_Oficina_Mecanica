package com.example.Projeto_Oficina_Mecanica.mapper;


import com.example.Projeto_Oficina_Mecanica.dto.response.ItemNotaFiscalEntradaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.NotaFiscalEntradaResponseDTO;

import com.example.Projeto_Oficina_Mecanica.entity.ItemNotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;



@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotaFiscalEntradaMapper {



    // ==========================================================
    // ENTITY -> RESPONSE DTO
    // ==========================================================


    @Mapping(
            target = "fornecedorId",
            source = "fornecedor.id"
    )
    @Mapping(
            target = "fornecedorRazaoSocial",
            source = "fornecedor.razaoSocial"
    )
    NotaFiscalEntradaResponseDTO toResponseDTO(
            NotaFiscalEntrada entity
    );



    // ==========================================================
    // ITEM ENTITY -> ITEM RESPONSE DTO
    // ==========================================================


    @Mapping(
            target = "produtoId",
            source = "produto.id"
    )
    @Mapping(
            target = "produtoDescricao",
            source = "produto.descricao"
    )
    ItemNotaFiscalEntradaResponseDTO itemToResponseDTO(
            ItemNotaFiscalEntrada item
    );



}
