# Sprint — Fechamento de Pendências (OficinaPRO)

**Objetivo da sprint:** eliminar os gaps identificados entre o que o projeto "deveria" ter (segundo o planejamento original) e o que está de fato implementado, priorizando o que dá mais valor com menos risco primeiro.

**Ordem sugerida:** Auditoria → Sprint 2 (Controllers) → Dashboard → Relatórios → Sprint 3 (Repository) → Backup → Front-end.

---

## Fase 1 — Auditoria (conectar o que já existe)
Infraestrutura pronta (`AuditoriaServiceImpl.registrar()`), só falta ser chamada.

- [x] Login (sucesso e falha) — `AuthServiceImpl`
- [x] Cadastro de registros — `criar()` dos services principais (Cliente, Veículo, Fornecedor, Produto, Usuário)
- [x] Alterações — `atualizar()` dos mesmos services
- [x] Exclusões / desativações — `desativar()` / `excluir()`
- [x] Pagamentos — `ContaPagarServiceImpl.registrarPagamento()`
- [x] Recebimentos — `ContaReceberServiceImpl.registrarPagamento()`
- [x] OS finalizada — `OrdemServicoServiceImpl.finalizar()`

**Critério de pronto:** cada ação relevante gera uma linha em `Auditoria` com usuário, ação, entidade, ID do registro e descrição.

---

## Fase 2 — Sprint 2: Testes de Controller (MockMvc)
Já prontos: `ClienteControllerTest`, `AuthControllerTest`.

- [ ] UsuarioControllerTest
- [ ] VeiculoControllerTest
- [ ] FornecedorControllerTest
- [ ] ProdutoControllerTest
- [ ] OrdemServicoControllerTest
- [ ] ContaPagarControllerTest (equivalente ao "Pagamento" do plano original)
- [ ] RecebimentoControllerTest
- [ ] DashboardControllerTest

Fora do plano original, mas existem no projeto e ficariam sem cobertura se quiser 100%:
- [ ] ContaReceberControllerTest
- [ ] FluxoCaixaControllerTest
- [ ] RelatorioControllerTest
- [ ] EstoqueControllerTest
- [ ] ServicoRealizadoControllerTest

---

## Fase 3 — Dashboard Gerencial (~40% → 100%)
Hoje só conta totais (clientes, veículos, produtos, OS por status). Faltam:

- [ ] Receita do mês
- [ ] Despesas do mês
- [ ] Lucro (receita − despesas)
- [ ] OS abertas / concluídas (já existe, só confirmar)
- [ ] Produtos com estoque baixo (usar `estoqueAbaixoMinimo` que já existe no `ProdutoResponseDTO`)
- [ ] Contas vencidas (Pagar + Receber, `dataVencimento < hoje` e status ≠ PAGO)
- [ ] Fluxo diário (últimos N dias, a partir de `FluxoCaixa`)

---

## Fase 4 — Relatórios
Preciso abrir o `RelatorioServiceImpl`/`RelatorioController` reais para avaliar o que já existe antes de planejar essa fase em detalhe. Escopo alvo do plano original:

- [ ] Financeiro
- [ ] Fluxo de Caixa
- [ ] Ordem de Serviço
- [ ] Estoque
- [ ] Clientes
- [ ] Fornecedores
- [ ] Produtos
- [ ] Faturamento

---

## Fase 5 — Sprint 3: Testes de Repository (H2)
Ainda não detalhado com você quais repositórios entram. Sugestão de escopo mínimo: os repositórios com queries customizadas (`buscarComFiltros`, `findByStatus`, `existsByX`, etc.) — são os que mais valem teste, já que o CRUD puro do Spring Data já é testado pelo próprio framework.

- [ ] Definir lista final de repositórios a cobrir
- [ ] Escrever os testes com `@DataJpaTest` + H2

---

## Fase 6 — Backup
- [ ] Definir estratégia (dump agendado do banco, endpoint manual, etc. — a definir com você)
- [ ] Implementar rotina

---

## Fase 7 — Front-end
- [ ] Criar `estoque.html`
- [ ] Criar `financeiro.html`
- [ ] Criar `usuarios.html`
- [ ] Criar `relatorios.html`
- [ ] Corrigir `os.html`: criar `css/layout.css` e `css/os.css` (ou remover as referências, se não forem necessárias)

---

## Itens já concluídos (referência)
- ✅ Integração Nota Fiscal → Conta a Pagar (automática)
- ✅ Integração OS → Conta a Receber (automática, ao finalizar)
- ✅ Bug do `GlobalExceptionHandler` (404 em vez de 500 para `ResourceNotFoundException`)
- ✅ Sprint 1 completo (12 testes de service)
- ✅ Diversos bugs de front-end corrigidos (login→dashboard, Toast ausente, CSS de menu sobreposto, caminhos de script quebrados)
