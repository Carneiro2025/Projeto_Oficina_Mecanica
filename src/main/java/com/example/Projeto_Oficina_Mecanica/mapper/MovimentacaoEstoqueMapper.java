package com.example.Projeto_Oficina_Mecanica.mapper;


import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.MovimentacaoEstoqueResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.MovimentacaoEstoque;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovimentacaoEstoqueMapper {



    /**
     * Request DTO -> Entity
     */
    @Mapping(
            target = "produto",
            ignore = true
    )
    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "dataMovimentacao",
            ignore = true
    )
    MovimentacaoEstoque toEntity(
            CriarMovimentacaoEstoqueRequestDTO dto
    );



    /**
     * Entity -> Response DTO
     */
    @Mapping(
            target = "produtoId",
            source = "produto.id"
    )
    @Mapping(
            target = "produtoDescricao",
            source = "produto.descricao"
    )
    MovimentacaoEstoqueResponseDTO toResponseDTO(
            MovimentacaoEstoque entity
    );

}
