# 📊 Informações sobre API CoinGecko, H2 Database e Swagger

## 🔍 API CoinGecko - Status Atual

### ✅ Endpoints Já Implementados no Projeto

O projeto atualmente usa os seguintes endpoints da API CoinGecko:

1. **`/coins/{id}`** - Busca dados detalhados de uma criptomoeda específica
   - Usado em: `CoinGeckoApiService.getCryptoData()`
   - Retorna: nome, símbolo, preço, market cap, volume, variação 24h, descrição

2. **`/coins/markets`** - Busca dados de mercado de múltiplas criptomoedas
   - Usado em: `CoinGeckoApiService.getMarketData()`
   - Retorna: lista de criptomoedas com preços, market cap, volume, variação 24h
   - Suporta: Bitcoin, Ethereum, BNB, Cardano, Solana

3. **`/ping`** - Verifica se a API CoinGecko está funcionando
   - Usado em: `CoinGeckoApiService.isHealthy()`

### ❌ Endpoints NÃO Implementados (mas disponíveis na CoinGecko)

Os endpoints que você mostrou **NÃO estão implementados** ainda:

1. **`/simple/price`** - Preços simples em formato compacto
   ```json
   {
     "bitcoin": {
       "usd": 106922,
       "usd_market_cap": 2131964048665.609,
       "usd_24h_vol": 76019606756.07213,
       "usd_24h_change": -1.8636681597243139
     }
   }
   ```
   - **Vantagem**: Mais rápido, menos dados
   - **Uso**: Quando você só precisa de preços rápidos

2. **`/search`** - Busca de criptomoedas, NFTs e categorias
   ```json
   {
     "coins": [...],
     "nfts": [...],
     "categories": [...]
   }
   ```
   - **Vantagem**: Busca completa com sugestões
   - **Uso**: Quando você quer buscar por nome/símbolo

### 💡 Quer Implementar Esses Endpoints?

Se você quiser adicionar esses endpoints, posso ajudar a implementar:
- `/simple/price` - Para buscar preços de forma mais rápida
- `/search` - Para buscar criptomoedas por nome/símbolo

---

## 🗄️ H2 Database - Como Funciona

### O que é H2?
H2 é um banco de dados **relacional em memória** (in-memory database) escrito em Java. É perfeito para desenvolvimento e testes.

### Configuração no Projeto

**Arquivo:** `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  h2:
    console:
      enabled: true
      path: /h2-console
```

### Características do H2

✅ **Vantagens:**
- **Rápido**: Tudo em memória (RAM)
- **Não precisa instalar**: Já vem com Spring Boot
- **Fácil de usar**: Configuração simples
- **Ideal para desenvolvimento**: Testes rápidos

❌ **Desvantagens:**
- **Dados temporários**: Quando a aplicação para, os dados são perdidos
- **Não é para produção**: Usado apenas em desenvolvimento/testes

### Como Acessar o H2 Console

1. **Inicie a aplicação** (se ainda não iniciou)
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o H2 Console**
   - URL: http://localhost:8080/h2-console
   - Vai abrir uma tela de login

3. **Credenciais de Acesso**
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: `password`
   - Clique em **Connect**

4. **O que você pode fazer:**
   - Ver todas as tabelas: `SHOW TABLES;`
   - Ver dados: `SELECT * FROM CRYPTO;`
   - Executar queries SQL
   - Ver estrutura das tabelas

### Exemplo de Queries no H2 Console

```sql
-- Ver todas as criptomoedas
SELECT * FROM CRYPTO;

-- Ver portfólios
SELECT * FROM PORTFOLIO;

-- Ver transações
SELECT * FROM TRANSACTION;

-- Buscar Bitcoin
SELECT * FROM CRYPTO WHERE SYMBOL = 'BTC';

-- Ver portfólios ordenados por valor
SELECT * FROM PORTFOLIO ORDER BY TOTAL_VALUE DESC;
```

### ⚠️ Importante sobre H2

