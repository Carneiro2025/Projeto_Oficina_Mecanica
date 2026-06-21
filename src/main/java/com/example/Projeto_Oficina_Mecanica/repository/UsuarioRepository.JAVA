package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Usuario> findByAtivoTrue(Pageable pageable);

    Page<Usuario> findByPerfilAndAtivoTrue(
            PerfilUsuario perfil,
            Pageable pageable
    );

    @Query("""
        SELECT u
        FROM Usuario u
        WHERE u.ativo = true
        AND (u.nome LIKE %:nome% OR u.email LIKE %:nome%)
        AND u.perfil = :perfil
    """)
    Page<Usuario> buscarConFiltros(
            @Param("nome") String nome,
            @Param("perfil") PerfilUsuario perfil,
            Pageable pageable
    );
}