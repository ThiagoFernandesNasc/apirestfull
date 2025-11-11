# Guia do Código – CryptoGecko API 

Este guia explica cada parte do projeto, para que você entenda a arquitetura, as responsabilidades de cada classe e como as peças se conectam.

---

## Visão Geral da Arquitetura

- **Framework**: Spring Boot (REST + JPA + Validation + WebSocket + Cache)
- **Camadas**:
  - **Controller**: expõe endpoints HTTP.
  - **Service**: regras de negócio e orquestração.
  - **Repository**: acesso ao banco (JPA/Hibernate).
  - **Model (Entity)**: mapeamento das tabelas (JPA) e validações (Jakarta Validation).
  - **Config**: configuração de Swagger/OpenAPI, Cache e WebSocket.
  - **Exception**: tratamento global de erros.
- **Integração externa**: CoinGecko (via `WebClient`).
- **Dados em tempo real**: agendamento com `@Scheduled` e broadcast via WebSocket.
- **Documentação**: Swagger UI.

Estrutura principal de pacotes:

- `com.coingecko`
  - `controller`
  - `service`
  - `repository`
  - `model`
  - `config`
  - `exception`

---

## Ponto de Entrada

- **`CryptoGeckoApplication`**
  - Anotações: `@SpringBootApplication`, `@EnableScheduling`, `@EnableCaching`.
  - Função: inicia a aplicação, habilita tarefas agendadas e caching.

---

## Camada Model (Entidades)

- **`Crypto`**
  - Tabela: `cryptos`.
  - Campos principais: `name`, `symbol`, `currentPrice`, `marketCap`, `volume24h`, `change24h`, `description`, `imageUrl`, `createdAt`, `updatedAt`.
  - Validações: obrigatoriedade, tamanhos, faixas (ex.: preço > 0).
  - Callbacks: `@PrePersist` e `@PreUpdate` para timestamps.

- **`Portfolio`**
  - Tabela: `portfolios`.
  - Campos: `name`, `description`, `totalValue`, `createdAt`, `updatedAt`.
  - Relações: `@OneToMany` com `Transaction` (lazy, mapeado por `portfolio`).

- **`Transaction`**
  - Tabela: `transactions`.
  - Relações: `@ManyToOne` com `Portfolio` e `Crypto`.
  - Campos: `type` (BUY/SELL), `quantity`, `pricePerUnit`, `totalValue`, `notes`, `transactionDate`, `createdAt`.
  - Regra útil: `setQuantity`/`setPricePerUnit` recalculam `totalValue`.

Por que isso importa? As entidades definem o contrato com o banco e centralizam validações – evitando salvar dados inválidos.

---

## Camada Repository (Acesso a Dados)

- **`CryptoRepository`**
  - Métodos de consulta por nome/símbolo.
  - Consultas por faixa: preço e variação 24h.
  - Ordenações por `marketCap`, `volume24h`, `change24h`.

- **`PortfolioRepository`**
  - Busca por nome, ordenação por `totalValue` e `createdAt`.

- **`TransactionRepository`**
  - Filtros por `portfolioId`, `cryptoId`, `type`.
  - Agregações úteis: somatórios de quantidade comprada/vendida e totais investidos/vendidos por portfólio.

Padrão seguido: Repositórios expõem apenas acesso a dados; lógica fica nos Services.

---

## Camada Service (Regras de Negócio)

- **`CryptoService`**
  - CRUD e buscas especializadas (por nome/símbolo/faixas).
  - Regras de consistência ao criar/atualizar (nome/símbolo únicos).

- **`PortfolioService`**
  - CRUD, buscas e ordenações.
  - `updateTotalValue`: recalcula o valor com base em transações (investido − vendido).
  - `getTotalInvested`/`getTotalSold`: somatórios via `TransactionRepository`.

