# Seasko

Sistema completo de gerenciamento de agendamentos para estúdios (salão de beleza, barbearia, etc.).

Composto por uma **API REST Spring Boot** e um **frontend SPA** (HTML/CSS/JS puro) servido pela própria aplicação.

---

## Funcionalidades

- Autenticação via HTTP Basic Auth com senhas BCrypt
- Dois níveis de acesso: `ADMIN` e `EMPLOYEE`
- CRUD completo de **usuários**, **clientes** e **agendamentos**
- Filtros de agendamentos por data, mês, ano, cliente, usuário e status
- Paginação nas listagens de agendamentos e clientes (25 itens por página)
- Atualização isolada de status de agendamento
- Tratamento centralizado de erros com respostas JSON padronizadas
- Frontend SPA com dark mode, skeleton loading e modal de confirmação

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL rodando localmente

---

## Configuração

As credenciais do banco são lidas de variáveis de ambiente. Defina-as antes de executar:

```bash
export PGHOST=localhost
export PGPORT=5432
export PGDATABASE=agilo
export PGUSER=<seu_usuario>
export PGPASSWORD=<sua_senha>
```

O banco de dados e as tabelas são criados automaticamente pelo Hibernate na primeira execução.

---

## Executando

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

O frontend é acessado diretamente em `http://localhost:8080/` — a tela de login abre automaticamente.

**Usuário admin criado automaticamente no primeiro startup:**
- `usuario`: `admin`
- `senha`: `admin123`

---

## Autenticação

Todas as rotas (exceto `/`, `/index.html` e `/public/**`) exigem autenticação Basic Auth.

Exemplo com curl:

```bash
curl -u admin:admin123 http://localhost:8080/usuario
```

---

## Endpoints

### Usuários — `/usuario`

> Operações de escrita (`POST`, `PUT`, `DELETE`) exigem role `ADMIN`.

| Método | Path                      | Descrição     |
|--------|---------------------------|---------------|
| GET    | `/usuario`                | Listar todos  |
| GET    | `/usuario/{id}`           | Buscar por ID |
| POST   | `/usuario/registrar`      | Criar         |
| PUT    | `/usuario/atualizar/{id}` | Atualizar     |
| DELETE | `/usuario/deletar/{id}`   | Remover       |

**UsuarioDtoRequest:**
```json
{
  "nome": "Maria Silva",
  "usuario": "maria",
  "senha": "senha123",
  "role": "EMPLOYEE"
}
```

**UsuarioDtoResponse:**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "usuario": "maria",
  "role": "EMPLOYEE",
  "ativo": true
}
```

---

### Clientes — `/cliente`

> `DELETE` exige role `ADMIN`.

| Método | Path                      | Descrição         | Query params   |
|--------|---------------------------|-------------------|----------------|
| GET    | `/cliente`                | Listar (paginado) | `page`, `size`, `busca` |
| GET    | `/cliente/{id}`           | Buscar por ID     | —              |
| POST   | `/cliente/inserir`        | Criar             | —              |
| PUT    | `/cliente/atualizar/{id}` | Atualizar         | —              |
| DELETE | `/cliente/deletar/{id}`   | Remover           | —              |

`page` default `0` · `size` default `25` · `busca` filtra por nome ou telefone (parcial, case-insensitive) · ordenado por `nome` asc.

**ClienteDtoRequest:**
```json
{
  "nome": "João Pereira",
  "telefone": "11999990000",
  "anotacao": "Prefere atendimento às manhãs"
}
```

**Resposta paginada (`GET /cliente`):**
```json
{
  "content": ["..."],
  "page": 0,
  "size": 25,
  "totalElements": 42,
  "totalPages": 2
}
```

---

### Agendamentos — `/agendamento`

> `DELETE` exige role `ADMIN`.

| Método | Path                          | Descrição         |
|--------|-------------------------------|-------------------|
| GET    | `/agendamento`                | Listar (paginado) |
| GET    | `/agendamento/{id}`           | Buscar por ID     |
| POST   | `/agendamento/inserir`        | Criar             |
| PUT    | `/agendamento/atualizar/{id}` | Atualizar         |
| PUT    | `/agendamento/status/{id}`    | Atualizar status  |
| DELETE | `/agendamento/deletar/{id}`   | Remover           |

**Query params disponíveis no `GET /agendamento`:**

| Param       | Tipo         | Descrição              |
|-------------|--------------|------------------------|
| `page`      | int          | Página (default `0`)   |
| `size`      | int          | Tamanho (default `25`) |
| `data`      | `YYYY-MM-DD` | Data exata             |
| `mes`       | int (1–12)   | Mês                    |
| `ano`       | int          | Ano                    |
| `clienteId` | Long         | ID do cliente          |
| `usuarioId` | Long         | ID do usuário          |
| `status`    | enum         | Status do agendamento  |

Ordenado por `dataMarcada` desc, `horaInicio` desc.

**AgendamentoDtoRequest:**
```json
{
  "dataMarcada": "2026-07-01",
  "horaInicio": "09:00:00",
  "horaFim": "10:00:00",
  "clientId": 1,
  "userId": 1,
  "descricao": "Corte e barba",
  "anotacao": "",
  "status": "MARCADO"
}
```

**Valores válidos para `status`:**
`MARCADO` · `CONFIRMADO` · `CANCELADO` · `CONCLUIDO` · `NAO_APARECEU`

---

## Respostas de erro

```json
{
  "status": 404,
  "message": "Agendamento não encontrado: 99"
}
```

| Código | Situação                                   |
|--------|--------------------------------------------|
| 400    | JSON malformado ou tipo de campo incorreto |
| 401    | Sem autenticação ou credenciais inválidas  |
| 403    | Sem permissão (role insuficiente)          |
| 404    | Recurso não encontrado                     |
| 500    | Erro interno do servidor                   |

---

## Estrutura do projeto

```
src/
├── main/
│   ├── java/com/agilo/
│   │   ├── config/        # SecurityConfig
│   │   ├── common/        # PageResponse<T>
│   │   ├── exception/     # GlobalExceptionHandler + hierarquia de exceções
│   │   ├── user/          # Usuario, UsuarioService, UsuarioController, DTOs
│   │   ├── client/        # Cliente, ClienteService, ClienteController, DTOs
│   │   └── appointment/   # Agendamento, AgendamentoService, AgendamentoController,
│   │                      # AgendamentoSpecification, DTOs
│   └── resources/
│       ├── static/
│       │   ├── index.html   # Frontend SPA (servido em /)
│       │   └── favicon.svg
│       └── application.properties
```

---

## Stack

| Tecnologia      | Versão                          |
|-----------------|---------------------------------|
| Java            | 21                              |
| Spring Boot     | 4.1.0                           |
| Spring Security | —                               |
| Spring Data JPA | —                               |
| PostgreSQL      | —                               |
| Lombok          | —                               |
| Frontend        | HTML / CSS / JS (sem framework) |
