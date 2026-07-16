package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    /**
     * Converte DTO de criação para Entity.
     *
     * O cliente será atribuído no Service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Veiculo toEntity(CriarVeiculoRequestDTO dto);

    /**
     * Entity → ResponseDTO
     */
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nome", target = "clienteNome")
    VeiculoResponseDTO toResponseDTO(Veiculo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(
        AtualizarVeiculoRequestDTO dto,
        @MappingTarget Veiculo entity);

}