- **`TransactionService`**
  - CRUD, filtros por portfólio/cripto/tipo.
  - Ao criar/atualizar/deletar, sincroniza o `totalValue` do `Portfolio`.

- **`CoinGeckoApiService`**
  - Cliente HTTP para CoinGecko usando `WebClient`.
  - Métodos: buscar preço por símbolo, buscar mercado (top), buscar detalhes, pesquisar por query, etc.
  - Parsing robusto com `ObjectMapper`; logs e tratamento de erros (inclui casos de rate limit 429).
  - Pode usar cache (`@Cacheable`) para reduzir chamadas externas.

- **`RealTimeDataService`**
  - Controla ciclo de atualizações em tempo real (`isRunning`).
  - `@Scheduled(fixedRate = 30000)`: a cada 30s, busca dados do mercado e:
    - Atualiza/insere `Crypto` no banco.
    - Notifica clientes via `WebSocketService`.
  - Operações auxiliares: `startRealTimeUpdates`, `stopRealTimeUpdates`, `isRealTimeUpdatesRunning`, `clearCache`, `syncInitialData`.

- **`WebSocketService`**
  - Envia mensagens no tópico `/topic/crypto-updates` usando `SimpMessagingTemplate`.
  - Eventos: `sendCryptoUpdate`, `sendMarketUpdate`, `sendStatusUpdate`, `sendErrorUpdate`.

Por que isso importa? A camada de Service é onde garantimos consistência, cálculos e integrações – mantendo Controllers enxutos.

---

## Camada Controller (Endpoints HTTP)

- **`CryptoController`** (`/api/cryptos`)
  - Endpoints clássicos de CRUD: listar, buscar por id, criar, atualizar, deletar.
  - Extras: busca por nome/símbolo, por faixas de preço/variação, ordenações, contagem, health.
  - Retornos com `ResponseEntity` e códigos HTTP apropriados.

- **`PortfolioController`** (`/api/portfolios`)
  - CRUD de portfólios.
  - Agregados: `total-invested`, `total-sold`, contagem.

- **`TransactionController`** (`/api/transactions`)
  - CRUD de transações.
  - Filtros por portfólio, cripto e tipo.
  - Agregados por portfólio (investido/vendido).

- **`RealTimeController`** (`/api/realtime`)
  - Controle do ciclo em tempo real: `start`, `stop`, `status`, `clear-cache`, `sync`.
  - Métodos para consumir CoinGecko via HTTP (ex.: `search`, `price`, `market-top`, etc.).
  - Útil para integrar frontend que exibe stream de preços.

- **`HomeController`**
  - Redireciona `/` para `index.html` e evita erro de `favicon.ico`.

- **`HealthController`**
  - `GET /api/health`: status, timestamp, mensagem.
  - `GET /api/`: mensagem de boas‑vindas e índice de endpoints úteis.

Por que isso importa? Controllers focam em traduzir HTTP ⇄ Serviço, validando entrada e definindo status de saída.

---

## Configurações

- **`OpenApiConfig`**
  - Configura o OpenAPI/Swagger: título, descrição, contato, licença, servidores.
  - Acesse a documentação interativa em: `/swagger-ui.html` (ou `/swagger-ui/index.html`).

- **`CacheConfig`**
  - Habilita cache (`@EnableCaching`) e define caches como `cryptoData`, `marketData`, `coinGeckoApi`.

- **`WebSocketConfig`**
  - Habilita STOMP: broker simples em `/topic`, prefixo de app `/app`.
  - Endpoints: `/ws` (com SockJS) e `/ws-native`.

- **`DataInitializer`**
  - Populariza o banco com dados de exemplo (cryptos, portfolios, transactions) quando vazio.

Por que isso importa? Essas configs fornecem infra: docs, desempenho (cache) e comunicação em tempo real (WebSocket).

---

## Tratamento Global de Exceções

