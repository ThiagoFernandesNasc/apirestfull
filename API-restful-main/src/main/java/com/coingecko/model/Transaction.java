package com.coingecko.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Portfólio é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "hibernateLazyInitializer", "handler"})
    private Portfolio portfolio;
    
    @NotNull(message = "Criptomoeda é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id", nullable = false)
    private Crypto crypto;
    
    @NotNull(message = "Tipo de transação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantidade deve ser maior que zero")
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;
    
    @NotNull(message = "Preço por unidade é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal pricePerUnit;
    
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor total deve ser maior que zero")
    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal totalValue;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Construtores
    public Transaction() {}
    
    public Transaction(Portfolio portfolio, Crypto crypto, TransactionType type, 
                      BigDecimal quantity, BigDecimal pricePerUnit, String notes) {
        this.portfolio = portfolio;
        this.crypto = crypto;
        this.type = type;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalValue = quantity.multiply(pricePerUnit);
        this.notes = notes;
        this.transactionDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Portfolio getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    
    public Crypto getCrypto() {
        return crypto;
    }
    
    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        // Recalcular valor total quando quantidade mudar
        if (this.pricePerUnit != null) {
            this.totalValue = this.quantity.multiply(this.pricePerUnit);
        }
    }
    
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }
    
    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        // Recalcular valor total quando preço mudar
        if (this.quantity != null) {
            this.totalValue = this.quantity.multiply(this.pricePerUnit);
        }
    }
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
    }
    
    public enum TransactionType {
        BUY("Compra"),
        SELL("Venda");
        
        private final String description;
        
        TransactionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

