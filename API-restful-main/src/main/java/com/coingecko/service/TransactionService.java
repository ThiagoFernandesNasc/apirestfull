package com.coingecko.service;

import com.coingecko.model.Crypto;
import com.coingecko.model.Portfolio;
import com.coingecko.model.Transaction;
import com.coingecko.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private CryptoService cryptoService;
    
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
    
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public List<Transaction> findByPortfolioId(Long portfolioId) {
        return transactionRepository.findByPortfolioId(portfolioId);
    }
    
    public List<Transaction> findByCryptoId(Long cryptoId) {
        return transactionRepository.findByCryptoId(cryptoId);
    }
    
    public List<Transaction> findByType(Transaction.TransactionType type) {
        return transactionRepository.findByType(type);
    }
    
    public List<Transaction> findByPortfolioIdAndType(Long portfolioId, Transaction.TransactionType type) {
        return transactionRepository.findByPortfolioIdAndType(portfolioId, type);
    }
    
    public List<Transaction> findByCryptoIdAndType(Long cryptoId, Transaction.TransactionType type) {
        return transactionRepository.findByCryptoIdAndType(cryptoId, type);
    }
    
    public List<Transaction> findByPortfolioIdOrderByDate(Long portfolioId) {
        return transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolioId);
    }
    
    public List<Transaction> findByCryptoIdOrderByDate(Long cryptoId) {
        return transactionRepository.findByCryptoIdOrderByTransactionDateDesc(cryptoId);
    }
    
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }
    
    public List<Transaction> findByPortfolioIdAndDateRange(Long portfolioId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByPortfolioIdAndTransactionDateBetween(portfolioId, startDate, endDate);
    }
    
    public Transaction save(Transaction transaction) {
        // Verificar se o portfólio existe
        if (!portfolioService.existsById(transaction.getPortfolio().getId())) {
            throw new RuntimeException("Portfólio não encontrado com id: " + transaction.getPortfolio().getId());
        }
        
        // Verificar se a criptomoeda existe
        if (!cryptoService.existsById(transaction.getCrypto().getId())) {
            throw new RuntimeException("Criptomoeda não encontrada com id: " + transaction.getCrypto().getId());
        }
        
        // Buscar as entidades completas
        Portfolio portfolio = portfolioService.findById(transaction.getPortfolio().getId())
            .orElseThrow(() -> new RuntimeException("Portfólio não encontrado"));
        
        Crypto crypto = cryptoService.findById(transaction.getCrypto().getId())
            .orElseThrow(() -> new RuntimeException("Criptomoeda não encontrada"));
        
        transaction.setPortfolio(portfolio);
        transaction.setCrypto(crypto);
        
        // Calcular valor total se não foi fornecido
        if (transaction.getTotalValue() == null) {
            transaction.setTotalValue(transaction.getQuantity().multiply(transaction.getPricePerUnit()));
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Atualizar valor total do portfólio
        portfolioService.updateTotalValue(portfolio.getId());
        
        return savedTransaction;
    }
    
    public Transaction update(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transação não encontrada com id: " + id));
        
        // Verificar se o portfólio existe (se mudou)
        if (!transaction.getPortfolio().getId().equals(transactionDetails.getPortfolio().getId()) &&
            !portfolioService.existsById(transactionDetails.getPortfolio().getId())) {
            throw new RuntimeException("Portfólio não encontrado com id: " + transactionDetails.getPortfolio().getId());
        }
        
        // Verificar se a criptomoeda existe (se mudou)
        if (!transaction.getCrypto().getId().equals(transactionDetails.getCrypto().getId()) &&
            !cryptoService.existsById(transactionDetails.getCrypto().getId())) {
            throw new RuntimeException("Criptomoeda não encontrada com id: " + transactionDetails.getCrypto().getId());
        }
        
        // Buscar as entidades completas se mudaram
        if (!transaction.getPortfolio().getId().equals(transactionDetails.getPortfolio().getId())) {
            Portfolio portfolio = portfolioService.findById(transactionDetails.getPortfolio().getId())
                .orElseThrow(() -> new RuntimeException("Portfólio não encontrado"));
            transaction.setPortfolio(portfolio);
        }
        
        if (!transaction.getCrypto().getId().equals(transactionDetails.getCrypto().getId())) {
            Crypto crypto = cryptoService.findById(transactionDetails.getCrypto().getId())
                .orElseThrow(() -> new RuntimeException("Criptomoeda não encontrada"));
            transaction.setCrypto(crypto);
        }
        
        transaction.setType(transactionDetails.getType());
        transaction.setQuantity(transactionDetails.getQuantity());
        transaction.setPricePerUnit(transactionDetails.getPricePerUnit());
        transaction.setNotes(transactionDetails.getNotes());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        
        // Recalcular valor total
        transaction.setTotalValue(transaction.getQuantity().multiply(transaction.getPricePerUnit()));
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        // Atualizar valor total do portfólio
        portfolioService.updateTotalValue(transaction.getPortfolio().getId());
        
        return updatedTransaction;
    }
    
    public void deleteById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transação não encontrada com id: " + id));
        
        Long portfolioId = transaction.getPortfolio().getId();
        
        transactionRepository.deleteById(id);
        
        // Atualizar valor total do portfólio
        portfolioService.updateTotalValue(portfolioId);
    }
    
    public BigDecimal getTotalBoughtQuantity(Long cryptoId) {
        BigDecimal total = transactionRepository.getTotalBoughtQuantity(cryptoId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalSoldQuantity(Long cryptoId) {
        BigDecimal total = transactionRepository.getTotalSoldQuantity(cryptoId);
        return total != null ? total : BigDecimal.ZERO;
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
        return transactionRepository.existsById(id);
    }
    
    public long count() {
        return transactionRepository.count();
    }
}