- **`GlobalExceptionHandler`**
  - Converte exceções em respostas JSON padronizadas com `status`, `message`, `timestamp`.
  - Trata validações (`MethodArgumentNotValidException`) e falhas genéricas.
  - Melhora DX/UX: respostas consistentes e legíveis.

---

## Fluxo de Dados Típico

1. Cliente chama um endpoint em `Controller`.
2. Controller valida entrada e delega para `Service`.
3. Service aplica regras e consulta/salva via `Repository`.
4. Entidades são persistidas pelo JPA/Hibernate.
5. Service retorna o resultado ao Controller, que monta `ResponseEntity` com o status adequado.
6. Em tempo real, `RealTimeDataService` atualiza o banco e usa `WebSocketService` para notificar clientes.

---

## WebSocket – Como consumir

- Conecte ao endpoint: `/ws` (SockJS) ou `/ws-native` (WebSocket puro).
- Assine o tópico: `/topic/crypto-updates`.
- Receba mensagens JSON com tipos: `crypto_update`, `market_update`, `status_update`, `error`.

Exemplo simples (pseudocódigo):

```javascript
const socket = new SockJS('/ws');
const stomp = Stomp.over(socket);
stomp.connect({}, () => {
  stomp.subscribe('/topic/crypto-updates', (msg) => console.log(JSON.parse(msg.body)));
});
```

---

## Endpoints Úteis (exemplos)

- Criptos: `GET /api/cryptos`, `GET /api/cryptos/{id}`, `POST /api/cryptos`, `PUT /api/cryptos/{id}`, `DELETE /api/cryptos/{id}`.
- Portfólios: `GET /api/portfolios`, `POST /api/portfolios`, agregados: `/total-invested`, `/total-sold`.
- Transações: `GET /api/transactions`, filtros por portfólio/cripto/tipo, agregados.
- Tempo real: `POST /api/realtime/start`, `POST /api/realtime/stop`, `GET /api/realtime/status`, `POST /api/realtime/clear-cache`, `POST /api/realtime/sync`.
- Saúde: `GET /api/health`.
- Swagger: `/swagger-ui.html`.

Dica: Use o Swagger UI para testar rapidamente todos os endpoints.

---

## Boas Práticas Encontradas no Projeto

- Separação clara de camadas e responsabilidades.
- Validações nas entidades para proteção de integridade.
- Serviços idempotentes e com verificações de existência.
- Tratamento de erros consistente no `GlobalExceptionHandler`.
- Logs detalhados no cliente da CoinGecko e nos serviços em tempo real.
- Uso de `@Transactional` nas services para garantir atomicidade.

---

## Como rodar e explorar

1. Build e run (ex.: via sua IDE ou `mvn spring-boot:run`).
2. Acesse `http://localhost:8080/swagger-ui.html` para testar a API.
3. Para tempo real:
   - Inicie: `POST /api/realtime/start`.
   - Assine o tópico no frontend: `/topic/crypto-updates`.

---

## Próximos Passos (sugestões de evolução)

- Persistência externa (PostgreSQL) e perfis de ambiente.
- Autenticação/Autorização (Spring Security) para endpoints sensíveis.
- Cálculo de valor atual do portfólio com base no preço de mercado ao vivo.
- Testes automatizados (unitários e de integração) para Services e Controllers.

---

Se quiser, posso personalizar este guia destacando os pontos mais relevantes para seus objetivos de estudo ou manutenção.

---

# Detalhamento por Pacote e por Classe (explicado como professor)

## Pacote: `com.coingecko` (raiz)

- **`CryptoGeckoApplication`**
  - Responsável por iniciar a aplicação Spring Boot.
  - Anotações:
    - `@SpringBootApplication`: varredura de componentes, auto-configuração e configuração base.
    - `@EnableScheduling`: liga o suporte a tarefas agendadas (usado por `RealTimeDataService`).
    - `@EnableCaching`: liga o suporte a cache (configurado em `CacheConfig`).

---

## Pacote: `com.coingecko.config`

