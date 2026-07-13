package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClienteMapper {

    /**
     * Converte DTO de criação para Entidade.
     */
    Cliente toEntity(CriarClienteRequestDTO dto);

    /**
     * Converte Entidade para DTO de resposta.
     */
    @Mapping(
            target = "quantidadeVeiculos",
            expression = "java(cliente.getVeiculos() != null ? cliente.getVeiculos().size() : 0)"
    )
    ClienteResponseDTO toResponseDTO(Cliente cliente);

    /**
     * Atualiza apenas os campos enviados.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(
            AtualizarClienteRequestDTO dto,
            @MappingTarget Cliente entity
    );

}