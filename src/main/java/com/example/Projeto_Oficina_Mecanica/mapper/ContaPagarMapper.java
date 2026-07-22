package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContaPagarMapper {

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    @Mapping(source = "fornecedor.razaoSocial", target = "fornecedorRazaoSocial")

    @Mapping(source = "notaFiscalEntrada.id", target = "notaFiscalEntradaId")
    @Mapping(source = "notaFiscalEntrada.numero", target = "numeroNotaFiscal")

    ContaPagarResponseDTO toResponseDTO(
            ContaPagar entity
    );

    List<ContaPagarResponseDTO> toResponseDTOList(
            List<ContaPagar> entities
    );

}
