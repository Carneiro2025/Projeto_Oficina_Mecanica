package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.response.ItemOrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nome", target = "clienteNome")

    @Mapping(source = "veiculo.id", target = "veiculoId")
    @Mapping(source = "veiculo.placa", target = "placaVeiculo")
    @Mapping(source = "veiculo.modelo", target = "modeloVeiculo")

    OrdemServicoResponseDTO toResponseDTO(
            OrdemServico entity
    );

    List<OrdemServicoResponseDTO> toResponseDTOList(
            List<OrdemServico> entities
    );



    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.codigo", target = "codigoProduto")
    @Mapping(source = "produto.descricao", target = "descricaoProduto")
    ItemOrdemServicoResponseDTO toItemResponseDTO(
            ItemOrdemServico entity
    );

    List<ItemOrdemServicoResponseDTO> toItemResponseDTOList(
            List<ItemOrdemServico> entities
    );

}
