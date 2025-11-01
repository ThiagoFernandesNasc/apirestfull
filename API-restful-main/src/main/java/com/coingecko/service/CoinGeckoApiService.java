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
import java.util.List;
import java.util.Optional;

@Service
public class CoinGeckoApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoApiService.class);
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3";
    private static final String[] SUPPORTED_COINS = {"bitcoin", "ethereum", "binancecoin", "cardano", "solana"};
    
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
    
    public List<Crypto> getMarketData() {
        try {
            logger.info("Buscando dados de mercado para todas as moedas");
            
            String coinIds = String.join(",", SUPPORTED_COINS);
            String url = "/coins/markets?vs_currency=usd&ids=" + coinIds + "&order=market_cap_desc&per_page=100&page=1&sparkline=false&price_change_percentage=24h";
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String jsonResponse = response.block();
            logger.info("Resposta da API: {}", jsonResponse != null ? jsonResponse.substring(0, Math.min(200, jsonResponse.length())) : "null");
            
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                return parseMarketData(jsonResponse);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar dados de mercado: {}", e.getMessage(), e);
        }
        
        return new ArrayList<>();
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
            
            if (root.isArray()) {
                for (JsonNode node : root) {
                    Crypto crypto = new Crypto();
                    crypto.setName(node.get("name").asText());
                    crypto.setSymbol(node.get("symbol").asText().toUpperCase());
                    crypto.setCurrentPrice(new BigDecimal(node.get("current_price").asText()));
                    crypto.setMarketCap(new BigDecimal(node.get("market_cap").asText()));
                    crypto.setVolume24h(new BigDecimal(node.get("total_volume").asText()));
                    
                    JsonNode priceChange = node.get("price_change_percentage_24h");
                    if (priceChange != null) {
                        crypto.setChange24h(new BigDecimal(priceChange.asText()));
                    }
                    
                    cryptos.add(crypto);
                }
            }
            
            logger.info("Parseados {} criptomoedas do mercado", cryptos.size());
            
        } catch (Exception e) {
            logger.error("Erro ao fazer parse dos dados de mercado: {}", e.getMessage());
        }
        
        return cryptos;
    }
    
    public boolean isHealthy() {
        try {
            Mono<String> response = webClient.get()
                    .uri("/ping")
                    .retrieve()
                    .bodyToMono(String.class);
            
            String result = response.block();
            logger.info("Resposta da API CoinGecko: {}", result);
            return result != null && result.contains("To the Moon");
            
        } catch (Exception e) {
            logger.error("Erro ao verificar saúde da API: {}", e.getMessage());
            return false;
        }
    }
}
