# Exemplos de Uso da CryptoGecko API

Este arquivo contém exemplos práticos de como usar a CryptoGecko API.

## Iniciando a Aplicação

```bash
# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

##  Acessando a Documentação

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

##  Exemplos de Criptomoedas

### Listar todas as criptomoedas
```bash
curl -X GET http://localhost:8080/api/cryptos
```

### Buscar Bitcoin por símbolo
```bash
curl -X GET http://localhost:8080/api/cryptos/symbol/BTC
```

### Buscar criptomoedas por nome
```bash
curl -X GET "http://localhost:8080/api/cryptos/search/name?name=bitcoin"
```

### Filtrar por faixa de preço
```bash
curl -X GET "http://localhost:8080/api/cryptos/price-range?minPrice=100&maxPrice=1000"
```

### Top criptomoedas por capitalização
```bash
curl -X GET http://localhost:8080/api/cryptos/top/market-cap
```

### Criar nova criptomoeda
```bash
curl -X POST http://localhost:8080/api/cryptos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Litecoin",
    "symbol": "LTC",
    "currentPrice": 150.00,
    "marketCap": 10000000000,
    "volume24h": 500000000,
    "change24h": 1.5,
    "description": "A prata digital"
  }'
```

##  Exemplos de Portfólios

### Listar todos os portfólios
```bash
curl -X GET http://localhost:8080/api/portfolios
```

### Buscar portfólio por nome
```bash
curl -X GET http://localhost:8080/api/portfolios/name/Portfólio%20Conservador
```

### Criar novo portfólio
```bash
curl -X POST http://localhost:8080/api/portfolios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Meu Portfólio",
    "description": "Portfólio pessoal de investimentos"
  }'
```

### Atualizar portfólio
```bash
curl -X PUT http://localhost:8080/api/portfolios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Portfólio Atualizado",
    "description": "Descrição atualizada do portfólio"
  }'
```

##  Exemplos de Transações

### Listar todas as transações
```bash
curl -X GET http://localhost:8080/api/transactions
```

### Buscar transações por portfólio
```bash
curl -X GET http://localhost:8080/api/transactions/portfolio/1
```

### Buscar transações por criptomoeda
```bash
curl -X GET http://localhost:8080/api/transactions/crypto/1
```

### Buscar transações por tipo
```bash
curl -X GET http://localhost:8080/api/transactions/type/BUY
```

### Buscar transações por período
```bash
curl -X GET "http://localhost:8080/api/transactions/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

### Criar nova transação de compra
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "portfolio": {"id": 1},
    "crypto": {"id": 1},
    "type": "BUY",
    "quantity": 0.5,
    "pricePerUnit": 44000.00,
    "notes": "Compra de Bitcoin para longo prazo"
  }'
```

### Criar nova transação de venda
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "portfolio": {"id": 1},
    "crypto": {"id": 2},
    "type": "SELL",
    "quantity": 1.0,
    "pricePerUnit": 3200.00,
    "notes": "Realização de lucros em Ethereum"
  }'
```

##  Exemplos de Análises

### Obter total investido em um portfólio
```bash
curl -X GET http://localhost:8080/api/portfolios/1/total-invested
```

### Obter total vendido de um portfólio
```bash
curl -X GET http://localhost:8080/api/portfolios/1/total-sold
```

### Obter quantidade total comprada de uma criptomoeda
```bash
curl -X GET http://localhost:8080/api/transactions/crypto/1/total-bought
```

### Obter quantidade total vendida de uma criptomoeda
```bash
curl -X GET http://localhost:8080/api/transactions/crypto/1/total-sold
```

### Atualizar valor total do portfólio
```bash
curl -X PUT http://localhost:8080/api/portfolios/1/update-value
```

##  Exemplos de Buscas Avançadas

### Buscar criptomoedas com variação positiva
```bash
curl -X GET "http://localhost:8080/api/cryptos/change-range?minChange=0&maxChange=100"
```

### Buscar portfólios com valor entre 1000 e 10000
```bash
curl -X GET "http://localhost:8080/api/portfolios/value-range?minValue=1000&maxValue=10000"
```

### Buscar transações de um portfólio específico no último mês
```bash
curl -X GET "http://localhost:8080/api/transactions/portfolio/1/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59"
```

##  Testando Validações

### Tentar criar criptomoeda com dados inválidos
```bash
curl -X POST http://localhost:8080/api/cryptos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "symbol": "",
    "currentPrice": -100
  }'
```

### Tentar criar transação com portfólio inexistente
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "portfolio": {"id": 999},
    "crypto": {"id": 1},
    "type": "BUY",
    "quantity": 0.1,
    "pricePerUnit": 44000.00
  }'
```

##  Dados de Exemplo Pré-carregados

A aplicação já vem com dados de exemplo:

### Criptomoedas
- **Bitcoin (BTC)**: $45,000.00
- **Ethereum (ETH)**: $3,200.00
- **Binance Coin (BNB)**: $320.00
- **Cardano (ADA)**: $0.45
- **Solana (SOL)**: $95.00

### Portfólios
- **Portfólio Conservador**: Foco em criptomoedas estáveis
- **Portfólio Agressivo**: Foco em criptomoedas de alto potencial
- **Portfólio DeFi**: Foco em protocolos DeFi

### Transações
- Compra de Bitcoin no portfólio conservador
- Compra de Ethereum no portfólio conservador
- Compra de Cardano no portfólio agressivo
- Venda de Bitcoin no portfólio agressivo

##  Próximos Passos

1. **Explore a documentação Swagger**: http://localhost:8080/swagger-ui.html
2. **Teste os endpoints**: Use os exemplos acima
3. **Verifique o banco H2**: http://localhost:8080/h2-console
4. **Experimente com seus próprios dados**: Crie suas próprias criptomoedas, portfólios e transações

##  Dicas Importantes

- Todos os valores monetários devem ser positivos
- As datas devem estar no formato ISO 8601
- Os IDs devem existir antes de criar relacionamentos
- Use o Swagger UI para uma experiência mais interativa
- O banco H2 é em memória, então os dados são perdidos ao reiniciar a aplicação

