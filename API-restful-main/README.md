# CryptoGecko API

Uma API RESTful para gerenciamento de criptomoedas inspirada no CoinGecko, desenvolvida em Java com Spring Boot.

## 📋 Sobre o Projeto

O CryptoGecko API é um sistema completo para gerenciamento de criptomoedas que permite:

- **Gerenciar Criptomoedas**: Cadastrar, atualizar e consultar informações de criptomoedas
- **Gerenciar Portfólios**: Criar e gerenciar portfólios de investimento
- **Gerenciar Transações**: Registrar compras e vendas de criptomoedas
- **Análises**: Obter estatísticas e relatórios de investimentos

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Swagger/OpenAPI 3** (documentação)
- **Maven** (gerenciamento de dependências)
- **Jakarta Validation**

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/coingecko/
│   │   ├── config/          # Configurações
│   │   ├── controller/      # Controllers REST
│   │   ├── exception/       # Tratamento de exceções
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositórios JPA
│   │   └── service/         # Serviços de negócio
│   └── resources/
│       └── application.yml  # Configurações da aplicação
└── pom.xml                  # Dependências Maven
```

## 🛠️ Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para Execução

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd api
   ```

2. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a aplicação**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

## 📚 Documentação da API

### Endpoints Principais

#### Criptomoedas (`/api/cryptos`)
- `GET /api/cryptos` - Listar todas as criptomoedas
- `GET /api/cryptos/{id}` - Buscar por ID
- `GET /api/cryptos/symbol/{symbol}` - Buscar por símbolo
- `GET /api/cryptos/search/name?name={term}` - Buscar por nome
- `GET /api/cryptos/price-range?minPrice={min}&maxPrice={max}` - Filtrar por preço
- `GET /api/cryptos/top/market-cap` - Top por capitalização
- `POST /api/cryptos` - Criar nova criptomoeda
- `PUT /api/cryptos/{id}` - Atualizar criptomoeda
- `DELETE /api/cryptos/{id}` - Deletar criptomoeda

#### Portfólios (`/api/portfolios`)
- `GET /api/portfolios` - Listar todos os portfólios
- `GET /api/portfolios/{id}` - Buscar por ID
- `GET /api/portfolios/search/name?name={term}` - Buscar por nome
- `GET /api/portfolios/value-range?minValue={min}&maxValue={max}` - Filtrar por valor
- `POST /api/portfolios` - Criar novo portfólio
- `PUT /api/portfolios/{id}` - Atualizar portfólio
- `DELETE /api/portfolios/{id}` - Deletar portfólio

#### Transações (`/api/transactions`)
- `GET /api/transactions` - Listar todas as transações
- `GET /api/transactions/portfolio/{portfolioId}` - Por portfólio
- `GET /api/transactions/crypto/{cryptoId}` - Por criptomoeda
- `GET /api/transactions/type/{type}` - Por tipo (BUY/SELL)
- `GET /api/transactions/date-range?startDate={start}&endDate={end}` - Por período
- `POST /api/transactions` - Criar nova transação
- `PUT /api/transactions/{id}` - Atualizar transação
- `DELETE /api/transactions/{id}` - Deletar transação

### Exemplos de Uso

#### Criar uma Criptomoeda
```json
POST /api/cryptos
{
  "name": "Bitcoin",
  "symbol": "BTC",
  "currentPrice": 45000.00,
  "marketCap": 850000000000,
  "volume24h": 25000000000,
  "change24h": 2.5,
  "description": "A primeira criptomoeda do mundo"
}
```

#### Criar um Portfólio
```json
POST /api/portfolios
{
  "name": "Meu Portfólio",
  "description": "Portfólio de investimentos em criptomoedas"
}
```

#### Criar uma Transação
```json
POST /api/transactions
{
  "portfolio": {"id": 1},
  "crypto": {"id": 1},
  "type": "BUY",
  "quantity": 0.1,
  "pricePerUnit": 44000.00,
  "notes": "Primeira compra de Bitcoin"
}
```

## 🗄️ Banco de Dados

O projeto utiliza o H2 Database em memória para desenvolvimento, com dados de exemplo pré-carregados:

- **5 Criptomoedas** (Bitcoin, Ethereum, Binance Coin, Cardano, Solana)
- **3 Portfólios** (Conservador, Agressivo, DeFi)
- **4 Transações** de exemplo

### Acessar H2 Console
1. Acesse: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: `password`

## 🔧 Configurações

### application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  
  h2:
    console:
      enabled: true
      path: /h2-console

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

## 📊 Funcionalidades

### ✅ Implementadas
- [x] CRUD completo para Criptomoedas
- [x] CRUD completo para Portfólios  
- [x] CRUD completo para Transações
- [x] Validação de dados com Jakarta Validation
- [x] Tratamento global de exceções
- [x] Documentação automática com Swagger
- [x] Banco de dados H2 com dados de exemplo
- [x] Consultas avançadas e filtros
- [x] Cálculos automáticos de valores

### 🔄 Funcionalidades Futuras
- [ ] Autenticação e autorização
- [ ] Integração com APIs reais de criptomoedas
- [ ] Relatórios em PDF
- [ ] Notificações de preços
- [ ] Dashboard web
- [ ] Testes automatizados

## 🧪 Testando a API

### Usando Swagger UI
1. Acesse: http://localhost:8080/swagger-ui.html
2. Explore os endpoints disponíveis
3. Teste as operações diretamente na interface

### Usando cURL
```bash
# Listar criptomoedas
curl -X GET http://localhost:8080/api/cryptos

# Criar nova criptomoeda
curl -X POST http://localhost:8080/api/cryptos \
  -H "Content-Type: application/json" \
  -d '{"name":"Litecoin","symbol":"LTC","currentPrice":150.00}'

# Buscar por símbolo
curl -X GET http://localhost:8080/api/cryptos/symbol/BTC
```

## 👥 Equipe

Este projeto foi desenvolvido como parte de uma atividade acadêmica com foco em:
- Arquitetura de APIs RESTful
- Padrões de desenvolvimento com Spring Boot
- Gerenciamento de dados com JPA/Hibernate
- Documentação de APIs

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 🤝 Contribuições

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou suporte, entre em contato:
- Email: contato@cryptogecko.com
- GitHub Issues: [Criar uma issue](https://github.com/seu-usuario/crypto-gecko-api/issues)

---

**Desenvolvido com ❤️ pela equipe CryptoGecko**

