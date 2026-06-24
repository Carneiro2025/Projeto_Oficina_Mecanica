# 🔧 Sistema de Gestão de Oficina Mecânica - Guia de Configuração

> **Versão 3.0** | Spring Boot 3.x | Java 17+ | MySQL 8.x

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Instalação](#instalação)
4. [Configuração por Ambiente](#configuração-por-ambiente)
5. [Variáveis de Ambiente](#variáveis-de-ambiente)
6. [Docker & Docker Compose](#docker--docker-compose)
7. [Desenvolvimento Local](#desenvolvimento-local)
8. [Testes](#testes)
9. [Produção](#produção)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Visão Geral

Este projeto usa **Spring Boot 3.x** com configuração baseada em **profiles** (dev, test, prod). Os arquivos de configuração suportam:

- **application.properties** - Configuração base (padrão)
- **application.yml** - Alternativa em YAML (mais legível)
- **application-dev.properties** - Desenvolvimento local
- **application-test.properties** - Testes automatizados
- **application-prod.properties** - Produção
- **.env** - Variáveis de ambiente (não committar!)

---

## ⚙️ Pré-requisitos

### Sistema
- **Java 17+** LTS (OpenJDK ou Oracle)
- **Maven 3.9+** (ou use `./mvnw` incluído)
- **MySQL 8.x** (ou Docker)
- **Git**

### Ferramentas Opcionais
- **Docker** e **Docker Compose**
- **Postman** ou **Insomnia** (para testar APIs)
- **IDE**: IntelliJ IDEA, VS Code ou Eclipse

### Verificar Instalação bash
java -version          # Deve ser Java 17+
mvn -version           # Deve ser Maven 3.9+
mysql --version        # MySQL 8.x
docker --version       # Opcional mas recomendado
docker-compose --version  # Opcional mas recomendado
---

## 📦 Instalação

### 1. Clonar Repositório
git clone https://seu-repo/oficina-mecanica.git
cd oficina-mecanica

### 2. Configurar Variáveis de Ambiente 
# Copiar template e editar
cp .env.example .env

# Abrir e editar com seus valores
nano .env
# ou
code .env  # VS Code

### 3. Compilar Projeto 
# Com Maven
mvn clean install

# Ou com wrapper incluído
./mvnw clean install


### 4. Executar Aplicação
# Desenvolvimento
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Ou
java -jar target/oficina-mecanica-*.jar --spring.profiles.active=dev

# Teste
mvn test --spring.profiles.active=test

# Produção (não use diretamente em prod!)
java -jar target/oficina-mecanica-*.jar --spring.profiles.active=prod

---

## 🌍 Configuração por Ambiente

### Desenvolvimento (dev)

**Características:**
- ✅ Hot reload com DevTools
- ✅ SQL e queries visíveis
- ✅ Debug logging detalhado
- ✅ Swagger habilitado
- ✅ CORS permissivo
- ✅ DDL automático (update)

**Ativar:** 
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

**Banco de dados padrão:**

URL: jdbc:mysql://localhost:3306/oficina_mecanica_dev
User: dev_user
Password: dev_password

**Acessar:**
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html
- Actuator: http://localhost:8080/actuator

### Testes (test)

**Características:**
- ✅ Banco em memória (H2) - rápido
- ✅ Sem dependências externas
- ✅ Reset automático após cada teste
- ✅ Logging mínimo
- ✅ Perfect para CI/CD

**Ativar:**

mvn test -Dspring.profiles.active=test

# Ou rodar suite específica
mvn test -Dtest=ClienteServiceTest


**H2 Console (se habilitado):**
- http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`

### Produção (prod)

**Características:**
- ✅ Segurança máxima
- ✅ CORS restritivo
- ✅ Logging mínimo (em arquivo)
- ✅ Cache habilitado
- ✅ Swagger desabilitado
- ✅ Validações rígidas
- ✅ Backup automático

**Ativar:**

export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://seu-host:3306/oficina_mecanica?...
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha_forte
export JWT_SECRET=sua_chave_secreta_minimo_32_caracteres

java -jar oficina-mecanica-3.0.jar

**Variáveis obrigatórias em produção:**
- DB_URL - Conexão MySQL
- DB_USER - Usuário banco
- DB_PASSWORD - Senha banco (usar secret manager!)
- JWT_SECRET - Chave JWT (usar secret manager!)
- CORS_ORIGINS - Domínios permitidos

---

## 🔐 Variáveis de Ambiente

### Gerar Chave JWT Segura

# Linux/Mac
openssl rand -base64 32

# Resultado (exemplo):
# rKkL9QmXz/x7vVnJ0pY3qA1bZcFw2sT9mHjLqUiOp5s=

### .env Completo (Exemplo Desenvolvimento)

# Perfil
SPRING_PROFILES_ACTIVE=dev

# Banco
DB_URL=jdbc:mysql://localhost:3306/oficina_mecanica_dev?serverTimezone=America/Recife&characterEncoding=UTF-8
DB_USER=dev_user
DB_PASSWORD=dev_password

# JWT
JWT_SECRET=sua_chave_jwt_aqui_minimo_32_caracteres

# CORS
CORS_ORIGINS=http://localhost:3000,http://localhost:4200

# Email (opcional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-app-google

### Carregar .env automaticamente

# Opção 1: Via export
export $(cat .env | xargs)

# Opção 2: Com Spring Boot Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.config.location=file:.env"

# Opção 3: Com Docker (automático)
docker-compose up

---

## 🐳 Docker & Docker Compose

### Executar Completo com Docker Compose

# 1. Copiar e configurar .env
cp .env.example .env
nano .env

# 2. Subir tudo
docker-compose up -d

# 3. Verificar saúde
docker-compose ps
docker-compose logs -f app

# 4. Acessar
# API: http://localhost:8080/api
# Swagger: http://localhost:8080/api/swagger-ui.html
# phpMyAdmin: http://localhost:8081
# Usuário: admin / Senha: app_password

### Build Manual da Imagem

# Build
docker build -t oficina-mecanica:3.0 .

# Run
docker run -p 8080:8080 \
  --env SPRING_PROFILES_ACTIVE=dev \
  --env DB_URL=jdbc:mysql://host.docker.internal:3306/oficina_mecanica \
  --env DB_USER=dev_user \
  --env DB_PASSWORD=dev_password \
  oficina-mecanica:3.0

### Troubleshoot Docker

# Logs da app
docker-compose logs -f app

# Logs do MySQL
docker-compose logs -f mysql

# Entrar no container
docker exec -it oficina-api bash
docker exec -it oficina-mysql bash

# Resetar banco
docker-compose down -v
docker-compose up -d

---

## 💻 Desenvolvimento Local (SEM Docker)

### 1. Instalar MySQL Localmente

# macOS (Homebrew)
brew install mysql@8.0
brew services start mysql@8.0

# Ubuntu/Debian
sudo apt-get install mysql-server-8.0
sudo systemctl start mysql

# Windows (Chocolatey)
choco install mysql

# Windows (Manual): https://dev.mysql.com/downloads/mysql/

### 2. Criar Banco de Dados

mysql -u root -p

# No MySQL prompt:
CREATE DATABASE oficina_mecanica_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dev_user'@'localhost' IDENTIFIED BY 'dev_password';
GRANT ALL PRIVILEGES ON oficina_mecanica_dev.* TO 'dev_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

### 3. Rodar Aplicação

# Terminal 1: MySQL
mysql -u dev_user -p oficina_mecanica_dev

# Terminal 2: Aplicação
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

### 4. Verificar

# Health check
curl http://localhost:8080/api/actuator/health

# Swagger
open http://localhost:8080/api/swagger-ui.html

---

## 🧪 Testes

### Executar Testes

# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=ClienteServiceTest

# Com cobertura JaCoCo
mvn clean test jacoco:report

# Relatório gerado em:
# target/site/jacoco/index.html

### Cobertura Esperada

Mínimo: 80% (RNF08)
Meta: 90%+

Classes: src/test/java/com/oficina
- *ServiceTest
- *ControllerTest
- *RepositoryTest

### SonarQube (Análise Estática)

# Executar análise
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=oficina-mecanica \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=seu_token

# Acessar dashboard
# http://localhost:9000

---

## 🚀 Produção

### Checklist Pré-Deploy

- [ ] Todas as variáveis de ambiente configuradas
- [ ] JWT_SECRET com chave forte (openssl rand -base64 32)
- [ ] SSL/HTTPS habilitado
- [ ] Banco de dados em servidor dedicado
- [ ] Backup automático configurado (cron)
- [ ] Logs em local persistente
- [ ] Monitoramento ativo (APM)
- [ ] Cobertura de testes > 80%
- [ ] SonarQube: 0 bugs críticos
- [ ] CORS restringido ao domínio

### Deploy com Docker

# 1. Build
docker build -t oficina-mecanica:3.0 .

# 2. Tag para registry
docker tag oficina-mecanica:3.0 seu-registry/oficina-mecanica:3.0

# 3. Push
docker push seu-registry/oficina-mecanica:3.0

# 4. Pull e rodar em produção
docker pull seu-registry/oficina-mecanica:3.0
docker run -d \
  --name oficina \
  -p 8080:8080 \
  --env-file /etc/oficina/.env.prod \
  --restart=always \
  seu-registry/oficina-mecanica:3.0

### Deploy com systemd (Linux)

# 1. Copiar JAR
sudo cp target/oficina-mecanica-3.0.jar /opt/oficina/

# 2. Criar serviço
sudo tee /etc/systemd/system/oficina.service <<EOF
[Unit]
Description=Oficina Mecanica API
After=network.target

[Service]
Type=simple
User=oficina
WorkingDirectory=/opt/oficina
EnvironmentFile=/etc/oficina/.env.prod
ExecStart=/usr/bin/java -jar oficina-mecanica-3.0.jar
Restart=on-failure
RestartSec=30s

[Install]
WantedBy=multi-user.target
EOF

# 3. Ativar
sudo systemctl daemon-reload
sudo systemctl enable oficina
sudo systemctl start oficina

# 4. Verificar status
sudo systemctl status oficina
sudo journalctl -u oficina -f

## 🆘 Troubleshooting

### Erro: "Unable to determine a suitable driver class"

# Causa: Driver MySQL não encontrado
# Solução: Verificar pom.xml tem mysql-connector-java ou mysql-connector-j
mvn dependency:resolve | grep mysql


### Erro: "Connection refused" (Banco)

# Causa: MySQL não está rodando
# Solução:
sudo systemctl restart mysql
# ou
docker-compose restart mysql

### Erro: "JWT token invalid"

# Causa: JWT_SECRET diferente entre aplicações
# Solução: Usar mesmo JWT_SECRET em todos os ambientes
export JWT_SECRET=$(cat .env | grep JWT_SECRET | cut -d= -f2)

### Erro: "Port 8080 already in use"

# Solução 1: Matar processo
kill -9 $(lsof -i :8080 | grep LISTEN | awk '{print $2}')

# Solução 2: Usar outra porta
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

### Logs muito grandes

# Limpar
rm -rf logs/*

# Configurar rotação (já está no application.properties)
# logging.file.max-size=10MB
# logging.file.max-history=30

### H2 Console não abre

# Verificar se está habilitado em application-test.properties
# spring.h2.console.enabled=true
# Acessar: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb

---

## 📚 Referências

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Springdoc OpenAPI**: https://springdoc.org/
- **JWT JJWT**: https://github.com/jwtk/jjwt
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Docker**: https://docs.docker.com/

---
## 📧 Contato & Suporte

Para dúvidas sobre configuração ou bugs:
1. Verificar logs: `logs/oficina-mecanica.log`
2. Consultar documentação Swagger: `/api/swagger-ui.html`
3. Abrir issue no repositório

---

**Última atualização**: Junho 2026  
**Versão**: 3.0  
**Status**: ✅ Production Ready
