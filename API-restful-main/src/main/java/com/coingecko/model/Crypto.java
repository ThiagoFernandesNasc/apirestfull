package com.coingecko.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cryptos")
public class Crypto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Símbolo é obrigatório")
    @Size(max = 10, message = "Símbolo deve ter no máximo 10 caracteres")
    @Column(nullable = false, unique = true)
    private String symbol;
    
    @NotNull(message = "Preço atual é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal currentPrice;
    
    @DecimalMin(value = "0.0", message = "Capitalização de mercado deve ser não negativa")
    @Column(precision = 20, scale = 2)
    private BigDecimal marketCap;
    
    @DecimalMin(value = "0.0", message = "Volume 24h deve ser não negativo")
    @Column(precision = 20, scale = 2)
    private BigDecimal volume24h;
    
    @DecimalMin(value = "-100.0", message = "Variação 24h deve ser maior que -100%")
    @DecimalMax(value = "10000.0", message = "Variação 24h deve ser menor que 10000%")
    @Column(precision = 5, scale = 2)
    private BigDecimal change24h;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public Crypto() {}
    
    public Crypto(String name, String symbol, BigDecimal currentPrice) {
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
    
    public BigDecimal getVolume24h() {
        return volume24h;
    }
    
    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }
    
    public BigDecimal getChange24h() {
        return change24h;
    }
    
    public void setChange24h(BigDecimal change24h) {
        this.change24h = change24h;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

