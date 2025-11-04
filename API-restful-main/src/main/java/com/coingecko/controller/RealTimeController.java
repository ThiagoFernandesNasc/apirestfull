package com.coingecko.controller;

import com.coingecko.model.Crypto;
import com.coingecko.service.RealTimeDataService;
import com.coingecko.service.CoinGeckoApiService;
import com.coingecko.service.CryptoService;
import com.coingecko.service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/realtime")
@Tag(name = "Real Time Data", description = "API para dados em tempo real de criptomoedas")
public class RealTimeController {
    
    @Autowired
    private RealTimeDataService realTimeDataService;
    
    @Autowired
    private CoinGeckoApiService coinGeckoApiService;
    
    @Autowired
    private CryptoService cryptoService;
    
    @Autowired
    private WebSocketService webSocketService;
    
    @PostMapping("/start")
    @Operation(summary = "Iniciar atualizações em tempo real", 
               description = "Inicia as atualizações automáticas de dados a cada 30 segundos")
    public ResponseEntity<Map<String, Object>> startRealTimeUpdates() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            realTimeDataService.startRealTimeUpdates();
            realTimeDataService.syncInitialData();
            
            response.put("status", "success");
            response.put("message", "Atualizações em tempo real iniciadas");
            response.put("interval", "30 segundos");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao iniciar atualizações: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/stop")
    @Operation(summary = "Parar atualizações em tempo real", 
               description = "Para as atualizações automáticas de dados")
    public ResponseEntity<Map<String, Object>> stopRealTimeUpdates() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            realTimeDataService.stopRealTimeUpdates();
            
