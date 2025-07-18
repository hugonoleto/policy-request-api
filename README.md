# ACME Policy Request API

## Visão Geral

Este microsserviço gerencia o ciclo de vida de solicitações de apólice de seguros, integrando-se com uma API de Fraudes e utilizando arquitetura orientada a eventos (EDA) com RabbitMQ. O serviço foi desenvolvido em Java com Spring Boot, seguindo princípios de Clean Architecture, SOLID e boas práticas de design.

---

## Como Executar o Projeto

### Pré-requisitos

- Docker e Docker Compose instalados
- Git instalado

### Clonando o repositório

Clone o projeto:

```bash
git clone https://github.com/hugonoleto/policy-request-api
```

### Subindo a infraestrutura

```bash
docker-compose up
```

Isso irá subir:
- Banco de dados PostgreSQL
- RabbitMQ (com interface de gerenciamento em http://localhost:15672)
- Wiremock (mock da API de Fraudes em http://localhost:8080)

### Executando a aplicação localmente

 Rode a aplicação Spring Boot:
   ```bash
   mvn spring-boot:run
   ```

---

## Documentação da API (Swagger)

A documentação interativa da API está disponível via Swagger UI em:

[http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)

Utilize este endereço para explorar e testar os endpoints da API de forma visual.

---

## Detalhes da Solução

### Arquitetura

- **Camadas**: Interface Adapters, Application, Domain, Infrastructure
- **Event-Driven**: RabbitMQ para comunicação de eventos de estado
- **Persistência**: PostgreSQL
- **Mock de Fraudes**: Wiremock, configurado via `wiremock/mappings/fraud-api-mock.json`
- **Testes**: JUnit, Mockito, Testcontainers para integração

### Fluxo Principal

1. **Criação de Solicitação**: API REST recebe o pedido, persiste e publica evento.
2. **Análise de Fraude**: Consulta à API de Fraudes (Wiremock) para classificar o cliente.
3. **Validação de Regras**: Aplica regras de negócio conforme classificação e categoria.
4. **Processamento de Eventos**: Consome eventos de pagamento e subscrição via RabbitMQ.
5. **Histórico e Estados**: Cada transição de estado é registrada e publicada.
6. **Cancelamento**: Permite cancelar solicitações exceto quando já aprovadas/rejeitadas.

---

## Decisões e Racional

### **Filas e Simulação de Serviços**:  
  Para simular a integração com serviços externos de Pagamento e Assinatura, foram criadas as filas `policy.payment` e `policy.subscription`. Os subscribers `PaymentProcessedEventSubscriber` e `SubscriptionAuthorizedEventSubscriber` simulam o processamento e resposta desses serviços, permitindo testar o fluxo completo sem dependências externas.

### **Tabela de Clientes Bloqueados**:  
  A tabela `customer_blocked` foi adicionada para simular cenários em que os serviços de Pagamento ou Assinatura rejeitariam uma solicitação de apólice. Isso permite validar regras de negócio e fluxos de exceção de forma controlada.

### **Novos Status de Solicitação**:  
  Para controlar o status de aprovação dos serviços, foram criados quatro novos status:
    - `PAYMENT_APPROVED`: pagamento aprovado
    - `PAYMENT_REJECTED`: pagamento rejeitado
    - `SUBSCRIPTION_APPROVED`: assinatura aprovada
    - `SUBSCRIPTION_REJECTED`: assinatura rejeitada  
      Isso garante rastreabilidade e clareza no ciclo de vida da solicitação.

### **State Machine para Processamento**:  
  Foi implementada uma máquina de estados para gerenciar as transições da solicitação. O estado `ReceivedState` trata a análise antifraude e riscos, enquanto o estado `Validated` envia a solicitação para os serviços de pagamento e assinatura. Essa abordagem facilita a manutenção e evolução do fluxo de negócio.

---
## Premissas Assumidas

- Os serviços de Pagamento e Assinatura são simulados via filas e subscribers, pois não há integração real disponível.
- O campo `customerId` foi utilizado como chave para facilitar a realização de testes de diferentes perfis de risco, por exemplo:
    - `44444444-4444-4444-4444-444444444444` = cliente com perfil de risco HIGH_RISK
    - `55555555-5555-5555-5555-555555555555` = cliente com perfil de risco PREFERENTIAL
    - `66666666-6666-6666-6666-666666666666` = cliente com perfil de risco NO_INFORMATION
    - Qualquer outro UUID = cliente com perfil de risco REGULAR
- Para simular a rejeição de solicitações pelos serviços de Pagamento e Assinatura, foram adicionados registros específicos na tabela `customer_blocked`:
    - `11111111-1111-1111-1111-111111111111` = bloqueia o cliente apenas no serviço de Pagamento, fazendo com que toda solicitação desse cliente seja rejeitada nesse serviço.
    - `22222222-2222-2222-2222-222222222222` = bloqueia o cliente apenas no serviço de Assinatura, rejeitando as solicitações nesse contexto.
    - O`33333333-3333-3333-3333-333333333333` = bloqueia o cliente em ambos os serviços, resultando em rejeição tanto no Pagamento quanto na Assinatura.

---

## Testes e Coleção Postman

Foram desenvolvidos testes integrados cobrindo todos os cenários principais, garantindo a validação dos fluxos de negócio, rejeições e aprovações simuladas.  
Além disso, o arquivo `policy_request_api.postman_collection.json`, disponível na pasta `postman`, contém todas as chamadas da API e exemplos de testes prontos para facilitar a validação manual dos endpoints.

### Executando os testes

   ```bash
   mvn test
   ```

---

## Exemplos de Uso

### Clientes bloqueados

No banco, os seguintes clientes estão bloqueados:

- **Pagamento**: `11111111-1111-1111-1111-111111111111`
- **Assinatura**: `22222222-2222-2222-2222-222222222222`
- **Ambos**: `33333333-3333-3333-3333-333333333333`

Exemplo de requisição bloqueada para pagamento:

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "productId": "c2b8d9f3-5678-4e12-8def-0987654321cd",
  "category": "AUTO",
  "salesChannel": "ONLINE",
  "paymentMethod": "CREDIT_CARD",
  "totalMonthlyPremiumAmount": 150.75,
  "insuredAmount": 5000,
  "coverages": { "ROUBO": 20000, "COLISAO": 30000 },
  "assistances": ["GUINCHO", "CARRO_RESERVA"]
}
```

### Wiremock

- Endpoint de fraude: `GET http://localhost:8080/fraud-analysis/{customerId}`
- Exemplos de clientes:
    - `44444444-4444-4444-4444-444444444444` → HIGH_RISK
    - `55555555-5555-5555-5555-555555555555` → PREFERENTIAL
    - `66666666-6666-6666-6666-666666666666` → NO_INFORMATION
    - Outros → REGULAR

---

## Observabilidade

- Logs estruturados com SLF4J