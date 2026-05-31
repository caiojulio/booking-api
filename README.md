# Coworking Booking API

Uma API RESTful desenvolvida para gerir o cadastro e a reserva de salas num espaço de coworking. O sistema garante a integridade da agenda, prevenindo conflitos de horários através de regras de negócio validadas, e foi construído com foco em boas práticas de engenharia de software e arquitetura limpa.

## Sumário

* [Arquitetura e Modelagem](#arquitetura-e-modelagem)
* [Tecnologias e Ferramentas](#tecnologias-e-ferramentas)
* [Endpoints da API](#endpoints-da-api)
* [Como Executar o Projeto Localmente](#como-executar-o-projeto-localmente)
* [Exemplos de Uso (Postman)](#exemplos-de-uso-postman)
* [Formato de Erros](#formato-de-erros)
* [Decisões Arquiteturais](#decisões-arquiteturais)
* [Processo de Desenvolvimento da API](#processo-de-desenvolvimento-da-api)

---

## Arquitetura e Modelagem

<table>
  <tr>
    <th width="50%" valign="top">Estrutura do Projeto</th>
    <th width="50%" valign="top">Modelagem do Domínio</th>
  </tr>
  <tr>
    <td valign="top">
      <pre>
booking-api/
├── docs/
│   └── diagrama-classes-coworking-api.svg
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── src/
│   ├── main/
│   │   ├── java/com/coworking/bookingapi/
│   │   │   ├── BookingApiApplication.java
│   │   │   ├── config/
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── BookingController.java
│   │   │   │   └── RoomController.java
│   │   │   ├── dto/
│   │   │   │   ├── BookingRequestDTO.java
│   │   │   │   ├── BookingResponseDTO.java
│   │   │   │   ├── ErrorResponseDTO.java
│   │   │   │   ├── RoomRequestDTO.java
│   │   │   │   └── RoomResponseDTO.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── model/
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BookingStatus.java
│   │   │   │   ├── Room.java
│   │   │   │   └── RoomType.java
│   │   │   ├── repository/
│   │   │   │   ├── BookingRepository.java
│   │   │   │   └── RoomRepository.java
│   │   │   └── service/
│   │   │       ├── BookingService.java
│   │   │       └── RoomService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/coworking/bookingapi/
│           ├── BookingApiApplicationTests.java
│           ├── controller/
│           │   ├── BookingControllerTest.java
│           │   └── RoomControllerTest.java
│           └── service/
│               ├── BookingServiceTest.java
│               └── RoomServiceTest.java
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
      </pre>
      <p align="center">
        <i>O projeto segue uma arquitetura em camadas típica de Spring Boot: <b>Controller → Service → Repository → Model</b>, com DTOs de entrada e saída, validações nas entidades e tratamento centralizado de exceções.</i>
      </p>
    </td>
    <td valign="top">
      <br>
      <img src="docs/diagrama-classes-coworking-api.svg" alt="Diagrama de Classes da API" width="100%">
      <br><br>
      <p align="center">
        <i>Diagrama de classes do projeto.</i>
      </p>
    </td>
  </tr>
</table>

## Tecnologias e Ferramentas

- **Java 17 & Spring Boot 4.0.6:** Base do desenvolvimento backend.
- **Spring Data JPA & Hibernate:** Persistência de dados.
- **Lombok:** Redução de boilerplate e injeção de dependências via construtor.
- **H2 Database ou PostgreSQL:** H2 em memória no perfil padrão (desenvolvimento) e PostgreSQL no perfil `prod`.
- **JUnit 5 & Mockito & MockMvc:** Testes unitários e de integração da camada web.
- **Springdoc OpenAPI (Swagger):** Documentação interativa da API.
- **Docker & Docker Compose:** Provisionamento do PostgreSQL para ambiente local com banco real.
- **Kubernetes (K8s):** Manifestos para orquestração em ambiente Cloud com caráter de prova de conceito (PoC).

---

## Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/rooms` | Cadastra uma nova sala |
| `GET` | `/api/rooms` | Lista todas as salas |
| `GET` | `/api/rooms/{id}` | Busca uma sala por ID (404 se não existir) |
| `GET` | `/api/rooms/available?date=&startTime=&endTime=` | Lista salas livres num período |
| `POST` | `/api/bookings` | Cria uma reserva |
| `GET` | `/api/bookings?page=&size=&sort=` | Lista todas as reservas com paginação (uso administrativo) |
| `DELETE` | `/api/bookings/{id}` | Cancela uma reserva |
| `GET` | `/api/bookings/agenda?date=YYYY-MM-DD` | Consulta a agenda do dia |

As respostas de sucesso utilizam `RoomResponseDTO` e `BookingResponseDTO`, evitando expor entidades JPA diretamente. Criações (`POST`) retornam **201 Created** com o cabeçalho `Location` apontando para o recurso criado.

> A listagem de reservas usa paginação do Spring Data. Por padrão, retorna `size=10` e ordena por `date` em ordem decrescente. O termo "uso administrativo" indica a finalidade do endpoint; o projeto não implementa autenticação/autorização.

Documentação interativa: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Como Executar o Projeto Localmente

### Opção 1: Via IDE (Padrão)

A forma mais simples. O projeto utiliza H2 em memória por padrão.

1. Clone o repositório: `https://github.com/caiojulio/booking-api`
2. Abra o projeto na sua IDE preferida (IntelliJ, Eclipse, VS Code).
3. Execute a classe principal `BookingApiApplication.java`.
4. A API estará disponível na porta `8080`.
5. Acesse o Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
6. Opcionalmente, acesse o console H2: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Configuração padrão do H2:**

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:coworkingdb` |
| User | `sa` |
| Password | *(vazio)* |

### Opção 2: Base de Dados Real via Docker (PostgreSQL)

Simula um ambiente mais próximo da produção: o Docker provê o PostgreSQL e o Maven executa a API conectada a ele.

⚠️ **Pré-requisitos:** **Docker** e **Docker Compose** instalados, com o **Docker Engine ativo**.

**Passo a passo:**

1. **Suba o banco de dados** (na raiz do projeto):

   ```
   docker-compose up
   ```

   *(Este terminal ficará exibindo os logs do PostgreSQL. Mantenha-o em execução.)*

2. **Inicie a API** em um **novo terminal** (também na raiz do projeto):

   ```
   ./mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"
   ```

   *(As aspas garantem compatibilidade com Windows PowerShell.)*

3. **Pronto!** A API estará conectada ao PostgreSQL. Acesse o Swagger UI:
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

O perfil `prod` também aceita variáveis de ambiente para conexão com o banco:

| Variável | Valor padrão |
|----------|--------------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `coworkingdb` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `password` |

### Executar os Testes

Na raiz do projeto:

```
./mvnw test
```

---

## Exemplos de Uso (Postman)

**URL base:** `http://localhost:8080`

**Headers recomendados** (requisições com corpo JSON):

| Header | Valor |
|--------|-------|
| `Content-Type` | `application/json` |
| `Accept` | `application/json` |

**Ordem sugerida para testar o fluxo completo:**

1. Cadastrar uma sala → anote o `id` retornado
2. Consultar salas livres no período desejado
3. Criar uma reserva usando esse `roomId`
4. Consultar a agenda do dia
5. Cancelar a reserva
6. Listar todas as reservas com paginação
7. (Opcional) Repetir a reserva no mesmo horário para validar o conflito (409)

> **Tipos de sala válidos (`type`):** `INDIVIDUAL`, `SHARED`, `AUDITORIUM`

---

### 1. Cadastrar sala

**`POST`** `http://localhost:8080/api/rooms`

**Body (JSON):**

```json
{
  "name": "Sala Focus",
  "type": "INDIVIDUAL",
  "capacity": 1
}
```

**Resposta esperada — `201 Created`:**

```json
{
  "id": 1,
  "name": "Sala Focus",
  "type": "INDIVIDUAL",
  "capacity": 1
}
```

**Header de resposta:** `Location: http://localhost:8080/api/rooms/1`

---

### 2. Listar salas

**`GET`** `http://localhost:8080/api/rooms`

**Resposta esperada — `200 OK`:**

```json
[
  {
    "id": 1,
    "name": "Sala Focus",
    "type": "INDIVIDUAL",
    "capacity": 1
  }
]
```

---

### 3. Buscar sala por ID

**`GET`** `http://localhost:8080/api/rooms/1`

**Resposta esperada — `200 OK`:**

```json
{
  "id": 1,
  "name": "Sala Focus",
  "type": "INDIVIDUAL",
  "capacity": 1
}
```

---

### 4. Consultar salas livres

**`GET`** `http://localhost:8080/api/rooms/available?date=2026-06-15&startTime=09:00:00&endTime=11:00:00`

*(Sem body — parâmetros enviados na query string.)*

**Resposta esperada — `200 OK`:**

```json
[
  {
    "id": 1,
    "name": "Sala Focus",
    "type": "INDIVIDUAL",
    "capacity": 1
  }
]
```

> Retorna apenas salas **sem reservas confirmadas** que se sobreponham ao período informado. Após criar a reserva do exemplo 5, repetir esta consulta no mesmo horário deve excluir a sala reservada da lista.

---

### 5. Criar reserva

**`POST`** `http://localhost:8080/api/bookings`

**Body (JSON):**

```json
{
  "responsiblePerson": "Sophia Pantoja",
  "date": "2026-06-15",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "roomId": 1
}
```

**Resposta esperada — `201 Created`:**

```json
{
  "id": 1,
  "responsiblePerson": "Sophia Pantoja",
  "date": "2026-06-15",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "status": "CONFIRMED",
  "roomName": "Sala Focus"
}
```

**Header de resposta:** `Location: http://localhost:8080/api/bookings/1`

---

### 6. Consultar agenda do dia

**`GET`** `http://localhost:8080/api/bookings/agenda?date=2026-06-15`

**Resposta esperada — `200 OK`:**

```json
[
  {
    "id": 1,
    "responsiblePerson": "Sophia Pantoja",
    "date": "2026-06-15",
    "startTime": "09:00:00",
    "endTime": "11:00:00",
    "status": "CONFIRMED",
    "roomName": "Sala Focus"
  }
]
```

---

### 7. Cancelar reserva

**`DELETE`** `http://localhost:8080/api/bookings/1`

**Resposta esperada — `200 OK`:**

```json
{
  "id": 1,
  "responsiblePerson": "Sophia Pantoja",
  "date": "2026-06-15",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "status": "CANCELLED",
  "roomName": "Sala Focus"
}
```

---

### 8. Listar reservas com paginação

**`GET`** `http://localhost:8080/api/bookings?page=0&size=10&sort=date,desc`

*(Sem body — parâmetros de paginação enviados na query string.)*

**Resposta esperada — `200 OK`:**

```json
{
  "content": [
    {
      "id": 1,
      "responsiblePerson": "Sophia Pantoja",
      "date": "2026-06-15",
      "startTime": "09:00:00",
      "endTime": "11:00:00",
      "status": "CANCELLED",
      "roomName": "Sala Focus"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

> A estrutura completa do objeto `Page` pode variar conforme a versão do Spring, mas os campos principais (`content`, `totalElements`, `totalPages`, `size`, `number`) indicam os dados e metadados da paginação.

---

### Exemplos de erro (para validação)

Cada cenário abaixo traz o **JSON para enviar no Postman** (quando houver corpo) e a **resposta esperada** da API.

> **Nota:** Os valores de `timestamp` nos exemplos são ilustrativos (podem incluir fração de segundos). A **ordem dos campos no JSON pode variar** — valide o conteúdo de cada campo, não a ordem.

---

#### 9. Validação de DTO — nome da sala vazio

**`POST`** `http://localhost:8080/api/rooms`

**Body (JSON):**

```json
{
  "name": "",
  "type": "INDIVIDUAL",
  "capacity": 1
}
```

**Resposta esperada — `400 Bad Request`:**

```json
{
  "fieldErrors": {
    "name": "O nome da sala é obrigatório"
  },
  "error": "Bad Request",
  "message": "Erro de validação nos dados enviados.",
  "timestamp": "2026-05-30T17:20:44.7199556",
  "status": 400
}
```

---

#### 10. Horário inválido — início após o término

**`POST`** `http://localhost:8080/api/bookings`

**Body (JSON):**

```json
{
  "responsiblePerson": "Sophia Pantoja",
  "date": "2026-06-15",
  "startTime": "14:00:00",
  "endTime": "10:00:00",
  "roomId": 1
}
```

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T10:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O horário de início deve ser anterior ao horário de término."
}
```

---

#### 11. Conflito de horário — slot já reservado

> **Pré-requisito:** existir uma reserva confirmada para a sala `1` das 09:00 às 11:00 no dia `2026-06-15` (use o exemplo 5 antes deste teste ou realize o POST do seguinte exemplo duas vezes seguidas).

**`POST`** `http://localhost:8080/api/bookings`

**Body (JSON):**

```json
{
  "responsiblePerson": "Caio Silva",
  "date": "2026-06-15",
  "startTime": "10:00:00",
  "endTime": "12:00:00",
  "roomId": 1
}
```

**Resposta esperada — `409 Conflict`:**

```json
{
  "timestamp": "2026-06-15T10:40:00",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe uma reserva confirmada para esta sala neste horário."
}
```

---

#### 12. Reserva já cancelada — cancelamento duplicado

> **Pré-requisito:** cancelar a reserva `1` uma vez (exemplo 7) e repetir a mesma requisição.

**`DELETE`** `http://localhost:8080/api/bookings/1`

*(Sem body — requisição DELETE não envia JSON.)*

**Resposta esperada — `409 Conflict`:**

```json
{
  "timestamp": "2026-06-15T10:45:00",
  "status": 409,
  "error": "Conflict",
  "message": "Esta reserva já encontra-se cancelada."
}
```

---

#### 13. Sala inexistente — `roomId` inválido

**`POST`** `http://localhost:8080/api/bookings`

**Body (JSON):**

```json
{
  "responsiblePerson": "Caio Silva",
  "date": "2026-06-15",
  "startTime": "09:00:00",
  "endTime": "10:00:00",
  "roomId": 999
}
```

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T10:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Sala não encontrada com o ID informado."
}
```

---

#### 14. Sala inexistente — busca por ID

**`GET`** `http://localhost:8080/api/rooms/999`

*(Sem body — requisição GET não envia JSON.)*

**Resposta esperada — `404 Not Found`:** corpo vazio

---

#### 15. Reserva inexistente — cancelamento

**`DELETE`** `http://localhost:8080/api/bookings/999`

*(Sem body — requisição DELETE não envia JSON.)*

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T11:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Reserva não encontrada com o ID informado."
}
```

---

#### 16. Horário inválido — consulta de salas livres

**`GET`** `http://localhost:8080/api/rooms/available?date=2026-06-15&startTime=16:00:00&endTime=14:00:00`

*(Sem body — parâmetros inválidos na query string.)*

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T11:05:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O horário de início deve ser anterior ao horário de término."
}
```

---

#### 17. Nome de sala duplicado

> **Pré-requisito:** já existir uma sala cadastrada com o nome `"Sala Focus"`.

**`POST`** `http://localhost:8080/api/rooms`

**Body (JSON):**

```json
{
  "name": "Sala Focus",
  "type": "INDIVIDUAL",
  "capacity": 1
}
```

**Resposta esperada — `409 Conflict`:**

```json
{
  "timestamp": "2026-06-15T11:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe um registro com estes dados únicos no sistema."
}
```

---

#### 18. JSON inválido ou enum inválido

**`POST`** `http://localhost:8080/api/rooms`

**Body (JSON):**

```json
{
  "name": "Sala Workshop",
  "type": "MEETING",
  "capacity": 8
}
```

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T11:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Formato de dado inválido no corpo da requisição. Verifique se os valores dos campos estão corretos."
}
```

---

#### 19. Parâmetro inválido na URL

**`GET`** `http://localhost:8080/api/rooms/abc`

*(Sem body — o ID deveria ser numérico.)*

**Resposta esperada — `400 Bad Request`:**

```json
{
  "timestamp": "2026-06-15T11:20:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O parâmetro enviado na URL é inválido. Verifique se o formato está correto."
}
```

---

## Formato de Erros

Todos os erros tratados pelo `GlobalExceptionHandler` seguem um envelope JSON padronizado usando `ErrorResponseDTO`. Os campos presentes dependem do tipo de erro; campos nulos não são serializados.

| Campo | Sempre presente? | Descrição |
|-------|------------------|-----------|
| `timestamp` | Sim | Momento do erro (pode incluir fração de segundos) |
| `status` | Sim | Código HTTP numérico (ex.: `400`, `409`) |
| `error` | Sim | Frase padrão do HTTP (ex.: `"Bad Request"`) |
| `message` | Sim | Mensagem descritiva do erro |
| `fieldErrors` | Apenas em validação de DTO | Mapa `campo → mensagem` |

**Exemplo — erro de validação:**

```json
{
  "timestamp": "2026-05-30T17:20:44.7199556",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos dados enviados.",
  "fieldErrors": {
    "name": "O nome da sala é obrigatório"
  }
}
```

**Exemplo — erro de negócio (sem `fieldErrors`):**

```json
{
  "timestamp": "2026-05-30T17:25:10.1234567",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe uma reserva confirmada para esta sala neste horário."
}
```

| Situação | HTTP | Observação |
|----------|------|------------|
| Validação de DTO (`@Valid`) | 400 | Inclui `fieldErrors` com detalhes por campo |
| JSON malformado, enum inválido ou corpo ilegível | 400 | Mensagem padronizada sobre formato inválido |
| Parâmetro de URL com tipo inválido | 400 | Ex.: `/api/rooms/abc` |
| Regra de negócio inválida (ex.: horário, sala inexistente) | 400 | Apenas `message` |
| Violação de dados únicos (ex.: nome de sala duplicado) | 409 | Apenas `message` |
| Conflito de horário ou reserva já cancelada | 409 | Apenas `message` |
| Busca de sala por ID inexistente | 404 | Retorna corpo vazio no endpoint `GET /api/rooms/{id}` |
| Erro interno não mapeado | 500 | Mensagem genérica de segurança |

---

## Decisões Arquiteturais

1. **Idioma do Código e Domínio:** O código-fonte (classes, variáveis, métodos) foi escrito em **Inglês**. As mensagens de retorno da API (exceções, validações de DTO) e as descrições dos testes (`@DisplayName`) permanecem em **Português**, refletindo o idioma do negócio e dos utilizadores finais.

2. **Separação de Camadas e DTOs:** Os `Controllers` recebem DTOs de entrada (`RoomRequestDTO`, `BookingRequestDTO`) e devolvem DTOs de saída (`RoomResponseDTO`, `BookingResponseDTO`), protegendo as entidades JPA de exposição direta na API. O contrato de erro fica centralizado em `ErrorResponseDTO`. A lógica de negócio e persistência ficam na camada `Service`; o acesso a dados, nos `Repository`. Injeção de dependências via construtor com Lombok (`@RequiredArgsConstructor`).

3. **Modelo de Domínio Rico:** As entidades encapsulam regras e comportamentos — por exemplo, `Booking` valida horários no construtor e expõe `cancel()`; `Room` valida dados no construtor e em `updateDetails()`. Isso evita entidades anêmicas e concentra invariantes do domínio.

4. **Prevenção de Conflitos (BookingService):** A verificação de disponibilidade da sala está centralizada no serviço, com consulta **JPQL** no repositório para detectar sobreposição de horários, executada de forma transacional (`@Transactional`).

5. **Consulta de Salas Livres (RoomService):** O endpoint `GET /api/rooms/available` retorna salas sem reservas confirmadas no período informado, utilizando consulta **JPQL** em `RoomRepository.findAvailableRooms`. A validação de horários inválidos ocorre na camada de serviço antes da consulta.

6. **Tratamento Global de Exceções:** O `GlobalExceptionHandler` padroniza o JSON de erro e mapeia exceções para os status HTTP adequados (400, 409, 500), cobrindo validação de DTO, JSON inválido, parâmetro de URL inválido, violação de integridade no banco, conflitos de negócio e fallback para erros internos.

7. **Infraestrutura como Código (K8s PoC):** O `docker-compose` provê apenas o PostgreSQL para avaliação local com banco real. A pasta `/k8s` é uma **Prova de Conceito (PoC)** de orquestração em Cloud (Deployments e Services), não sendo necessária para execução da API.

## Processo de Desenvolvimento da API

O projeto foi desenvolvido buscando seguir boas práticas de controle de versão e organização de código:

1. **Desenvolvimento com Branches:** A maior parte das novas funcionalidades (como `feat/available-rooms-query`) foi desenvolvida em branches locais isoladas para garantir a estabilidade do código.
2. **Commits Semânticos (Conventional Commits):** Os commits foram estruturados de forma atômica e descritiva (ex: `feat:`, `docs:`, `chore:`), garantindo um histórico de alterações legível e organizado.
3. **Fluxo de Integração:** Para o desenvolvimento das funcionalidades isoladas, seguiu-se o padrão de atualizar a branch local com a `main` remota (`pull`), realizar o `merge` local e, em seguida, o `push` para o repositório remoto.
4. **Validação Local:** Sempre que aplicável, os testes unitários (`./mvnw test`) foram executados localmente para garantir o bom funcionamento da API antes do envio final.