- **`CacheConfig`**
  - Cria um `CacheManager` (`ConcurrentMapCacheManager`) com caches nomeados: `cryptoData`, `marketData`, `coinGeckoApi`.
  - Objetivo: reduzir chamadas externas (ex.: CoinGecko) e acelerar leituras repetidas.
  - Ponto pedagógico: caches simples em memória, ótimos para dev/testes; em produção, avalie Redis ou Caffeine.

- **`OpenApiConfig`**
  - Define um bean `OpenAPI` com título, descrição, versão, contato e licença.
  - Lista servidores (desenvolvimento e produção) exibidos na UI do Swagger.
  - Benefício: documentação interativa automática em `/swagger-ui.html`.

- **`WebSocketConfig`**
  - Habilita STOMP sobre WebSocket (`@EnableWebSocketMessageBroker`).
  - Broker simples para destinos iniciados por `/topic`.
  - Prefixo de envio de mensagens do app: `/app`.
  - Endpoints para clientes:
    - `/ws` com SockJS (fallback para navegadores antigos).
    - `/ws-native` WebSocket puro.

- **`DataInitializer`**
  - `CommandLineRunner` que popula dados de exemplo (cryptos, portfolios, transactions) apenas quando as tabelas estão vazias.
  - Ensina: como criar dados seed e como consultar por listas (`findBySymbolIn`, `findByNameIn`).

---

## Pacote: `com.coingecko.model` (Entidades)

- **`Crypto`**
  - Representa uma criptomoeda com campos de mercado (`currentPrice`, `marketCap`, `volume24h`, `change24h`).
  - Validações garantem integridade (ex.: preço > 0).
  - Tem `imageUrl` e `description` para enriquecer o front.
  - `@PrePersist`/`@PreUpdate` mantêm timestamps automáticos.

- **`Portfolio`**
  - Representa um portfólio do usuário com `name`, `description` e `totalValue`.
  - Relação `@OneToMany` com `Transaction` (lazy), ocultada com `@JsonIgnore` para evitar loops em JSON.
  - `totalValue` é recalculado pelo serviço conforme transações e preços atuais.

- **`Transaction`**
  - Representa uma operação de compra/venda.
  - Relações `@ManyToOne` com `Portfolio` e `Crypto`.
  - `TransactionType` enum: `BUY` e `SELL` (com descrição amigável).
  - Recalcula `totalValue` quando `quantity` ou `pricePerUnit` mudam.

---

## Pacote: `com.coingecko.repository` (Repositórios JPA)

- **`CryptoRepository`**
  - Consultas por `name`, `symbol`, busca parcial (`ContainingIgnoreCase`).
  - Filtros por faixa (`price`, `change24h`) e ordenações por `marketCap`, `volume24h`, `change24h`.
  - Ensina: uso de `@Query` com JPQL e `@Param`.

- **`PortfolioRepository`**
  - Busca por nome e ordenações (`totalValue`, `createdAt`).
  - Filtro por faixa de valor.

- **`TransactionRepository`**
  - Filtros por portfólio, cripto e tipo.
  - Consultas ordenadas por data.
  - Agregações: soma de quantidades compradas/vendidas e valores investidos/vendidos.

---

## Pacote: `com.coingecko.service` (Regras de Negócio)

- **`CryptoService`**
  - Lida com CRUD e buscas por nome/símbolo/faixas.
  - Regras de unicidade (nome/símbolo) antes de salvar/atualizar.
  - Demonstra boas práticas de validação no serviço (além da entidade).

- **`PortfolioService`**
  - CRUD e agregados do portfólio.
  - `updateTotalValue(Long portfolioId)`: calcula o valor de mercado atual do portfólio.
    - Agrupa transações por cripto, calcula quantidade líquida (BUY − SELL).
    - Multiplica quantidades líquidas positivas pelo `currentPrice` da cripto.
    - Atualiza `totalValue` com a soma.
  - `getTotalInvested`/`getTotalSold`: somas via `TransactionRepository` (úteis para relatórios históricos).

