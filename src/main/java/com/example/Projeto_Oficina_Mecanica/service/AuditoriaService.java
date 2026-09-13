package com.example.Projeto_Oficina_Mecanica.service;

/**
 * Interface corrigida para bater com a AuditoriaServiceImpl real do projeto
 * (encontrada no upload mais recente). A versão anterior deste arquivo tinha
 * sido reconstruída sem acesso ao impl real e usava uma assinatura errada
 * (Usuario + 5 params, sem ip) — isso quebrava a compilação porque
 * AuditoriaServiceImpl.registrar(...) usa String (não Usuario) e tem um
 * parâmetro `ip` a mais.
 *
 * @param usuario    identificador do usuário (e-mail) que praticou a ação —
 *                    pode ser null em casos como falha de login
 * @param acao        ex.: "LOGIN", "LOGIN_FALHA", "CRIAR", "ATUALIZAR", "EXCLUIR"
 * @param entidade    nome da entidade afetada, ex.: "Usuario", "Cliente"
 * @param registroId  ID do registro afetado (pode ser null quando não se aplica)
 * @param descricao   descrição livre do que aconteceu
 * @param ip          IP de origem da requisição (pode ser null quando indisponível)
 */
public interface AuditoriaService {

    void registrar(
            String usuario,
            String acao,
            String entidade,
            Long registroId,
            String descricao,
            String ip
    );
}

