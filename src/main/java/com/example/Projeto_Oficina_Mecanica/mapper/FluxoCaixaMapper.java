package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.response.FluxoCaixaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FluxoCaixaMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nome", target = "clienteNome")

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    @Mapping(source = "fornecedor.razaoSocial", target = "fornecedorRazaoSocial")

    @Mapping(source = "ordemServico.id", target = "ordemServicoId")

    @Mapping(source = "contaReceber.id", target = "contaReceberId")

    @Mapping(source = "contaPagar.id", target = "contaPagarId")
    FluxoCaixaResponseDTO toResponseDTO(
            FluxoCaixa entity
    );

    List<FluxoCaixaResponseDTO> toResponseDTOList(
            List<FluxoCaixa> entities
    );

}


