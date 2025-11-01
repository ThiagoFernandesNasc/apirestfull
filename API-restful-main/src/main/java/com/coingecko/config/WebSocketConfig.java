package com.coingecko.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita um broker de mensagens simples para enviar mensagens para clientes
        config.enableSimpleBroker("/topic");
        
        // Define o prefixo para mensagens que vão para métodos anotados com @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint WebSocket que os clientes usarão para se conectar
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Endpoint alternativo sem SockJS para clientes que suportam WebSocket nativo
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }
}
