# 🦎 CryptoGecko API - Documentação para Apresentação

## 📋 Índice

1. [Visão Geral do Projeto](#visão-geral-do-projeto)
2. [Arquitetura e Tecnologias](#arquitetura-e-tecnologias)
3. [Funcionalidades Principais](#funcionalidades-principais)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Endpoints da API](#endpoints-da-api)
6. [Integração com CoinGecko](#integração-com-coingecko)
7. [Interface Web](#interface-web)
8. [Como Executar](#como-executar)
9. [Demonstração](#demonstração)
10. [Conclusão](#conclusão)

---

## 🎯 Visão Geral do Projeto

### O que é o CryptoGecko API?

O **CryptoGecko API** é uma API RESTful completa para gerenciamento de criptomoedas, desenvolvida em Java com Spring Boot. O sistema permite:

- ✅ Gerenciar informações de criptomoedas
- ✅ Criar e gerenciar portfólios de investimento
- ✅ Registrar transações de compra e venda
- ✅ Obter dados em tempo real da API CoinGecko
- ✅ Atualizações automáticas via WebSocket
- ✅ Interface web interativa para testes

### Objetivos do Projeto

- Demonstrar conhecimento em desenvolvimento de APIs RESTful
- Integração com APIs externas (CoinGecko)
- Implementação de WebSocket para dados em tempo real
- Documentação automática com Swagger/OpenAPI
- Interface web moderna e responsiva

---

## 🏗️ Arquitetura e Tecnologias

### Stack Tecnológico

#### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **Spring WebSocket** - Comunicação em tempo real
- **Spring WebFlux** - Cliente HTTP reativo
- **Jackson** - Serialização JSON
- **Jakarta Validation** - Validação de dados

#### Frontend
- **HTML5/CSS3** - Interface web
- **JavaScript (Vanilla)** - Interatividade
- **SockJS/STOMP** - WebSocket client

#### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Swagger/OpenAPI 3** - Documentação da API
- **Jetty** - Servidor web embutido

### Arquitetura da Aplicação

```
┌─────────────────────────────────────────────────────────┐
│                    Cliente (Browser)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  index.html  │  │ realtime.html│  │  Swagger UI  │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
└─────────┼──────────────────┼──────────────────┼────────┘
          │                  │                  │
          │ HTTP/REST        │ WebSocket        │ HTTP/REST
          │                  │                  │
┌─────────┴──────────────────┴──────────────────┴────────┐
│              Spring Boot Application                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │ Controllers  │  │   Services   │  │ Repositories │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
│         │                  │                  │         │
│  ┌──────┴──────────────────┴──────────────────┴──────┐ │
│  │              JPA / Hibernate                        │ │
│  └──────────────────────┬─────────────────────────────┘ │
└─────────────────────────┼───────────────────────────────┘
                           │
                    ┌──────┴──────┐
                    │  H2 Database │
                    │   (In-Memory)│
                    └──────────────┘
                           │
                           │ HTTP
                    ┌──────┴──────┐
                    │ CoinGecko API│
                    │  (External) │
                    └──────────────┘
```

---

## ⚡ Funcionalidades Principais

### 1. Gerenciamento de Criptomoedas

- **CRUD Completo**: Criar, ler, atualizar e deletar criptomoedas
- **Busca Avançada**: Por nome, símbolo, faixa de preço
- **Top por Market Cap**: Listar criptomoedas por capitalização
- **Imagens**: Exibição automática de logos das criptomoedas

### 2. Gerenciamento de Portfólios

- **Criar Portfólios**: Múltiplos portfólios de investimento
- **Valor Total**: Cálculo automático do valor do portfólio
- **Busca e Filtros**: Por nome, faixa de valor

### 3. Gerenciamento de Transações

- **Registrar Transações**: Compras e vendas de criptomoedas
- **Histórico**: Por portfólio, criptomoeda, tipo ou período
- **Cálculos Automáticos**: Valor total, preço médio

### 4. Integração com CoinGecko

- **Dados em Tempo Real**: Sincronização automática com CoinGecko
- **35+ Criptomoedas**: Suporte para principais moedas
- **Top Market Data**: Busca automática das top criptomoedas
- **Preços Simples**: Endpoint rápido para preços
- **Busca**: Endpoint de busca de criptomoedas, NFTs e categorias

### 5. WebSocket em Tempo Real

- **Atualizações Automáticas**: A cada 30 segundos
- **Push de Dados**: Envio automático via WebSocket
- **Status em Tempo Real**: Monitoramento do sistema

### 6. Documentação Interativa

- **Swagger UI**: Interface para testar endpoints
- **OpenAPI 3**: Especificação completa da API
- **Try it Out**: Testar endpoints diretamente no navegador

---

## 📁 Estrutura do Projeto

```
API-restful-main/
├── src/
│   ├── main/
│   │   ├── java/com/coingecko/
│   │   │   ├── config/              # Configurações
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── WebSocketConfig.java
│   │   │   ├── controller/          # Controllers REST
│   │   │   │   ├── CryptoController.java
│   │   │   │   ├── PortfolioController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── RealTimeController.java
│   │   │   │   ├── HealthController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── exception/           # Tratamento de erros
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── model/               # Entidades JPA
│   │   │   │   ├── Crypto.java
│   │   │   │   ├── Portfolio.java
│   │   │   │   └── Transaction.java
│   │   │   ├── repository/          # Repositórios JPA
│   │   │   │   ├── CryptoRepository.java
│   │   │   │   ├── PortfolioRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   └── service/             # Lógica de negócio
│   │   │       ├── CryptoService.java
│   │   │       ├── PortfolioService.java
│   │   │       ├── TransactionService.java
│   │   │       ├── CoinGeckoApiService.java
│   │   │       ├── RealTimeDataService.java
│   │   │       └── WebSocketService.java
│   │   └── resources/
│   │       ├── application.yml      # Configurações
│   │       └── static/              # Frontend
│   │           ├── index.html
│   │           └── realtime-test.html
│   └── pom.xml                      # Dependências Maven
└── README.md
```

### Padrões de Arquitetura

- **MVC (Model-View-Controller)**: Separação de responsabilidades
- **Repository Pattern**: Abstração de acesso a dados
- **Service Layer**: Lógica de negócio isolada
- **DTO Pattern**: Transferência de dados
- **Exception Handling**: Tratamento global de erros

---

## 🔌 Endpoints da API

### Endpoints de Criptomoedas (`/api/cryptos`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/cryptos` | Listar todas as criptomoedas |
| `GET` | `/api/cryptos/{id}` | Buscar por ID |
| `GET` | `/api/cryptos/symbol/{symbol}` | Buscar por símbolo (BTC, ETH, etc.) |
| `GET` | `/api/cryptos/search/name?name={term}` | Buscar por nome |
| `GET` | `/api/cryptos/price-range?minPrice={min}&maxPrice={max}` | Filtrar por faixa de preço |
| `GET` | `/api/cryptos/top/market-cap` | Top criptomoedas por market cap |
| `POST` | `/api/cryptos` | Criar nova criptomoeda |
| `PUT` | `/api/cryptos/{id}` | Atualizar criptomoeda |
| `DELETE` | `/api/cryptos/{id}` | Deletar criptomoeda |

### Endpoints de Portfólios (`/api/portfolios`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/portfolios` | Listar todos os portfólios |
| `GET` | `/api/portfolios/{id}` | Buscar por ID |
| `GET` | `/api/portfolios/search/name?name={term}` | Buscar por nome |
| `GET` | `/api/portfolios/value-range?minValue={min}&maxValue={max}` | Filtrar por valor |
| `POST` | `/api/portfolios` | Criar novo portfólio |
| `PUT` | `/api/portfolios/{id}` | Atualizar portfólio |
| `DELETE` | `/api/portfolios/{id}` | Deletar portfólio |

### Endpoints de Transações (`/api/transactions`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/transactions` | Listar todas as transações |
| `GET` | `/api/transactions/{id}` | Buscar por ID |
| `GET` | `/api/transactions/portfolio/{portfolioId}` | Transações por portfólio |
| `GET` | `/api/transactions/crypto/{cryptoId}` | Transações por criptomoeda |
| `GET` | `/api/transactions/type/{type}` | Transações por tipo (BUY/SELL) |
| `GET` | `/api/transactions/date-range?startDate={start}&endDate={end}` | Transações por período |
| `POST` | `/api/transactions` | Criar nova transação |
| `PUT` | `/api/transactions/{id}` | Atualizar transação |
| `DELETE` | `/api/transactions/{id}` | Deletar transação |

### Endpoints de Tempo Real (`/api/realtime`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/realtime/start` | Iniciar atualizações automáticas |
| `POST` | `/api/realtime/stop` | Parar atualizações |
| `GET` | `/api/realtime/status` | Status do sistema |
| `POST` | `/api/realtime/sync` | Sincronizar dados com CoinGecko |
| `GET` | `/api/realtime/test-api` | Testar API CoinGecko |
| `GET` | `/api/realtime/simple-price` | Preços simples (endpoint leve) |
| `GET` | `/api/realtime/search?query={term}` | Buscar criptomoedas/NFTs |
| `GET` | `/api/realtime/api-health` | Verificar saúde da API CoinGecko |
| `POST` | `/api/realtime/clear-cache` | Limpar cache |

### Endpoints de Saúde

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/health` | Status da aplicação |
| `GET` | `/` | Página inicial |

---

## 🌐 Integração com CoinGecko

### Endpoints Utilizados

O projeto integra com a API pública da CoinGecko:

1. **`/ping`** - Verificar saúde da API
2. **`/coins/markets`** - Dados de mercado de múltiplas criptomoedas
3. **`/coins/{id}`** - Dados detalhados de uma criptomoeda
4. **`/simple/price`** - Preços simples (formato compacto)
5. **`/search`** - Busca de criptomoedas, NFTs e categorias

### Criptomoedas Suportadas

O sistema suporta **35+ criptomoedas** populares:

- Bitcoin (BTC), Ethereum (ETH), Binance Coin (BNB)
- Cardano (ADA), Solana (SOL), Ripple (XRP)
- Polkadot (DOT), Dogecoin (DOGE), Avalanche (AVAX)
- Shiba Inu (SHIB), Tron (TRX), Chainlink (LINK)
- Polygon (MATIC), Litecoin (LTC), Uniswap (UNI)
- E mais 20+ criptomoedas...

### Funcionalidades de Integração

- ✅ **Cache Inteligente**: Reduz requisições à API CoinGecko
- ✅ **Rate Limiting**: Tratamento de limites de requisições
- ✅ **Fallback**: Resiliência a falhas da API externa
- ✅ **Atualizações Automáticas**: Sincronização a cada 30 segundos
- ✅ **Top Market Data**: Busca automática das principais criptomoedas

---

## 💻 Interface Web

### Página Principal (`index.html`)

- **Design Moderno**: Interface com gradiente e glassmorphism
- **Cards Informativos**: Apresentação visual das funcionalidades
- **Modal Interativo**: Exibição organizada dos dados da API
- **Testes Rápidos**: Botões para testar endpoints

### Página de Tempo Real (`realtime-test.html`)

- **WebSocket Client**: Conexão em tempo real
- **Cards de Criptomoedas**: Exibição visual com imagens
- **Log de Eventos**: Monitoramento de atividades
- **Controles**: Iniciar/parar atualizações

### Recursos Visuais

- **Imagens das Criptomoedas**: Logos exibidos automaticamente
- **Formatação de Valores**: Preços, market cap e volume formatados
- **Indicadores de Variação**: Verde para positivo, vermelho para negativo
- **Layout Responsivo**: Adapta-se a diferentes tamanhos de tela

---

## 🦎 Como Executar

### Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6** ou superior
- **Conexão com Internet** (para API CoinGecko)

### Passos para Execução

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd API-restful-main
   ```

2. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicação**
   ```bash
   # Windows
   .\run.bat
   
   # Linux/Mac
   ./run.sh
   
   # Ou diretamente
   mvn spring-boot:run
   ```

4. **Acesse a aplicação**
   - **Página Principal**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **H2 Console**: http://localhost:8080/h2-console
   - **Tempo Real**: http://localhost:8080/realtime-test.html

### Credenciais H2 Console

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

---

## 🎬 Demonstração

### Fluxo de Uso Típico

1. **Iniciar a Aplicação**
   - Executar `mvn spring-boot:run`
   - Aguardar inicialização completa

2. **Acessar Interface Web**
   - Abrir http://localhost:8080
   - Visualizar página principal

3. **Testar API CoinGecko**
   - Clicar em "Testar API CoinGecko"
   - Ver modal com 33+ criptomoedas e imagens

4. **Explorar Swagger**
   - Acessar http://localhost:8080/swagger-ui.html
   - Testar endpoints diretamente

5. **Testar WebSocket**
   - Acessar http://localhost:8080/realtime-test.html
   - Conectar WebSocket
   - Iniciar atualizações em tempo real

6. **Gerenciar Dados**
   - Criar portfólio via Swagger
   - Adicionar transações
   - Visualizar no H2 Console

### Exemplos de Requisições

#### Criar uma Criptomoeda
```bash
POST /api/cryptos
Content-Type: application/json

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
```bash
POST /api/portfolios
Content-Type: application/json

{
  "name": "Meu Portfólio",
  "description": "Portfólio de investimentos em criptomoedas"
}
```

#### Criar uma Transação
```bash
POST /api/transactions
Content-Type: application/json

{
  "portfolio": {"id": 1},
  "crypto": {"id": 1},
  "type": "BUY",
  "quantity": 0.1,
  "pricePerUnit": 44000.00,
  "notes": "Primeira compra de Bitcoin"
}
```

#### Testar API CoinGecko
```bash
GET /api/realtime/test-api
```

#### Buscar Top Criptomoedas
```bash
GET /api/realtime/test-api?topOnly=true&limit=50
```

---

## 📊 Dados de Exemplo

O projeto inclui dados pré-carregados:

- **5 Criptomoedas**: Bitcoin, Ethereum, BNB, Cardano, Solana
- **3 Portfólios**: Conservador, Agressivo, DeFi
- **4 Transações**: Exemplos de compras e vendas

---

## 🔒 Segurança e Validação

### Validações Implementadas

- **Campos Obrigatórios**: Validação com Jakarta Validation
- **Tipos de Dados**: Validação de tipos e formatos
- **Ranges**: Validação de valores mínimos/máximos
- **Tratamento de Erros**: Respostas padronizadas

### Tratamento de Exceções

- **GlobalExceptionHandler**: Tratamento centralizado
- **Mensagens Claras**: Erros informativos para o usuário
- **Códigos HTTP**: Uso correto de status codes

---

## 🎯 Diferenciais do Projeto

1. **Integração Completa**: API CoinGecko totalmente integrada
2. **WebSocket em Tempo Real**: Atualizações automáticas
3. **Interface Moderna**: Design responsivo e intuitivo
4. **Documentação Completa**: Swagger com exemplos
5. **Código Limpo**: Padrões e boas práticas
6. **Tratamento de Erros**: Sistema robusto de exceções
7. **Cache Inteligente**: Otimização de performance
8. **Múltiplas Criptomoedas**: Suporte para 35+ moedas

---

## 📈 Melhorias Futuras

### Possíveis Expansões

- [ ] Autenticação e autorização (JWT)
- [ ] Relatórios em PDF
- [ ] Gráficos de histórico de preços
- [ ] Notificações de alertas de preço
- [ ] Dashboard administrativo
- [ ] Testes automatizados (JUnit, Mockito)
- [ ] Deploy em produção (Docker, AWS)
- [ ] API Key para CoinGecko (mais requisições)

---

## 🛠️ Tecnologias e Bibliotecas

### Dependências Principais

```xml
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter WebSocket
- Spring Boot Starter WebFlux
- Spring Boot Starter Validation
- Spring Boot Starter Cache
- H2 Database
- Jackson (JSON)
- Swagger/OpenAPI
- SockJS/STOMP
```

### Versões

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Maven**: 3.x
- **H2**: Versão incluída no Spring Boot

---

## 📝 Conclusão

### Resumo do Projeto

O **CryptoGecko API** é um sistema completo e funcional que demonstra:

✅ **Arquitetura Profissional**: Padrões de design e organização
✅ **Integração Externa**: Consumo de APIs públicas
✅ **Tecnologias Modernas**: Stack atual e relevante
✅ **Interface Amigável**: Experiência de usuário agradável
✅ **Documentação Completa**: Fácil de entender e usar
✅ **Código de Qualidade**: Manutenível e escalável

### Aprendizados

- Desenvolvimento de APIs RESTful com Spring Boot
- Integração com APIs externas
- WebSocket para comunicação em tempo real
- Padrões de arquitetura e design
- Documentação de APIs
- Interface web moderna

### Resultado Final

Um sistema funcional, documentado e pronto para demonstração, que serve como base para projetos maiores e mais complexos.

---

## 📞 Informações de Contato

- **Projeto**: CryptoGecko API
- **Versão**: 1.0.0
- **Desenvolvedor**: [Seu Nome]
- **Data**: 2025

---

**Desenvolvido com ❤️ usando Spring Boot e Java**

---

## 📎 Anexos

### URLs Importantes

- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **API Docs JSON**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/api/health

### Comandos Úteis

```bash
# Compilar
mvn clean compile

# Executar
mvn spring-boot:run

# Executar testes
mvn test

# Gerar documentação
mvn javadoc:javadoc
```

---

**Fim do Documento**

