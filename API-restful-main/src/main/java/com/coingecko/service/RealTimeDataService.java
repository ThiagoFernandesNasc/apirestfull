package com.coingecko.service;

import com.coingecko.model.Crypto;
import com.coingecko.repository.CryptoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Optional;

@Service
public class RealTimeDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealTimeDataService.class);
    
    @Autowired
    private CoinGeckoApiService coinGeckoApiService;
    
    @Autowired
    private CryptoRepository cryptoRepository;
    
    @Autowired
    private WebSocketService webSocketService;
    
    private boolean isRunning = false;
    
    // Inicializar automaticamente quando a aplicação iniciar
    @PostConstruct
    public void init() {
        logger.info("RealTimeDataService inicializado");
        // Não iniciar automaticamente, aguardar comando manual
    }
    
    @Scheduled(fixedRate = 30000) // A cada 30 segundos
    public void updateCryptoData() {
        if (!isRunning) {
            logger.debug("Atualizações em tempo real estão desabilitadas");
            return;
        }
        
        try {
            logger.info("Iniciando atualização de dados em tempo real...");
            
            // Verificar se a API está funcionando
            if (!coinGeckoApiService.isHealthy()) {
                logger.warn("API CoinGecko não está funcionando, pulando atualização");
                return;
            }
            
            List<Crypto> marketData = coinGeckoApiService.getMarketData();
            
            if (marketData == null || marketData.isEmpty()) {
                logger.warn("Nenhum dado de mercado recebido da API");
                return;
            }
            
            logger.info("Recebidos {} criptomoedas da API", marketData.size());
            
            int updatedCount = 0;
            for (Crypto apiCrypto : marketData) {
                try {
                    Optional<Crypto> existingCrypto = cryptoRepository.findBySymbol(apiCrypto.getSymbol());
                    
                    if (existingCrypto.isPresent()) {
                        Crypto crypto = existingCrypto.get();
                        
                        // Sempre atualizar os dados e enviar via WebSocket
                        crypto.setCurrentPrice(apiCrypto.getCurrentPrice());
                        crypto.setMarketCap(apiCrypto.getMarketCap());
                        crypto.setVolume24h(apiCrypto.getVolume24h());
                        crypto.setChange24h(apiCrypto.getChange24h());
                        
                        cryptoRepository.save(crypto);
                        logger.info("Atualizado: {} - Preço: ${}", crypto.getName(), crypto.getCurrentPrice());
                        
                        // Sempre enviar atualização via WebSocket
                        if (webSocketService != null) {
                            webSocketService.sendCryptoUpdate(crypto);
                            updatedCount++;
                        } else {
                            logger.error("WebSocketService não está disponível");
                        }
                    } else {
                        logger.debug("Criptomoeda {} não encontrada no banco local", apiCrypto.getSymbol());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar criptomoeda {}: {}", apiCrypto.getSymbol(), e.getMessage());
                }
            }
            
            logger.info("Atualização de dados concluída. {} criptomoedas processadas, {} enviadas via WebSocket.", 
                       marketData.size(), updatedCount);
            
        } catch (Exception e) {
            logger.error("Erro durante atualização de dados: {}", e.getMessage(), e);
        }
    }
    
    public void startRealTimeUpdates() {
        isRunning = true;
        logger.info("Atualizações em tempo real iniciadas");
    }
    
    public void stopRealTimeUpdates() {
        isRunning = false;
        logger.info("Atualizações em tempo real paradas");
    }
    
    public boolean isRealTimeUpdatesRunning() {
        return isRunning;
    }
    
    @CacheEvict(value = "cryptoData", allEntries = true)
    public void clearCache() {
        logger.info("Cache de dados de criptomoedas limpo");
    }
    
    @Transactional
    public void syncInitialData() {
        try {
            logger.info("Sincronizando dados iniciais com CoinGecko...");
            
            List<Crypto> marketData = coinGeckoApiService.getMarketData();
            
            for (Crypto apiCrypto : marketData) {
                Optional<Crypto> existingCrypto = cryptoRepository.findBySymbol(apiCrypto.getSymbol());
                
                if (existingCrypto.isPresent()) {
                    Crypto crypto = existingCrypto.get();
                    crypto.setCurrentPrice(apiCrypto.getCurrentPrice());
                    crypto.setMarketCap(apiCrypto.getMarketCap());
                    crypto.setVolume24h(apiCrypto.getVolume24h());
                    crypto.setChange24h(apiCrypto.getChange24h());
                    cryptoRepository.save(crypto);
                }
            }
            
            logger.info("Sincronização inicial concluída. {} criptomoedas atualizadas.", marketData.size());
            
        } catch (Exception e) {
            logger.error("Erro durante sincronização inicial: {}", e.getMessage());
        }
    }
}