- **`TransactionService`**
  - CRUD de transações com validação de existência de `Portfolio` e `Crypto`.
  - Calcula `totalValue` quando ausente.
  - Após salvar/atualizar/deletar, chama `portfolioService.updateTotalValue` para manter o portfólio sincronizado.

- **`CoinGeckoApiService`**
  - Cliente HTTP reativo (`WebClient`) para a API CoinGecko.
  - Métodos principais:
    - `getMarketData`/`getTopMarketData`: busca lista de criptos (mercado).
    - `getSimplePrice`: preços compactos.
    - `search`: busca geral (coins, nfts, categorias).
    - `isHealthy`: pinga `/ping` para verificar disponibilidade (com cache leve).
  - Parsing com `ObjectMapper` e tratamento de respostas vazias/erros (inclusive rate limit 429).

- **`RealTimeDataService`**
  - Atualização periódica (a cada 30s) quando `isRunning=true`.
  - Fluxo:
    - Verifica saúde da CoinGecko.
    - Busca `marketData`.
    - Para cada cripto: atualiza/insere no repositório.
    - Notifica clientes via `WebSocketService`.
  - Operações: `startRealTimeUpdates`, `stopRealTimeUpdates`, `isRealTimeUpdatesRunning`, `clearCache`, `syncInitialData`.

- **`WebSocketService`**
  - Envia mensagens no tópico `/topic/crypto-updates` via `SimpMessagingTemplate`.
  - Formatos de mensagem: `crypto_update`, `market_update`, `status_update`, `error` com `timestamp` ISO.

---

## Pacote: `com.coingecko.controller` (API REST)

- **`CryptoController`** (`/api/cryptos`)
  - CRUD completo e buscas avançadas (parciais por nome/símbolo, faixas e ordenações).
  - Boas práticas: uso de `ResponseEntity`, códigos de status corretos, anotações Swagger (`@Operation`).

- **`PortfolioController`** (`/api/portfolios`)
  - CRUD, agregados (`/total-invested`, `/total-sold`) e ordenações.
  - Endpoint útil: `PUT /{id}/update-value` para recalcular valor atual (usa `PortfolioService`).

- **`TransactionController`** (`/api/transactions`)
  - CRUD e filtros por portfólio, cripto, tipo e período.
  - Agregados por portfólio e por cripto.

- **`RealTimeController`** (`/api/realtime`)
  - Controla ciclo de tempo real (start/stop/status), sincronização inicial e limpeza de cache.
  - Endpoints utilitários para testar CoinGecko e WebSocket.

- **`HealthController`**
  - `GET /api/health` e `GET /api/` com mensagens de status e índice de endpoints.

- **`HomeController`**
  - Redireciona `/` para `index.html` e evita 404 de `favicon.ico` retornando 204.

---

## Pacote: `com.coingecko.exception`

- **`GlobalExceptionHandler`**
  - Converte exceções em respostas JSON padronizadas.
  - Casos cobertos:
    - `RuntimeException` → 400 com mensagem.
    - `MethodArgumentNotValidException` → 400 com mapa de erros de validação.
    - `Exception` genérica → 500 com mensagem segura e log detalhado para debug.
  - Didática: separa preocupação de erro da lógica de negócio e padroniza o contrato de falhas.

---

## Como essas peças se encaixam (revisão)

- **Config** prepara a infraestrutura (Swagger, Cache, WebSocket, seeds).
- **Model** define o esquema e validações.
- **Repository** fornece acesso ao banco com queries reutilizáveis.
- **Service** centraliza regras (cálculo do valor atual, sincronizações, integrações externas).
- **Controller** expõe endpoints fazendo a ponte HTTP ⇄ Serviço.
- **Exception** garante respostas de erro consistentes.

