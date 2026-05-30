# Coworking Booking API

Uma API RESTful desenvolvida para gerir o cadastro e a reserva de salas num espaço de coworking. O sistema garante a integridade da agenda, prevenindo conflitos de horários através de regras de negócio validadas, e foi construído com foco em boas práticas de engenharia de software e arquitetura limpa.

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
- **Kubernetes (K8s):** Manifestos para orquestração em ambiente Cloud (PoC).

---

## Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/rooms` | Cadastra uma nova sala |
| `GET` | `/api/rooms` | Lista todas as salas |
| `GET` | `/api/rooms/{id}` | Busca uma sala por ID (404 se não existir) |
| `POST` | `/api/bookings` | Cria uma reserva |
| `DELETE` | `/api/bookings/{id}` | Cancela uma reserva |
| `GET` | `/api/bookings/agenda?date=YYYY-MM-DD` | Consulta a agenda do dia |

As respostas de sucesso utilizam `RoomResponseDTO` e `BookingResponseDTO`, evitando expor entidades JPA diretamente. Criações (`POST`) retornam **201 Created** com o cabeçalho `Location` apontando para o recurso criado.

Documentação interativa: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Formato de Erros

Todos os erros seguem um envelope JSON padronizado pelo `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-05-30T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descrição do erro",
  "fieldErrors": {
    "campo": "Mensagem de validação"
  }
}
```

| Situação | HTTP | Observação |
|----------|------|------------|
| Validação de DTO (`@Valid`) | 400 | Inclui `fieldErrors` com detalhes por campo |
| Regra de negócio inválida (ex.: horário, sala inexistente) | 400 | Apenas `message` |
| Conflito de horário ou reserva já cancelada | 409 | Apenas `message` |
| Erro interno não mapeado | 500 | Mensagem genérica de segurança |

---

## Como Executar o Projeto Localmente

### Opção 1: Via IDE (Padrão)

A forma mais simples. O projeto utiliza H2 em memória por padrão.

1. Clone o repositório: `https://github.com/caiojulio/booking-api`
2. Abra o projeto na sua IDE preferida (IntelliJ, Eclipse, VS Code).
3. Execute a classe principal `BookingApiApplication.java`.
4. A API estará disponível na porta `8080`.
5. Acesse o Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

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

### Executar os Testes

Na raiz do projeto:

```
./mvnw test
```

---

## Decisões Arquiteturais

1. **Idioma do Código e Domínio:** O código-fonte (classes, variáveis, métodos) foi escrito em **Inglês**. As mensagens de retorno da API (exceções, validações de DTO) e as descrições dos testes (`@DisplayName`) permanecem em **Português**, refletindo o idioma do negócio e dos utilizadores finais.

2. **Separação de Camadas e DTOs:** Os `Controllers` recebem DTOs de entrada (`RoomRequestDTO`, `BookingRequestDTO`) e devolvem DTOs de saída (`RoomResponseDTO`, `BookingResponseDTO`), protegendo as entidades JPA de exposição direta na API. A lógica de negócio e persistência ficam na camada `Service`; o acesso a dados, nos `Repository`. Injeção de dependências via construtor com Lombok (`@RequiredArgsConstructor`).

3. **Modelo de Domínio Rico:** As entidades encapsulam regras e comportamentos — por exemplo, `Booking` valida horários no construtor e expõe `cancel()`; `Room` valida dados no construtor e em `updateDetails()`. Isso evita entidades anêmicas e concentra invariantes do domínio.

4. **Prevenção de Conflitos (BookingService):** A verificação de disponibilidade da sala está centralizada no serviço, com consulta **JPQL** no repositório para detectar sobreposição de horários, executada de forma transacional (`@Transactional`).

5. **Tratamento Global de Exceções:** O `GlobalExceptionHandler` padroniza o JSON de erro e mapeia exceções para os status HTTP adequados (400, 409, 500), incluindo fallback para erros internos.

6. **Infraestrutura como Código (K8s PoC):** O `docker-compose` provê apenas o PostgreSQL para avaliação local com banco real. A pasta `/k8s` é uma **Prova de Conceito (PoC)** de orquestração em Cloud (Deployments e Services), não sendo necessária para avaliar o desafio.
