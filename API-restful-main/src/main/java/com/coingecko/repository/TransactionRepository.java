package com.coingecko.repository;

import com.coingecko.model.Transaction;
import com.coingecko.model.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByPortfolioId(Long portfolioId);
    
    List<Transaction> findByCryptoId(Long cryptoId);
    
    List<Transaction> findByType(TransactionType type);
    
    List<Transaction> findByPortfolioIdAndType(Long portfolioId, TransactionType type);
    
    List<Transaction> findByCryptoIdAndType(Long cryptoId, TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.id = :portfolioId ORDER BY t.transactionDate DESC")
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(@Param("portfolioId") Long portfolioId);
    
    @Query("SELECT t FROM Transaction t WHERE t.crypto.id = :cryptoId ORDER BY t.transactionDate DESC")
    List<Transaction> findByCryptoIdOrderByTransactionDateDesc(@Param("cryptoId") Long cryptoId);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    List<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.id = :portfolioId AND " +
           "t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    List<Transaction> findByPortfolioIdAndTransactionDateBetween(@Param("portfolioId") Long portfolioId,
                                                               @Param("startDate") LocalDateTime startDate,
                                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.crypto.id = :cryptoId AND t.type = 'BUY'")
    java.math.BigDecimal getTotalBoughtQuantity(@Param("cryptoId") Long cryptoId);
    
    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.crypto.id = :cryptoId AND t.type = 'SELL'")
    java.math.BigDecimal getTotalSoldQuantity(@Param("cryptoId") Long cryptoId);
    
    @Query("SELECT SUM(t.totalValue) FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.type = 'BUY'")
    java.math.BigDecimal getTotalInvested(@Param("portfolioId") Long portfolioId);
    
    @Query("SELECT SUM(t.totalValue) FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.type = 'SELL'")
    java.math.BigDecimal getTotalSold(@Param("portfolioId") Long portfolioId);
}