            response.put("status", "success");
            response.put("message", "Atualizações em tempo real paradas");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao parar atualizações: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/status")
    @Operation(summary = "Status das atualizações em tempo real", 
               description = "Verifica se as atualizações em tempo real estão ativas")
    public ResponseEntity<Map<String, Object>> getRealTimeStatus() {
        Map<String, Object> response = new HashMap<>();
        
        boolean isRunning = realTimeDataService.isRealTimeUpdatesRunning();
        boolean apiHealthy = coinGeckoApiService.isHealthy();
        
        response.put("realTimeUpdates", isRunning);
        response.put("apiHealthy", apiHealthy);
        response.put("interval", "30 segundos");
        response.put("websocketEndpoint", "/ws");
        response.put("websocketTopic", "/topic/crypto-updates");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sync")
    @Operation(summary = "Sincronizar dados iniciais", 
               description = "Sincroniza os dados locais com a CoinGecko API")
    public ResponseEntity<Map<String, Object>> syncInitialData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            realTimeDataService.syncInitialData();
            
            response.put("status", "success");
            response.put("message", "Dados sincronizados com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro na sincronização: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/clear-cache")
    @Operation(summary = "Limpar cache", 
               description = "Limpa o cache de dados de criptomoedas")
    public ResponseEntity<Map<String, Object>> clearCache() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            realTimeDataService.clearCache();
            
            response.put("status", "success");
            response.put("message", "Cache limpo com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao limpar cache: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api-health")
    @Operation(summary = "Verificar saúde da API CoinGecko", 
               description = "Verifica se a API CoinGecko está funcionando")
    public ResponseEntity<Map<String, Object>> checkApiHealth() {
        Map<String, Object> response = new HashMap<>();
        
        boolean isHealthy = coinGeckoApiService.isHealthy();
        
        response.put("healthy", isHealthy);
        response.put("apiUrl", "https://api.coingecko.com/api/v3");
        response.put("message", isHealthy ? "API CoinGecko funcionando" : "API CoinGecko com problemas");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-api")
    @Operation(summary = "Testar API CoinGecko", 
               description = "Testa se conseguimos buscar dados da API CoinGecko. Retorna as criptomoedas suportadas (padrão: 35+ criptomoedas)")
    public ResponseEntity<Map<String, Object>> testApi(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") Boolean topOnly) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar saúde da API primeiro
            boolean isHealthy = coinGeckoApiService.isHealthy();
            response.put("apiHealthy", isHealthy);
            
            if (!isHealthy) {
                response.put("success", false);
                response.put("count", 0);
                response.put("data", new ArrayList<>());
                response.put("message", "API CoinGecko não está respondendo. Pode estar com rate limiting ou indisponível.");
                response.put("suggestion", "Aguarde alguns minutos e tente novamente. A API CoinGecko tem limites de requisições.");
                return ResponseEntity.ok(response);
            }
            
            List<Crypto> marketData;
            if (Boolean.TRUE.equals(topOnly)) {
                // Buscar top N criptomoedas por market cap (sem especificar IDs)
                int topLimit = limit != null ? limit : 50;
                marketData = coinGeckoApiService.getTopMarketData(topLimit);
                response.put("mode", "top_by_market_cap");
            } else {
                // Buscar criptomoedas da lista suportada
                if (limit != null && limit > 0) {
                    marketData = coinGeckoApiService.getMarketData(limit);
                } else {
                    marketData = coinGeckoApiService.getMarketData();
                }
                response.put("mode", "supported_coins");
            }
            
            if (marketData == null || marketData.isEmpty()) {
                response.put("success", true);
                response.put("count", 0);
                response.put("data", new ArrayList<>());
                response.put("message", "API funcionando, mas nenhuma criptomoeda foi retornada. Pode ser rate limiting ou problema de conexão.");
                response.put("suggestion", "Aguarde alguns minutos antes de tentar novamente. Muitas requisições podem ter excedido o limite da API.");
                return ResponseEntity.ok(response);
            }
            
            response.put("success", true);
            response.put("count", marketData.size());
            response.put("data", marketData);
            response.put("message", "API funcionando - " + marketData.size() + " criptomoedas encontradas");
            if (limit != null) {
                response.put("requestedLimit", limit);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("count", 0);
            response.put("data", new ArrayList<>());
            response.put("message", "Erro ao testar API: " + e.getMessage());
            response.put("suggestion", "Verifique sua conexão com a internet e aguarde alguns minutos antes de tentar novamente.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/send-test-data")
    @Operation(summary = "Enviar dados de teste via WebSocket", 
               description = "Força o envio de dados via WebSocket para teste")
    public ResponseEntity<Map<String, Object>> sendTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar dados atuais do banco
            List<Crypto> cryptos = cryptoService.findAll();
            
            if (cryptos == null || cryptos.isEmpty()) {
                response.put("success", false);
                response.put("message", "Nenhuma criptomoeda encontrada no banco de dados");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Enviar cada criptomoeda via WebSocket
            int sentCount = 0;
            for (Crypto crypto : cryptos) {
                try {
                    webSocketService.sendCryptoUpdate(crypto);
                    sentCount++;
                } catch (Exception e) {
                    response.put("warning", "Erro ao enviar " + crypto.getName() + ": " + e.getMessage());
                }
            }
            
            response.put("success", true);
            response.put("count", cryptos.size());
            response.put("sent", sentCount);
            response.put("message", "Dados enviados via WebSocket: " + sentCount + "/" + cryptos.size() + " criptomoedas");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Erro ao enviar dados: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/test-websocket")
    @Operation(summary = "Testar conectividade WebSocket", 
               description = "Testa se o WebSocket está funcionando corretamente")
    public ResponseEntity<Map<String, Object>> testWebSocket() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Criar dados de teste
            Crypto testCrypto = new Crypto();
            testCrypto.setName("Bitcoin Test");
            testCrypto.setSymbol("BTC");
            testCrypto.setCurrentPrice(new java.math.BigDecimal("50000.00"));
            testCrypto.setMarketCap(new java.math.BigDecimal("1000000000"));
            testCrypto.setVolume24h(new java.math.BigDecimal("50000000"));
            testCrypto.setChange24h(new java.math.BigDecimal("2.5"));
            
            webSocketService.sendCryptoUpdate(testCrypto);
            
            response.put("success", true);
            response.put("message", "Dados de teste enviados via WebSocket");
            response.put("testData", testCrypto);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Erro ao testar WebSocket: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/simple-price")
    @Operation(summary = "Buscar preços simples", 
               description = "Busca preços simples de criptomoedas usando o endpoint /simple/price da CoinGecko. Formato mais compacto e rápido.")
    public ResponseEntity<Map<String, Object>> getSimplePrice(
            @RequestParam(defaultValue = "bitcoin,ethereum,binancecoin,cardano,solana") String ids,
            @RequestParam(defaultValue = "usd") String vsCurrency) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> simplePriceData = coinGeckoApiService.getSimplePrice(ids, vsCurrency);
            
            response.put("success", true);
            response.put("data", simplePriceData);
            response.put("count", simplePriceData.size());
            response.put("message", "Preços simples obtidos com sucesso");
            response.put("vs_currency", vsCurrency);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Erro ao buscar preços simples: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar criptomoedas, NFTs e categorias", 
               description = "Busca criptomoedas, NFTs e categorias usando o endpoint /search da CoinGecko. Útil para buscar por nome ou símbolo.")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (query == null || query.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Parâmetro 'query' é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> searchResults = coinGeckoApiService.search(query);
            
            response.put("success", true);
            response.put("data", searchResults);
            response.put("query", query);
            response.put("message", "Busca realizada com sucesso");
            
            int coinsCount = searchResults.containsKey("coins") ? 
                ((List<?>) searchResults.get("coins")).size() : 0;
            int nftsCount = searchResults.containsKey("nfts") ? 
                ((List<?>) searchResults.get("nfts")).size() : 0;
            int categoriesCount = searchResults.containsKey("categories") ? 
                ((List<?>) searchResults.get("categories")).size() : 0;
            
            response.put("counts", Map.of(
                "coins", coinsCount,
                "nfts", nftsCount,
                "categories", categoriesCount
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Erro ao buscar: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
