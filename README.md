# RevGas Chatbot

Backend inicial em Java para um chatbot comercial. A estrutura cobre a coleta de dados do cliente e do pedido, consulta segura ao ERP e encaminhamento para atendimento humano. A API do ERP e o provedor de IA ainda não estão conectados: faltam documentação, regras de negócio e credenciais fornecidas pela Flow/RevGas.

## Requisitos

- JDK 21 ou superior
- Maven 3.6.3 ou superior

## Executar localmente

```powershell
mvn spring-boot:run
```

O servidor inicia em `http://localhost:8080`. A rota `GET /actuator/health` informa o estado do serviço.

## Conversa

Envie uma mensagem para `POST /api/v1/chat`:

```json
{
  "conversationId": "atendimento-123",
  "message": "Quero pedir gás"
}
```

Na primeira mensagem, omita `conversationId`; a resposta cria um identificador e pergunta o nome. Reutilize esse identificador nas próximas mensagens. O protótipo coleta nome, produto, quantidade, endereço e forma de pagamento. O estado da conversa fica em memória e será perdido ao reiniciar o servidor.

## Estrutura

- `api`: contrato HTTP e validação de entrada
- `application`: fluxo de conversa e regras de atendimento
- `domain`: estado da conversa e dados do pedido
- `integration.erp`: porta de integração com ERP (a implementar com a documentação real)
- `integration.ai`: ponto de extensão para provedor de IA; o fluxo atual não envia dados a serviços externos

## Próximos passos para integrar a API

Documentação RevGás: [Autenticação e permissão](https://revgas.stoplight.io/docs/revgas-api/en39g0vb0qwsb-autenticacao-e-permissao). Para implementar os endpoints, precisamos da especificação OpenAPI/Swagger (JSON/YAML), coleção Postman exportada ou documentação pública com operações, modelos de requisição/resposta e exemplos. Não envie tokens, senhas, chaves privadas ou dados reais de clientes. Depois do mapeamento, as credenciais podem ser configuradas localmente por variáveis de ambiente a partir de `.env.example`.

Antes de ativar operações que criam pedidos ou alteram dados, precisamos confirmar autenticação, escopos, campos obrigatórios, regras de confirmação do cliente, tratamento de erros, limites e ambiente de homologação.

## GitHub

Repositório do projeto: https://github.com/PlanT50/Projeto-de-Extensao. Nunca adicione `.env` ou credenciais ao Git.
