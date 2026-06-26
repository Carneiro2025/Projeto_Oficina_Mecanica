package com.example.Projeto_Oficina_Mecanica.mapper;

import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import org.mapstruct.*;

/**
 * Mapper MapStruct — converte entre Entity e DTOs automaticamente.
 * A implementação é gerada em tempo de compilação.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {

    // Entity → Response DTO (senha nunca é exposta)
    @Mapping(target = "id",        source = "id")
    @Mapping(target = "nome",      source = "nome")
    @Mapping(target = "email",     source = "email")
    @Mapping(target = "perfil",    source = "perfil")
    @Mapping(target = "ativo",     source = "ativo")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UsuarioResponseDTO toResponseDTO(Usuario usuario);

    // Request DTO → Entity (senha será criptografada no Service antes de salvar)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "ativo",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "senha",     source = "senha")   // será substituída no Service
    Usuario toEntity(CriarUsuarioRequestDTO dto);
}