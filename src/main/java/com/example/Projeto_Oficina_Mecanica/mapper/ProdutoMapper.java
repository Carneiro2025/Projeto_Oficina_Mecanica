package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProdutoMapper {

    /**
     * DTO -> Entity
     */
    Produto toEntity(CriarProdutoRequestDTO dto);

    /**
     * Entity -> DTO
     */
    @Mapping(target = "fornecedorId",
            source = "fornecedor.id")

    @Mapping(target = "fornecedorRazaoSocial",
            source = "fornecedor.razaoSocial")

    ProdutoResponseDTO toResponseDTO(Produto entity);

    /**
     * Atualização parcial
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            AtualizarProdutoRequestDTO dto,
            @MappingTarget Produto entity);

}
