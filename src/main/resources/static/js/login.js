// ============================================================
// script.js — Tela de login da Oficina Mecânica
// Integração com AuthController (POST /api/auth/login)
// ============================================================

let CONFIG = null;

async function carregarConfig() {
  const resp = await fetch('config.json');
  if (!resp.ok) throw new Error('Não foi possível carregar config.json');
  return resp.json();
}

function definirStatusApi(texto, cor) {
  const el = document.getElementById('statusApi');
  el.textContent = texto;
  el.style.color = cor;
}

async function verificarStatusApi() {
  try {
    // Troque por um endpoint de health real se tiver um (ex: /actuator/health)
    const resp = await fetch(`${CONFIG.apiBaseUrl}`, { method: 'GET' });
    if (resp.status < 500) {
      definirStatusApi('online', '#4caf7d');
    } else {
      definirStatusApi('instável', '#f2a93b');
    }
  } catch (e) {
    definirStatusApi('offline', '#e5533d');
  }
}

function mostrarErro(mensagem) {
  const el = document.getElementById('mensagemErro');
  el.textContent = mensagem;
  el.hidden = false;
}

function esconderErro() {
  const el = document.getElementById('mensagemErro');
  el.hidden = true;
  el.textContent = '';
}

function alternarCarregando(carregando) {
  const btn = document.getElementById('btnEntrar');
  const texto = btn.querySelector('.btn-entrar-texto');
  const spinner = btn.querySelector('.btn-entrar-spinner');

  btn.disabled = carregando;
  spinner.hidden = !carregando;
  texto.textContent = carregando ? 'ENTRANDO...' : 'ENTRAR';
}

async function realizarLogin(login, senha) {
  const { campoUsuario, campoSenha } = CONFIG.camposLoginRequestDTO;

  const corpo = {
    [campoUsuario]: login,
    [campoSenha]: senha,
  };

  const resposta = await fetch(`${CONFIG.apiBaseUrl}${CONFIG.endpoints.login}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(corpo),
  });

  if (resposta.status === 401) {
    throw new Error('Usuário ou senha inválidos.');
  }
  if (resposta.status === 400) {
    throw new Error('Dados inválidos. Confira o usuário e a senha informados.');
  }
  if (!resposta.ok) {
    throw new Error('Não foi possível entrar. Tente novamente em instantes.');
  }

  return resposta.json(); // TokenResponseDTO
}

function salvarTokens(dadosToken) {
  const { campoAccessToken, campoRefreshToken } = CONFIG.camposTokenResponseDTO;
  const { chaveAccessToken, chaveRefreshToken } = CONFIG.storage;

  const accessToken = dadosToken[campoAccessToken];
  const refreshToken = dadosToken[campoRefreshToken];

  if (accessToken) localStorage.setItem(chaveAccessToken, accessToken);
  if (refreshToken) localStorage.setItem(chaveRefreshToken, refreshToken);
}

function inicializarAlternarSenha() {
  const btn = document.getElementById('btnMostrarSenha');
  const input = document.getElementById('senha');

  btn.addEventListener('click', () => {
    const visivel = input.type === 'text';
    input.type = visivel ? 'password' : 'text';
    btn.setAttribute('aria-label', visivel ? 'Mostrar senha' : 'Ocultar senha');
  });
}

function inicializarFormulario() {
  const form = document.getElementById('formLogin');

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    esconderErro();

    const login = document.getElementById('login').value.trim();
    const senha = document.getElementById('senha').value;

    if (!login || !senha) {
      mostrarErro('Preencha usuário e senha para continuar.');
      return;
    }

    alternarCarregando(true);

    try {
      const dadosToken = await realizarLogin(login, senha);
      salvarTokens(dadosToken);

      // Ajuste para a rota real do seu painel interno:
      window.location.href = 'dashboard.html';
    } catch (erro) {
      mostrarErro(erro.message);
    } finally {
      alternarCarregando(false);
    }
  });
}

(async function iniciar() {
  try {
    CONFIG = await carregarConfig();
  } catch (e) {
    CONFIG = {
      apiBaseUrl: 'http://localhost:8080/api',
      endpoints: { login: '/auth/login', refresh: '/auth/refresh', logout: '/auth/logout' },
      camposLoginRequestDTO: { campoUsuario: 'login', campoSenha: 'senha' },
      camposTokenResponseDTO: { campoAccessToken: 'accessToken', campoRefreshToken: 'refreshToken' },
      storage: { chaveAccessToken: 'oficina_access_token', chaveRefreshToken: 'oficina_refresh_token' },
    };
  }

  inicializarAlternarSenha();
  inicializarFormulario();
  verificarStatusApi();
})();
