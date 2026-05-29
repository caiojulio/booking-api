# Coworking Booking API

Uma API RESTful robusta desenvolvida para gerir o cadastro e a reserva de salas num espaço de coworking. O sistema garante a integridade da agenda, prevenindo conflitos de horários através de regras de negócio validadas, e foi construído com foco em boas práticas de engenharia de software e arquitetura limpa.

## Tecnologias e Ferramentas

- **Java 17 & Spring Boot 3:** Base do desenvolvimento backend.
- **Spring Data JPA & Hibernate:** Persistência de dados.
- **H2 Database ou PostgreSQL:** Base de dados em memória (H2) para testes e desenvolvimento rápido, e PostgreSQL preparado para o ambiente de produção via Docker.
- **JUnit 5 & Mockito & MockMvc:** Testes unitários e de integração.
- **Springdoc OpenAPI (Swagger):** Documentação interativa e viva da API.
- **Docker & Docker Compose:** Conteinerização da aplicação e base de dados.
- **Kubernetes (K8s):** Manifestos para orquestração em ambiente Cloud.

---

## Como Executar o Projeto Localmente

### Opção padrão: Via IDE
A forma mais simples. O projeto está configurado para utilizar uma base de dados H2 em memória por padrão.
1. Clone o repositório: `https://github.com/caiojulio/booking-api`
2. Abra o projeto na sua IDE preferida (IntelliJ, Eclipse, VS Code).
3. Execute a classe principal `BookingApiApplication.java`.
4. A API estará disponível na porta `8080`.

## Decisões Arquiteturais

Para garantir a qualidade, manutenção e alinhamento com as necessidades do negócio, foram tomadas as seguintes decisões de desenho:

1. **Idioma do Código e Domínio:** O código-fonte (classes, variáveis, métodos) foram escritos em **Inglês**, respeitando o padrão global da indústria. No entanto, as mensagens de retorno da API (Exceptions, validações de DTO) e as descrições dos testes (`@DisplayName`) foram mantidas em **Português** para refletir o idioma nativo do negócio e dos utilizadores finais.
2. **Prevenção de Conflitos (BookingService):** A validação de disponibilidade da sala foi centralizada na camada de serviço, utilizando uma *query* nativa no repositório para garantir que não existem sobreposições de horários de forma transacional (`@Transactional`).
3. **Infraestrutura como Código (K8s PoC):** O projeto utiliza o `docker-compose` como ambiente oficial para avaliação local. A pasta `/k8s` foi incluída puramente como uma **Prova de Conceito (PoC)**, demonstrando como a aplicação seria orquestrada (Deployments e LoadBalancers) num ambiente real de Cloud (ex: AWS EKS, Google GKE), não sendo necessária a sua execução para avaliar este desafio.

---