- **Dados são perdidos** quando você reinicia a aplicação
- O projeto usa `ddl-auto: create-drop` - isso recria as tabelas toda vez
- Dados de exemplo são carregados automaticamente pelo `DataInitializer`

---

## 📚 Swagger/OpenAPI - Como Funciona

### O que é Swagger?
Swagger é uma ferramenta que **gera documentação interativa** da sua API automaticamente. Permite testar os endpoints diretamente no navegador.

### Configuração no Projeto

**Arquivo:** `OpenApiConfig.java`
- Título: "CryptoGecko API"
- Descrição: Detalhes da API
- Versão: 1.0.0

**Arquivo:** `application.yml`
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true
```

### Como Acessar o Swagger

1. **Inicie a aplicação**
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o Swagger UI**
   - URL: http://localhost:8080/swagger-ui.html
   - Ou: http://localhost:8080/swagger-ui/index.html

3. **O que você verá:**
   - Lista de todos os endpoints organizados por tags
   - Documentação de cada endpoint
   - Schema dos modelos (Crypto, Portfolio, Transaction)
   - Botão "Try it out" para testar cada endpoint

### Como Usar o Swagger

1. **Explorar Endpoints:**
   - Expanda um endpoint (ex: `GET /api/cryptos`)
   - Veja a descrição, parâmetros, respostas

2. **Testar um Endpoint:**
   - Clique em **"Try it out"**
   - Preencha os parâmetros (se houver)
   - Clique em **"Execute"**
   - Veja a resposta em tempo real

3. **Criar um Registro:**
   - Escolha um endpoint `POST` (ex: `POST /api/cryptos`)
   - Clique em **"Try it out"**
   - Preencha o JSON do body
   - Clique em **"Execute"**
   - Veja o resultado

### Exemplo de Uso no Swagger

**Criar uma Criptomoeda:**
1. Vá para `POST /api/cryptos`
2. Clique em "Try it out"
3. Cole este JSON:
```json
{
  "name": "Litecoin",
  "symbol": "LTC",
  "currentPrice": 150.00,
  "marketCap": 10000000000,
  "volume24h": 500000000,
  "change24h": 2.5,
  "description": "Uma criptomoeda popular"
}
```
4. Clique em "Execute"
5. Veja a resposta (201 Created)

### Endpoints Disponíveis no Swagger

**Crypto Controller:**
- `GET /api/cryptos` - Listar todas
- `GET /api/cryptos/{id}` - Buscar por ID
- `POST /api/cryptos` - Criar nova
- `PUT /api/cryptos/{id}` - Atualizar
- `DELETE /api/cryptos/{id}` - Deletar
- E mais...

**Portfolio Controller:**
- `GET /api/portfolios` - Listar todos
- `POST /api/portfolios` - Criar novo
- E mais...

**Transaction Controller:**
- `GET /api/transactions` - Listar todas
- `POST /api/transactions` - Criar nova
- E mais...

**Real Time Controller:**
- `GET /api/realtime/test-api` - Testar API CoinGecko
- `POST /api/realtime/start` - Iniciar atualizações
- E mais...

### 📖 Documentação JSON (OpenAPI Spec)

Você também pode acessar a especificação OpenAPI em formato JSON:
- URL: http://localhost:8080/api-docs
- Útil para gerar clientes de API automaticamente

---

## 🔄 Resumo Rápido

| Ferramenta | URL | Uso |
|------------|-----|-----|
| **H2 Console** | http://localhost:8080/h2-console | Ver/editar dados do banco |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Testar/documentar API |
| **API Docs** | http://localhost:8080/api-docs | JSON da documentação |

---

## 💡 Dicas

1. **H2 Console**: Use para debug - veja se os dados estão sendo salvos corretamente
2. **Swagger**: Use para testar a API sem precisar de Postman ou cURL
3. **API CoinGecko**: Atualmente só usa `/coins/markets` - podemos adicionar `/simple/price` e `/search` se quiser

---

**Precisa de mais alguma coisa?** Posso ajudar a implementar os endpoints faltantes da CoinGecko!

