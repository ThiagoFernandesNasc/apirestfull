package com.coingecko.service;

import com.coingecko.model.Portfolio;
import com.coingecko.repository.PortfolioRepository;
import com.coingecko.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public List<Portfolio> findAll() {
        return portfolioRepository.findAll();
    }
    
    public Optional<Portfolio> findById(Long id) {
        return portfolioRepository.findById(id);
    }
    
    public Optional<Portfolio> findByName(String name) {
        return portfolioRepository.findByName(name);
    }
    
    public List<Portfolio> searchByName(String name) {
        return portfolioRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Portfolio> findByValueRange(BigDecimal minValue, BigDecimal maxValue) {
        return portfolioRepository.findByValueRange(minValue, maxValue);
    }
    
    public List<Portfolio> findAllOrderByTotalValue() {
        return portfolioRepository.findAllOrderByTotalValueDesc();
    }
    
    public List<Portfolio> findAllOrderByCreatedAt() {
        return portfolioRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Portfolio save(Portfolio portfolio) {
        // Verificar se já existe um portfólio com o mesmo nome
        if (portfolioRepository.existsByName(portfolio.getName())) {
            throw new RuntimeException("Já existe um portfólio com o nome: " + portfolio.getName());
        }
        
        return portfolioRepository.save(portfolio);
    }
    
    public Portfolio update(Long id, Portfolio portfolioDetails) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Portfólio não encontrado com id: " + id));
        
        // Verificar se o novo nome já existe (se mudou)
        if (!portfolio.getName().equals(portfolioDetails.getName()) && 
            portfolioRepository.existsByName(portfolioDetails.getName())) {
            throw new RuntimeException("Já existe um portfólio com o nome: " + portfolioDetails.getName());
        }
        
        portfolio.setName(portfolioDetails.getName());
        portfolio.setDescription(portfolioDetails.getDescription());
        
        return portfolioRepository.save(portfolio);
    }
    
    public void deleteById(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new RuntimeException("Portfólio não encontrado com id: " + id);
        }
        portfolioRepository.deleteById(id);
    }
    
    public void updateTotalValue(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new RuntimeException("Portfólio não encontrado com id: " + portfolioId));
        
        List<com.coingecko.model.Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);
        java.util.Map<com.coingecko.model.Crypto, BigDecimal> netQuantities = new java.util.HashMap<>();
        
        for (com.coingecko.model.Transaction t : transactions) {
            com.coingecko.model.Crypto crypto = t.getCrypto();
            BigDecimal qty = netQuantities.getOrDefault(crypto, BigDecimal.ZERO);
            if (t.getType() == com.coingecko.model.Transaction.TransactionType.BUY) {
                qty = qty.add(t.getQuantity());
            } else if (t.getType() == com.coingecko.model.Transaction.TransactionType.SELL) {
                qty = qty.subtract(t.getQuantity());
            }
            netQuantities.put(crypto, qty);
        }
        
        BigDecimal currentValue = BigDecimal.ZERO;
        for (java.util.Map.Entry<com.coingecko.model.Crypto, BigDecimal> entry : netQuantities.entrySet()) {
            BigDecimal qty = entry.getValue();
            if (qty.signum() > 0) {
                BigDecimal price = entry.getKey().getCurrentPrice() != null ? entry.getKey().getCurrentPrice() : BigDecimal.ZERO;
                currentValue = currentValue.add(qty.multiply(price));
            }
        }
        
        portfolio.setTotalValue(currentValue);
        portfolioRepository.save(portfolio);
    }
    
    public BigDecimal getTotalInvested(Long portfolioId) {
        BigDecimal total = transactionRepository.getTotalInvested(portfolioId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalSold(Long portfolioId) {
        BigDecimal total = transactionRepository.getTotalSold(portfolioId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public boolean existsById(Long id) {
        return portfolioRepository.existsById(id);
    }
    
    public long count() {
        return portfolioRepository.count();
    }
}

