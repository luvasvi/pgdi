# PGDI — Plataforma de Gerenciamento de Documentos e Imagens

Sistema fullstack para armazenamento, organização e visualização de documentos e imagens, com controle de acesso por perfil (ADMIN/USER), filas de processamento e painel administrativo completo.

---

## Tecnologias

**Backend**
- Java 21 + Spring Boot 3.x
- Spring Security + JWT (jjwt)
- JPA/Hibernate + MySQL 8
- Argon2 (criptografia de senhas)
- Springdoc OpenAPI (Swagger)

**Frontend**
- Angular 21 + TypeScript
- RxJS / Signals
- PrimeNG

**Infraestrutura**
- Docker + Docker Compose
- Nginx (servidor do frontend)
- Volume Docker persistente para arquivos

---

## Como executar

### Pré-requisitos
- Docker Desktop instalado e rodando

### Passo a passo

**1. Clone o repositório**
```bash
git clone https://github.com/luvasvi/pgdi.git
cd pgdi
```

**2. Configure as variáveis de ambiente**
```bash
cp .env.example .env
```
Edite o `.env` e defina um valor seguro para `JWT_SECRET`.

**3. Suba os containers**
```bash
docker-compose up --build
```

**4. Acesse**
- Frontend: http://localhost
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html



## Funcionalidades

### USER
- Login e cadastro
- Upload de imagens (.jpg, .jpeg, .png)
- Visualização de filas não restritas
- Zoom, rotação e download de documentos
- Busca e paginação de documentos

### ADMIN
- Tudo que o USER faz
- CRUD de usuários (ativar/desativar)
- CRUD de tipos de documento
- CRUD de filas (com opção restrita)
- Gerenciar documentos (aprovar, reprovar, excluir)

---

## Arquitetura

### Backend
```
src/
├── config/       # SecurityConfig, DataSeedConfig, OpenApiConfig
├── controller/   # Endpoints REST
├── dto/          # Objetos de transferência de dados
├── infra/        # Tratamento global de exceções
├── mapper/       # Conversão entidade ↔ DTO
├── model/        # Entidades JPA
├── repository/   # Acesso ao banco de dados
├── security/     # Filtro JWT
└── service/      # Lógica de negócio
```

### Frontend
```
src/app/
├── guards/       # AuthGuard
├── interceptors/ # JWT Interceptor
├── pages/        # Telas (login, home, admin, etc.)
└── services/     # Serviços HTTP
```

---

## Decisões de arquitetura

- **Armazenamento de arquivos em volume Docker** em vez de BLOB no banco — melhor performance e escalabilidade.
- **Mapper separado do Service** — cada camada com responsabilidade única (Clean Code / SOLID).
- **Thumbnails gerados no upload** — preview rápido sem carregar a imagem original.
- **Paginação server-side** — evita carregar todos os registros de uma vez.
- **Argon2 para senhas** — algoritmo mais moderno e seguro que BCrypt.
- **JWT stateless** — sem estado no servidor, escalável horizontalmente.
- **Multi-stage build no Docker** — imagem de produção sem dependências de build, mais leve e segura.

---

## Endpoints principais

Documentação completa disponível em: http://localhost:8080/swagger-ui.html

| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| POST | /auth/login | Autenticação | Público |
| POST | /auth/register | Cadastro de usuário | ADMIN |
| GET | /documentos/buscar | Busca paginada | Autenticado |
| POST | /documentos/upload | Upload de imagem | Autenticado |
| GET | /documentos/arquivo/{id} | Download/visualização | Autenticado |
| GET | /documentos/arquivo/{id}/thumbnail | Thumbnail | Autenticado |
| PATCH | /documentos/{id}/status | Aprovar/Reprovar | ADMIN |
| DELETE | /documentos/{id} | Excluir documento | ADMIN |
| GET | /filas | Listar filas | Autenticado |
| GET | /usuarios | Listar usuários | ADMIN |

---