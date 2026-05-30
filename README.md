# Coworking Booking API

Uma API RESTful robusta desenvolvida para gerir o cadastro e a reserva de salas num espaço de coworking. O sistema garante a integridade da agenda, prevenindo conflitos de horários através de regras de negócio validadas, e foi construído com foco em boas práticas de engenharia de software e arquitetura limpa.

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
│   │   │   │   └── RoomRequestDTO.java
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
├── pom.xml                            
└── README.md
      </pre>
      <p align="center">
        <i>O projeto segue uma arquitetura em camadas típica de Spring Boot: <b>Controller → Service → Repository → Model</b>, com DTOs para entrada de dados e validações centralizadas.</i>
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
- **Lombok:** Redução de código boilerplate e injeção de dependências limpa.
- **H2 Database ou PostgreSQL:** Base de dados em memória (H2) para testes e desenvolvimento rápido, e PostgreSQL preparado para o ambiente de produção via Docker.
- **JUnit 5 & Mockito & MockMvc:** Testes unitários e de integração.
- **Springdoc OpenAPI (Swagger):** Documentação interativa e viva da API.
- **Docker & Docker Compose:** Conteinerização da aplicação e base de dados.
- **Kubernetes (K8s):** Manifestos para orquestração em ambiente Cloud.

---

## Como Executar o Projeto Localmente

### Opção 1: Via IDE (Padrão)
A forma mais simples. O projeto está configurado para utilizar uma base de dados H2 em memória por padrão.
1. Clone o repositório: `https://github.com/caiojulio/booking-api`
2. Abra o projeto na sua IDE preferida (IntelliJ, Eclipse, VS Code).
3. Execute a classe principal `BookingApiApplication.java`.
4. A API estará disponível na porta `8080`.

### Opção 2: Base de Dados Real via Docker (PostgreSQL)
Se você deseja testar a aplicação simulando um ambiente real de produção, utilizaremos o Docker para prover a base de dados PostgreSQL e o Maven para executar a API conectada a ela.

⚠️ **Atenção aos Pré-requisitos:** Certifique-se de que o **Docker** e o **Docker Compose** estejam instalados e, fundamentalmente, que o **Docker Engine esteja ativo** (rodando em background) na sua máquina antes de prosseguir.

**Passo a passo:**

1. **Suba o Banco de Dados:**
   Abra o seu terminal na raiz do projeto e execute o comando abaixo para iniciar o contêiner do PostgreSQL:

   ```
   docker-compose up
   ```

   *(Dica: Este terminal ficará ocupado exibindo os logs do banco de dados. Não o feche e deixe-o rodando!)*

2. **Inicie a Aplicação (API):**
   Abra um **novo terminal** (também na raiz do projeto) e execute a aplicação injetando o perfil de produção (`prod`), que orienta o Spring Boot a ignorar o banco em memória e conectar-se ao Docker:

   ```
   ./mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"
   ```

   *(Nota de compatibilidade: O uso das aspas no parâmetro garante que o comando funcione perfeitamente em qualquer sistema operacional e terminal, incluindo o Windows PowerShell).*

3. **Pronto para Uso!**
   Assim que o terminal exibir a mensagem de sucesso de inicialização, a sua API estará ativa e conectada ao banco real. Acesse a documentação interativa para realizar seus testes:
   * **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Decisões Arquiteturais

Para garantir a qualidade, manutenção e alinhamento com as necessidades do negócio, foram tomadas as seguintes decisões de desenho:

1. **Idioma do Código e Domínio:** O código-fonte (classes, variáveis, métodos) foram escritos em **Inglês**, respeitando o padrão global da indústria. No entanto, as mensagens de retorno da API (Exceptions, validações de DTO) e as descrições dos testes (`@DisplayName`) foram mantidas em **Português** para refletir o idioma nativo do negócio e dos utilizadores finais.
2. **Arquitetura Limpa e Isolamento de Responsabilidades:** O projeto segue uma separação rigorosa de camadas. Os `Controllers` atuam exclusivamente na recepção de requisições via DTOs, delegando toda a conversão de entidades e validação de negócio para a camada de `Service`. A injeção de dependências é feita via construtor utilizando a biblioteca Lombok.
3. **Prevenção de Conflitos (BookingService):** A validação de disponibilidade da sala foi centralizada na camada de serviço, utilizando uma *query* nativa no repositório para garantir que não existem sobreposições de horários de forma transacional (`@Transactional`).
4. **Infraestrutura como Código (K8s PoC):** O projeto utiliza o `docker-compose` como ambiente oficial para avaliação local. A pasta `/k8s` foi incluída puramente como uma **Prova de Conceito (PoC)**, demonstrando como a aplicação seria orquestrada (Deployments e LoadBalancers) num ambiente real de Cloud (ex: AWS EKS, Google GKE), não sendo necessária a sua execução para avaliar este desafio.
