package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.entity.Auditoria;
import com.example.Projeto_Oficina_Mecanica.repository.AuditoriaRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository repository;

    @Override
    public void registrar(
            String usuario,
            String acao,
            String entidade,
            Long registroId,
            String descricao,
            String ip
    ) {

        Auditoria auditoria = Auditoria.builder()

                .usuario(usuario)

                .acao(acao)

                .entidade(entidade)

                .registroId(registroId)

                .descricao(descricao)

                .ip(ip)

                .build();

        repository.save(auditoria);

        log.info(
                "[AUDITORIA] {} | {} | {} | {}",
                usuario,
                acao,
                entidade,
                descricao
        );
    }
}
