Sistema de Autorização de Pagamentos

Descrição
Sistema de autorização de pagamentos que simula a comunicação entre um terminal TEF e uma rede adquirente, com API REST e servidor socket.

Funcionalidades
Processamento de transações com cartão de crédito

Validação de valores (pares/ímpares, negativos, timeout)

Geração de códigos de autorização

Comunicação via sockets (protocolo ISO8583 simplificado)

API REST para integração

Regras de Negócio
Valores pares: APROVADO (código 000)
Valores ímpares: NEGADO (código 051)
Valores negativos: NEGADO AUTOMÁTICO
Valores > 1000: Timeout

Pré-requisitos
Java 17+
Maven 3.6+
Postman (para testes)

Como Executar

mvn clean install

Iniciar servidor autorizador:
mvn exec:java

Iniciar API REST:
mvn spring-boot:run

Para testar, faça uma requisição POST no Postman com essa URL: http://localhost:8080/authorization

Adicione no header os seguintes parâmetros:

KEY            VALUE
Content-Type - application/json
x-identifier - 123456789

Insira o corpo abaixo, e faça alterações no valor para verificar a validação.

{
  "external_id": "",
  "value": 200,
  "cardNumber": "",
  "installments": 2,
  "cvv": "",
  "expMonth": 11,
  "expYear": 28,
  "holder_name": "Destaxa"
}

Exemplo de Log do Servidor:

=== REQUISIÇÃO RECEBIDA ===
Mensagem: 0200|4111111111111111|2380|003001|2|123|1128

=== DETALHES ===
Valor: R$ 23,80
Status: APROVADO
Código: 000
Autorização: 123456
Data: 15/05/2025
Hora: 14:30:45