package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository responsável pelo acesso aos dados da entidade Cliente.
 *
 * Funcionalidades:
 * - CRUD completo
 * - Busca por CPF/CNPJ
 * - Busca por e-mail
 * - Validação de existência
 * - Paginação
 * - Filtros dinâmicos
 * - Consultas para Dashboard
 *
 * Projeto Oficina Mecânica
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>,
        JpaSpecificationExecutor<Cliente> {

    /**
     * Busca um cliente pelo CPF ou CNPJ.
     *
     * @param cpfCnpj CPF ou CNPJ
     * @return Cliente encontrado
     */
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    /**
     * Verifica se já existe um cliente com o CPF/CNPJ informado.
     *
     * @param cpfCnpj CPF ou CNPJ
     * @return true caso exista
     */
    boolean existsByCpfCnpj(String cpfCnpj);

    /**
     * Busca um cliente pelo e-mail.
     *
     * @param email E-mail
     * @return Cliente encontrado
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verifica existência do e-mail.
     *
     * OBS:
     * O e-mail não precisa ser único,
     * mas este método pode ser utilizado
     * em validações futuras.
     */
    boolean existsByEmail(String email);

    /**
     * Lista apenas clientes ativos.
     */
    Page<Cliente> findByAtivoTrue(Pageable pageable);

    /**
     * Lista apenas clientes inativos.
     */
    Page<Cliente> findByAtivoFalse(Pageable pageable);

    /**
     * Lista todos os clientes ativos ordenados pelo nome.
     */
    List<Cliente> findAllByAtivoTrueOrderByNomeAsc();

    /**
     * Quantidade de clientes ativos.
     */
    long countByAtivoTrue();

    /**
     * Quantidade de clientes inativos.
     */
    long countByAtivoFalse();

    /**
     * Pesquisa clientes utilizando filtros opcionais.
     *
     * Todos os parâmetros são opcionais.
     */
    @Query("""
        SELECT c
        FROM Cliente c
        WHERE
            (:nome IS NULL
                OR LOWER(c.nome)
                LIKE LOWER(CONCAT('%', :nome, '%')))
        AND
            (:cpfCnpj IS NULL
                OR c.cpfCnpj = :cpfCnpj)
        AND
            (:ativo IS NULL
                OR c.ativo = :ativo)
        ORDER BY c.nome
        """)
    Page<Cliente> buscarComFiltros(

            @Param("nome")
            String nome,

            @Param("cpfCnpj")
            String cpfCnpj,

            @Param("ativo")
            Boolean ativo,

            Pageable pageable);

}