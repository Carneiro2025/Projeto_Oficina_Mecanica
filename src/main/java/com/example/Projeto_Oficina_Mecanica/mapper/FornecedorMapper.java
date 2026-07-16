package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.Endereco;
import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FornecedorMapper {

    /**
     * Converte DTO de criação em entidade.
     */
    Fornecedor toEntity(CriarFornecedorRequestDTO dto);

    /**
     * Converte entidade em DTO de resposta.
     */
    FornecedorResponseDTO toResponseDTO(Fornecedor entity);

    Endereco toEndereco(EnderecoResponseDTO dto);

    EnderecoResponseDTO toEnderecoResponseDTO(Endereco entity);

    /**
     * Atualiza apenas os campos enviados.
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            AtualizarFornecedorRequestDTO dto,
            @MappingTarget Fornecedor entity);

}
