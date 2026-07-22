package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.response.ContaReceberResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContaReceberMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nome", target = "clienteNome")

    @Mapping(source = "ordemServico.id", target = "ordemServicoId")
    @Mapping(source = "ordemServico.numero", target = "numeroOrdemServico")
    ContaReceberResponseDTO toResponseDTO(
            ContaReceber entity
    );

    List<ContaReceberResponseDTO> toResponseDTOList(
            List<ContaReceber> entities
    );

}
