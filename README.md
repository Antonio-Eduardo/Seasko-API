# Studio Web

API REST para gerenciamento de agendamentos de um estúdio (salão de beleza, barbearia, etc.).
Construída com Spring Boot 4.1.0 + Java 21 + PostgreSQL.

---

## Funcionalidades

- Autenticação via HTTP Basic Auth com senhas BCrypt
- Dois níveis de acesso: `ADMIN` e `EMPLOYEE`
- CRUD completo de **usuários**, **clientes** e **agendamentos**
- Atualização isolada de status de agendamento
- Tratamento centralizado de erros com respostas JSON padronizadas

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL rodando localmente

---

## Configuração

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edite `application.properties`:

```properties
spring.application.name=studio-web
spring.datasource.url=jdbc:postgresql://localhost:5432/studio-web
spring.datasource.username=<seu_usuario>
spring.datasource.password=<sua_senha>
spring.jpa.hibernate.ddl-auto=update
```

O banco de dados e as tabelas são criados automaticamente pelo Hibernate na primeira execução.

---

## Executando

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

**Usuário admin criado automaticamente no primeiro startup:**
- `usuario`: `admin`
- `senha`: `admin123`

---

## Autenticação

Todas as rotas (exceto `/public/**`) exigem autenticação Basic Auth.

Exemplo com curl:

```bash
curl -u admin:admin123 http://localhost:8080/usuario
```

---

## Endpoints

### Usuários — `/usuario`

| Método | Path                    | Descrição         | Body                        |
|--------|-------------------------|-------------------|-----------------------------|
| GET    | `/usuario`              | Listar todos      | —                           |
| GET    | `/usuario/{id}`         | Buscar por ID     | —                           |
| POST   | `/usuario/registrar`    | Criar usuário     | `UsuarioDtoRequest`         |
| PUT    | `/usuario/atualizar/{id}` | Atualizar       | `UsuarioDtoRequest`         |
| DELETE | `/usuario/deletar/{id}` | Remover           | —                           |

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
  "usuario": "maria",
  "role": "EMPLOYEE",
  "ativo": true
}
```

---

### Clientes — `/cliente`

| Método | Path                     | Descrição     | Body               |
|--------|--------------------------|---------------|--------------------|
| GET    | `/cliente`               | Listar todos  | —                  |
| GET    | `/cliente/{id}`          | Buscar por ID | —                  |
| POST   | `/cliente/inserir`       | Criar cliente | `ClienteDtoRequest`|
| PUT    | `/cliente/atualizar/{id}`| Atualizar     | `ClienteDtoRequest`|
| DELETE | `/cliente/deletar/{id}`  | Remover       | —                  |

**ClienteDtoRequest:**
```json
{
  "nome": "João Pereira",
  "telefone": "11999990000",
  "anotacao": "Prefere atendimento às manhãs"
}
```

**ClienteDtoResponse:**
```json
{
  "id": 1,
  "nome": "João Pereira",
  "telefone": "11999990000",
  "anotacao": "Prefere atendimento às manhãs",
  "criadoEm": "2026-06-22T10:30:00"
}
```

---

### Agendamentos — `/agendamento`

| Método | Path                          | Descrição              | Body                     |
|--------|-------------------------------|------------------------|--------------------------|
| GET    | `/agendamento`                | Listar todos           | —                        |
| GET    | `/agendamento/{id}`           | Buscar por ID          | —                        |
| POST   | `/agendamento/inserir`        | Criar agendamento      | `AgendamentoDtoRequest`  |
| PUT    | `/agendamento/atualizar/{id}` | Atualizar              | `AgendamentoDtoRequest`  |
| PUT    | `/agendamento/status/{id}`    | Atualizar status       | `AgendamentoStatus` (string) |
| DELETE | `/agendamento/deletar/{id}`   | Remover                | —                        |

**AgendamentoDtoRequest:**
```json
{
  "dataMarcada": "2026-07-01",
  "horaInicio": "09:00",
  "horaFim": "10:00",
  "clientId": 1,
  "userId": 1,
  "descricao": "Corte e barba",
  "anotacao": ""
}
```

**AgendamentoDtoResponse:**
```json
{
  "id": 1,
  "dataMarcada": "2026-07-01",
  "horaInicio": "09:00",
  "horaFim": "10:00",
  "status": "MARCADO",
  "descricao": "Corte e barba",
  "anotacao": "",
  "clientId": 1,
  "userId": 1
}
```

**Valores válidos para status:**
`MARCADO` · `CONFIRMADO` · `CANCELADO` · `CONCLUIDO` · `NAO_APARECEU`

---

## Respostas de erro

Todos os erros retornam JSON no formato:

```json
{
  "status": 404,
  "message": "Agendamento não encontrado: 99"
}
```

| Código | Situação                                      |
|--------|-----------------------------------------------|
| 400    | JSON malformado ou tipo de campo incorreto    |
| 401    | Sem autenticação ou credenciais inválidas     |
| 404    | Recurso não encontrado                        |
| 500    | Erro interno do servidor                      |

---

## Estrutura do projeto

```
src/main/java/com/studioweb/studio_web/
├── config/           # SecurityConfig
├── exception/        # GlobalExceptionHandler + hierarquia de exceções
├── user/             # Usuario, UsuarioService, UsuarioController, DTOs
├── client/           # Cliente, ClienteService, ClienteController, DTOs
└── appointment/      # Agendamento, AgendamentoService, AgendamentoController, DTOs
```

---

## Stack

| Tecnologia        | Versão |
|-------------------|--------|
| Java              | 21     |
| Spring Boot       | 4.1.0  |
| Spring Security   | —      |
| Spring Data JPA   | —      |
| PostgreSQL        | —      |
| Lombok            | —      |
