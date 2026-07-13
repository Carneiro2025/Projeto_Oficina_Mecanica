package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.EnderecoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Endereco;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EnderecoMapper {

    /**
     * Converte DTO de entrada para entidade.
     */
    Endereco toEntity(EnderecoRequestDTO dto);

    /**
     * Converte entidade para DTO de saída.
     */
    EnderecoResponseDTO toResponseDTO(Endereco endereco);

    /**
     * Atualiza apenas os campos enviados.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(
            EnderecoRequestDTO dto,
            @MappingTarget Endereco entity
    );
}