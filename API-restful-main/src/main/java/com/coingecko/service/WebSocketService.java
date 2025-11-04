package com.coingecko.service;

import com.coingecko.model.Crypto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private static final String WEBSOCKET_TOPIC = "/topic/crypto-updates";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void sendCryptoUpdate(Crypto crypto) {
        try {
            if (messagingTemplate == null) {
                logger.error("SimpMessagingTemplate não está disponível");
                return;
            }
            
            Map<String, Object> update = new HashMap<>();
            update.put("type", "crypto_update");
            // Usar String ao invés de LocalDateTime para evitar problemas de serialização
            update.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
            update.put("data", crypto);
            
            String message = objectMapper.writeValueAsString(update);
            messagingTemplate.convertAndSend(WEBSOCKET_TOPIC, message);
            
            logger.debug("Atualização enviada via WebSocket para: {} - Preço: ${}", 
                        crypto.getName(), crypto.getCurrentPrice());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar atualização via WebSocket para {}: {}", 
                        crypto.getName(), e.getMessage(), e);
        }
    }
    
    public void sendMarketUpdate(Map<String, Object> marketData) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "market_update");
            update.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
            update.put("data", marketData);
            
            String message = objectMapper.writeValueAsString(update);
            messagingTemplate.convertAndSend(WEBSOCKET_TOPIC, message);
            
            logger.debug("Atualização de mercado enviada via WebSocket");
            
        } catch (Exception e) {
            logger.error("Erro ao enviar atualização de mercado via WebSocket: {}", e.getMessage());
        }
    }
    
    public void sendStatusUpdate(String status, String message) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "status_update");
            update.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
            update.put("status", status);
            update.put("message", message);
            
            String jsonMessage = objectMapper.writeValueAsString(update);
            messagingTemplate.convertAndSend(WEBSOCKET_TOPIC, jsonMessage);
            
            logger.debug("Atualização de status enviada: {}", status);
            
        } catch (Exception e) {
            logger.error("Erro ao enviar atualização de status via WebSocket: {}", e.getMessage());
        }
    }
    
    public void sendErrorUpdate(String errorMessage) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "error");
            update.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
            update.put("error", errorMessage);
            
            String jsonMessage = objectMapper.writeValueAsString(update);
            messagingTemplate.convertAndSend(WEBSOCKET_TOPIC, jsonMessage);
            
            logger.debug("Erro enviado via WebSocket: {}", errorMessage);
            
        } catch (Exception e) {
            logger.error("Erro ao enviar erro via WebSocket: {}", e.getMessage());
        }
    }
}
