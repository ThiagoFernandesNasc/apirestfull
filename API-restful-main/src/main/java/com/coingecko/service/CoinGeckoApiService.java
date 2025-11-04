package com.coingecko.service;

import com.coingecko.model.Crypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CoinGeckoApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoApiService.class);
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3";
    
    // Lista expandida de criptomoedas populares (top 30+)
    private static final String[] SUPPORTED_COINS = {
        "bitcoin", "ethereum", "binancecoin", "cardano", "solana",
        "ripple", "polkadot", "dogecoin", "avalanche-2", "shiba-inu",
        "tron", "chainlink", "polygon", "litecoin", "uniswap",
        "bitcoin-cash", "stellar", "ethereum-classic", "monero", "cosmos",
        "algorand", "vechain", "filecoin", "theta-token", "aave",
        "eos", "tezos", "axie-infinity", "the-sandbox", "decentraland",
        "gala", "enjincoin", "mana", "flow", "near"
    };
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public CoinGeckoApiService() {
        this.webClient = WebClient.builder()
                .baseUrl(COINGECKO_API_URL)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Cacheable(value = "cryptoData", key = "#coinId")
    public Optional<Crypto> getCryptoData(String coinId) {
        try {
            logger.info("Buscando dados para: {}", coinId);
            
            Mono<String> response = webClient.get()
                    .uri("/coins/{id}?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false", coinId)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            
            if (jsonResponse != null) {
                return parseCryptoData(jsonResponse);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar dados para {}: {}", coinId, e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Crypto> getAllSupportedCryptoData() {
        List<Crypto> cryptos = new ArrayList<>();
        
        for (String coinId : SUPPORTED_COINS) {
            getCryptoData(coinId).ifPresent(cryptos::add);
        }
        
        return cryptos;
    }
    
    /**
     * Busca dados de mercado das criptomoedas suportadas
     */
    public List<Crypto> getMarketData() {
        return getMarketData(SUPPORTED_COINS.length);
    }
    
    /**
     * Busca dados de mercado de múltiplas criptomoedas
     * @param limit Número máximo de criptomoedas a retornar (padrão: todas as suportadas)
     */
    public List<Crypto> getMarketData(int limit) {
        try {
            logger.info("Buscando dados de mercado para até {} criptomoedas", limit);
            
            String coinIds = String.join(",", SUPPORTED_COINS);
            String url = "/coins/markets?vs_currency=usd&ids=" + coinIds + "&order=market_cap_desc&per_page=" + Math.min(limit, 250) + "&page=1&sparkline=false&price_change_percentage=24h";
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                        clientResponse -> {
                            logger.error("Erro HTTP {} ao buscar dados de mercado", clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Erro HTTP: " + clientResponse.statusCode()));
                        })
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                logger.warn("Resposta vazia da API CoinGecko");
                return new ArrayList<>();
            }
            
            // Verificar se é um erro JSON
            if (jsonResponse.trim().startsWith("{") && (jsonResponse.contains("\"error\"") || jsonResponse.contains("\"status\""))) {
                logger.error("API retornou erro: {}", jsonResponse);
                return new ArrayList<>();
            }
            
            logger.info("Resposta da API recebida ({} caracteres)", jsonResponse.length());
            List<Crypto> result = parseMarketData(jsonResponse);
            
            if (result.isEmpty()) {
                logger.warn("Nenhuma criptomoeda parseada da resposta. Resposta: {}", 
                    jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
            } else {
                logger.info("Parseados {} criptomoedas com sucesso", result.size());
            }
            
            return result;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            logger.error("Erro HTTP {} ao buscar dados de mercado: {}", e.getStatusCode(), e.getMessage());
            
            // Rate limiting (429)
            if (e.getStatusCode() != null && e.getStatusCode().value() == 429) {
                logger.warn("Rate limit excedido na API CoinGecko. Aguarde alguns minutos.");
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Erro ao buscar dados de mercado: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    private Optional<Crypto> parseCryptoData(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            Crypto crypto = new Crypto();
            crypto.setName(root.get("name").asText());
            crypto.setSymbol(root.get("symbol").asText().toUpperCase());
            
            JsonNode marketData = root.get("market_data");
            if (marketData != null) {
                crypto.setCurrentPrice(new BigDecimal(marketData.get("current_price").get("usd").asText()));
                crypto.setMarketCap(new BigDecimal(marketData.get("market_cap").get("usd").asText()));
                crypto.setVolume24h(new BigDecimal(marketData.get("total_volume").get("usd").asText()));
                
                JsonNode priceChange = marketData.get("price_change_percentage_24h");
                if (priceChange != null) {
                    crypto.setChange24h(new BigDecimal(priceChange.asText()));
                }
            }
            
            JsonNode description = root.get("description");
            if (description != null && description.get("en") != null) {
                crypto.setDescription(description.get("en").asText());
            }
            
            // URL da imagem
            JsonNode image = root.get("image");
            if (image != null && !image.isNull()) {
                JsonNode imageSmall = image.get("small");
                if (imageSmall != null && !imageSmall.isNull()) {
                    crypto.setImageUrl(imageSmall.asText());
                } else if (image.isTextual()) {
                    crypto.setImageUrl(image.asText());
                }
            }
            
            logger.info("Dados parseados para: {} - Preço: ${}", crypto.getName(), crypto.getCurrentPrice());
            return Optional.of(crypto);
            
        } catch (Exception e) {
            logger.error("Erro ao fazer parse dos dados: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    private List<Crypto> parseMarketData(String jsonResponse) {
        List<Crypto> cryptos = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Verificar se é um array
            if (!root.isArray()) {
                logger.error("Resposta não é um array. Tipo: {}, Conteúdo: {}", 
                    root.getNodeType(), jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
                return cryptos;
            }
            
            // Verificar se array está vazio
            if (root.size() == 0) {
                logger.warn("Array vazio retornado da API");
                return cryptos;
            }
            
            for (JsonNode node : root) {
                try {
                    Crypto crypto = new Crypto();
                    
                    // Validar campos obrigatórios
                    if (!node.has("name") || !node.has("symbol") || !node.has("current_price")) {
                        logger.warn("Nó sem campos obrigatórios, pulando: {}", node.toString());
                        continue;
                    }
                    
                    crypto.setName(node.get("name").asText());
                    crypto.setSymbol(node.get("symbol").asText().toUpperCase());
                    
                    // Preço atual
                    JsonNode currentPrice = node.get("current_price");
                    if (currentPrice != null && !currentPrice.isNull()) {
                        crypto.setCurrentPrice(new BigDecimal(currentPrice.asText()));
                    } else {
                        logger.warn("Preço atual não encontrado para {}", crypto.getName());
                        continue;
                    }
                    
                    // Market Cap
                    JsonNode marketCap = node.get("market_cap");
                    if (marketCap != null && !marketCap.isNull()) {
                        crypto.setMarketCap(new BigDecimal(marketCap.asText()));
                    }
                    
                    // Volume 24h
                    JsonNode totalVolume = node.get("total_volume");
                    if (totalVolume != null && !totalVolume.isNull()) {
                        crypto.setVolume24h(new BigDecimal(totalVolume.asText()));
                    }
                    
                    // Mudança 24h
                    JsonNode priceChange = node.get("price_change_percentage_24h");
                    if (priceChange != null && !priceChange.isNull()) {
                        crypto.setChange24h(new BigDecimal(priceChange.asText()));
                    }
                    
                    // URL da imagem
                    JsonNode image = node.get("image");
                    if (image != null && !image.isNull()) {
                        crypto.setImageUrl(image.asText());
                    }
                    
                    cryptos.add(crypto);
                    logger.debug("Parseado: {} ({}) - ${}", crypto.getName(), crypto.getSymbol(), crypto.getCurrentPrice());
                    
                } catch (Exception e) {
                    logger.error("Erro ao parsear item do array: {}", e.getMessage());
                }
            }
            
            logger.info("Parseados {} criptomoedas do mercado com sucesso", cryptos.size());
            
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Erro ao fazer parse JSON: {}. Resposta: {}", e.getMessage(), 
                jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
        } catch (Exception e) {
            logger.error("Erro ao fazer parse dos dados de mercado: {}", e.getMessage(), e);
        }
        
        return cryptos;
    }
    
    private Boolean cachedHealthStatus = null;
    private long lastHealthCheck = 0;
    private static final long HEALTH_CHECK_CACHE_DURATION = 60000; // 1 minuto
    
    @Cacheable(value = "coinGeckoApi", key = "'health'")
    public boolean isHealthy() {
        long now = System.currentTimeMillis();
        
        // Usar cache para evitar requisições excessivas
        if (cachedHealthStatus != null && (now - lastHealthCheck) < HEALTH_CHECK_CACHE_DURATION) {
            logger.debug("Retornando status de saúde do cache");
            return cachedHealthStatus;
        }
        
        try {
            Mono<String> response = webClient.get()
                    .uri("/ping")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            logger.warn("Erro HTTP {} ao verificar saúde da API", clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Erro HTTP: " + clientResponse.statusCode()));
                        })
                    .bodyToMono(String.class);
            
            String result = response.block();
            boolean healthy = result != null && result.contains("To the Moon");
            
            // Atualizar cache
            cachedHealthStatus = healthy;
            lastHealthCheck = now;
            
            if (healthy) {
                logger.debug("API CoinGecko está funcionando");
            } else {
                logger.warn("API CoinGecko não está respondendo corretamente");
            }
            
            return healthy;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Rate limiting - não logar como erro, apenas cache como false
            if (e.getStatusCode() != null && e.getStatusCode().value() == 429) {
                logger.debug("Rate limit ao verificar saúde - usando cache");
                if (cachedHealthStatus == null) {
                    cachedHealthStatus = false;
                    lastHealthCheck = now;
                }
                return cachedHealthStatus != null ? cachedHealthStatus : false;
            }
            
            logger.error("Erro HTTP {} ao verificar saúde da API: {}", e.getStatusCode(), e.getMessage());
            cachedHealthStatus = false;
            lastHealthCheck = now;
            return false;
            
        } catch (Exception e) {
            logger.error("Erro ao verificar saúde da API: {}", e.getMessage());
            cachedHealthStatus = false;
            lastHealthCheck = now;
            return false;
        }
    }
    
    /**
     * Busca preços simples de múltiplas criptomoedas
     * Endpoint: /simple/price
     * Formato mais compacto e rápido que /coins/markets
     * Retorna formato: { "bitcoin": { "usd": 106922, "usd_market_cap": ..., ... } }
     */
    public Map<String, Object> getSimplePrice(String coinIds, String vsCurrency) {
        try {
            logger.info("Buscando preços simples para: {} vs {}", coinIds, vsCurrency);
            
            String url = "/simple/price?ids=" + coinIds + "&vs_currencies=" + vsCurrency + 
                        "&include_market_cap=true&include_24hr_vol=true&include_24hr_change=true&include_last_updated_at=true";
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonNode root = objectMapper.readTree(jsonResponse);
                Map<String, Object> result = new HashMap<>();
                
                // Converter JsonNode para Map mantendo o formato original
                root.fields().forEachRemaining(entry -> {
                    JsonNode coinData = entry.getValue();
                    Map<String, Object> coinInfo = new HashMap<>();
                    
                    String currencyKey = vsCurrency.toLowerCase();
                    
                    // Preço
                    if (coinData.has(currencyKey)) {
                        coinInfo.put(currencyKey, coinData.get(currencyKey).asDouble());
                    }
                    
                    // Market Cap
                    if (coinData.has(currencyKey + "_market_cap")) {
                        coinInfo.put(currencyKey + "_market_cap", coinData.get(currencyKey + "_market_cap").asDouble());
                    }
                    
                    // Volume 24h
                    if (coinData.has(currencyKey + "_24h_vol")) {
                        coinInfo.put(currencyKey + "_24h_vol", coinData.get(currencyKey + "_24h_vol").asDouble());
                    }
                    
                    // Mudança 24h
                    if (coinData.has(currencyKey + "_24h_change")) {
                        coinInfo.put(currencyKey + "_24h_change", coinData.get(currencyKey + "_24h_change").asDouble());
                    }
                    
                    // Última atualização
                    if (coinData.has("last_updated_at")) {
                        coinInfo.put("last_updated_at", coinData.get("last_updated_at").asLong());
                    }
                    
                    result.put(entry.getKey(), coinInfo);
                });
                
                logger.info("Preços simples obtidos para {} moedas", result.size());
                return result;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar preços simples: {}", e.getMessage(), e);
        }
        
        return new HashMap<>();
    }
    
    /**
     * Busca criptomoedas, NFTs e categorias
     * Endpoint: /search
     * Útil para buscar por nome ou símbolo
     */
    public Map<String, Object> search(String query) {
        try {
            logger.info("Buscando: {}", query);
            
            String url = "/search?query=" + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonNode root = objectMapper.readTree(jsonResponse);
                Map<String, Object> result = new HashMap<>();
                
                // Processar coins
                if (root.has("coins")) {
                    List<Map<String, Object>> coins = new ArrayList<>();
                    root.get("coins").forEach(coin -> {
                        Map<String, Object> coinInfo = new HashMap<>();
                        if (coin.has("item")) {
                            JsonNode item = coin.get("item");
                            coinInfo.put("id", item.has("id") ? item.get("id").asText() : null);
                            coinInfo.put("name", item.has("name") ? item.get("name").asText() : null);
                            coinInfo.put("symbol", item.has("symbol") ? item.get("symbol").asText() : null);
                            coinInfo.put("market_cap_rank", item.has("market_cap_rank") ? item.get("market_cap_rank").asInt() : null);
                            
                            if (item.has("data")) {
                                JsonNode data = item.get("data");
                                if (data.has("price")) {
                                    coinInfo.put("price", data.get("price").asDouble());
                                }
                                if (data.has("market_cap")) {
                                    coinInfo.put("market_cap", data.get("market_cap").asText());
                                }
                            }
                        }
                        coins.add(coinInfo);
                    });
                    result.put("coins", coins);
                }
                
                // Processar NFTs
                if (root.has("nfts")) {
                    List<Map<String, Object>> nfts = new ArrayList<>();
                    root.get("nfts").forEach(nft -> {
                        Map<String, Object> nftInfo = new HashMap<>();
                        nftInfo.put("id", nft.has("id") ? nft.get("id").asText() : null);
                        nftInfo.put("name", nft.has("name") ? nft.get("name").asText() : null);
                        nftInfo.put("symbol", nft.has("symbol") ? nft.get("symbol").asText() : null);
                        nfts.add(nftInfo);
                    });
                    result.put("nfts", nfts);
                }
                
                // Processar categories
                if (root.has("categories")) {
                    List<Map<String, Object>> categories = new ArrayList<>();
                    root.get("categories").forEach(category -> {
                        Map<String, Object> categoryInfo = new HashMap<>();
                        categoryInfo.put("id", category.has("id") ? category.get("id").asInt() : null);
                        categoryInfo.put("name", category.has("name") ? category.get("name").asText() : null);
                        categoryInfo.put("slug", category.has("slug") ? category.get("slug").asText() : null);
                        categories.add(categoryInfo);
                    });
                    result.put("categories", categories);
                }
                
                logger.info("Busca realizada: {} coins, {} nfts, {} categories", 
                           result.getOrDefault("coins", new ArrayList<>()).toString().length(),
                           result.getOrDefault("nfts", new ArrayList<>()).toString().length(),
                           result.getOrDefault("categories", new ArrayList<>()).toString().length());
                return result;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar: {}", e.getMessage(), e);
        }
        
        return new HashMap<>();
    }
    
    /**
     * Busca as top N criptomoedas por market cap (sem especificar IDs)
     * Útil para buscar as principais criptomoedas automaticamente
     */
    public List<Crypto> getTopMarketData(int limit) {
        try {
            logger.info("Buscando top {} criptomoedas por market cap", limit);
            
            // Não especificar IDs, apenas buscar as top por market cap
            String url = "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=" + Math.min(limit, 250) + "&page=1&sparkline=false&price_change_percentage=24h";
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                        clientResponse -> {
                            logger.error("Erro HTTP {} ao buscar top criptomoedas", clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Erro HTTP: " + clientResponse.statusCode()));
                        })
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                logger.warn("Resposta vazia da API CoinGecko (top market data)");
                return new ArrayList<>();
            }
            
            // Verificar se é um erro JSON
            if (jsonResponse.trim().startsWith("{") && (jsonResponse.contains("\"error\"") || jsonResponse.contains("\"status\""))) {
                logger.error("API retornou erro: {}", jsonResponse);
                return new ArrayList<>();
            }
            
            logger.info("Resposta da API recebida ({} caracteres) para top market data", jsonResponse.length());
            List<Crypto> result = parseMarketData(jsonResponse);
            
            if (result.isEmpty()) {
                logger.warn("Nenhuma criptomoeda parseada da resposta (top market data). Resposta: {}", 
                    jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
            } else {
                logger.info("Parseadas {} criptomoedas do top market com sucesso", result.size());
            }
            
            return result;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            logger.error("Erro HTTP {} ao buscar top criptomoedas: {}", e.getStatusCode(), e.getMessage());
            
            // Rate limiting (429)
            if (e.getStatusCode() != null && e.getStatusCode().value() == 429) {
                logger.warn("Rate limit excedido na API CoinGecko. Aguarde alguns minutos.");
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Erro ao buscar top criptomoedas: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
