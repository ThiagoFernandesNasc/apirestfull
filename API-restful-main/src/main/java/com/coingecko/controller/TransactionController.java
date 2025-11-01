package com.coingecko.controller;

import com.coingecko.model.Transaction;
import com.coingecko.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "API para gerenciamento de transações")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @GetMapping
    @Operation(summary = "Listar todas as transações", 
               description = "Retorna uma lista com todas as transações cadastradas")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID", 
               description = "Retorna uma transação específica pelo seu ID")
    public ResponseEntity<Transaction> getTransactionById(
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.findById(id);
        return transaction.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/portfolio/{portfolioId}")
    @Operation(summary = "Buscar transações por portfólio", 
               description = "Retorna todas as transações de um portfólio específico")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolio(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId) {
        List<Transaction> transactions = transactionService.findByPortfolioId(portfolioId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/crypto/{cryptoId}")
    @Operation(summary = "Buscar transações por criptomoeda", 
               description = "Retorna todas as transações de uma criptomoeda específica")
    public ResponseEntity<List<Transaction>> getTransactionsByCrypto(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long cryptoId) {
        List<Transaction> transactions = transactionService.findByCryptoId(cryptoId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Buscar transações por tipo", 
               description = "Retorna todas as transações de um tipo específico (BUY/SELL)")
    public ResponseEntity<List<Transaction>> getTransactionsByType(
            @Parameter(description = "Tipo da transação") @PathVariable Transaction.TransactionType type) {
        List<Transaction> transactions = transactionService.findByType(type);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/portfolio/{portfolioId}/type/{type}")
    @Operation(summary = "Buscar transações por portfólio e tipo", 
               description = "Retorna transações de um portfólio específico e tipo")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolioAndType(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId,
            @Parameter(description = "Tipo da transação") @PathVariable Transaction.TransactionType type) {
        List<Transaction> transactions = transactionService.findByPortfolioIdAndType(portfolioId, type);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/crypto/{cryptoId}/type/{type}")
    @Operation(summary = "Buscar transações por criptomoeda e tipo", 
               description = "Retorna transações de uma criptomoeda específica e tipo")
    public ResponseEntity<List<Transaction>> getTransactionsByCryptoAndType(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long cryptoId,
            @Parameter(description = "Tipo da transação") @PathVariable Transaction.TransactionType type) {
        List<Transaction> transactions = transactionService.findByCryptoIdAndType(cryptoId, type);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/portfolio/{portfolioId}/recent")
    @Operation(summary = "Buscar transações recentes por portfólio", 
               description = "Retorna transações de um portfólio ordenadas por data")
    public ResponseEntity<List<Transaction>> getRecentTransactionsByPortfolio(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId) {
        List<Transaction> transactions = transactionService.findByPortfolioIdOrderByDate(portfolioId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/crypto/{cryptoId}/recent")
    @Operation(summary = "Buscar transações recentes por criptomoeda", 
               description = "Retorna transações de uma criptomoeda ordenadas por data")
    public ResponseEntity<List<Transaction>> getRecentTransactionsByCrypto(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long cryptoId) {
        List<Transaction> transactions = transactionService.findByCryptoIdOrderByDate(cryptoId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Buscar transações por período", 
               description = "Retorna transações dentro de um período específico")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @Parameter(description = "Data inicial") @RequestParam LocalDateTime startDate,
            @Parameter(description = "Data final") @RequestParam LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/portfolio/{portfolioId}/date-range")
    @Operation(summary = "Buscar transações por portfólio e período", 
               description = "Retorna transações de um portfólio dentro de um período específico")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolioAndDateRange(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId,
            @Parameter(description = "Data inicial") @RequestParam LocalDateTime startDate,
            @Parameter(description = "Data final") @RequestParam LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.findByPortfolioIdAndDateRange(portfolioId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @PostMapping
    @Operation(summary = "Criar nova transação", 
               description = "Cria uma nova transação no sistema")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.save(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", 
               description = "Atualiza uma transação existente")
    public ResponseEntity<Transaction> updateTransaction(
            @Parameter(description = "ID da transação") @PathVariable Long id,
            @Valid @RequestBody Transaction transactionDetails) {
        try {
            Transaction updatedTransaction = transactionService.update(id, transactionDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar transação", 
               description = "Remove uma transação do sistema")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        try {
            transactionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/crypto/{cryptoId}/total-bought")
    @Operation(summary = "Obter quantidade total comprada", 
               description = "Retorna a quantidade total comprada de uma criptomoeda")
    public ResponseEntity<BigDecimal> getTotalBoughtQuantity(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long cryptoId) {
        try {
            BigDecimal totalBought = transactionService.getTotalBoughtQuantity(cryptoId);
            return ResponseEntity.ok(totalBought);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/crypto/{cryptoId}/total-sold")
    @Operation(summary = "Obter quantidade total vendida", 
               description = "Retorna a quantidade total vendida de uma criptomoeda")
    public ResponseEntity<BigDecimal> getTotalSoldQuantity(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long cryptoId) {
        try {
            BigDecimal totalSold = transactionService.getTotalSoldQuantity(cryptoId);
            return ResponseEntity.ok(totalSold);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/portfolio/{portfolioId}/total-invested")
    @Operation(summary = "Obter total investido no portfólio", 
               description = "Retorna o valor total investido em um portfólio")
    public ResponseEntity<BigDecimal> getTotalInvested(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId) {
        try {
            BigDecimal totalInvested = transactionService.getTotalInvested(portfolioId);
            return ResponseEntity.ok(totalInvested);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/portfolio/{portfolioId}/total-sold")
    @Operation(summary = "Obter total vendido do portfólio", 
               description = "Retorna o valor total vendido de um portfólio")
    public ResponseEntity<BigDecimal> getTotalSold(
            @Parameter(description = "ID do portfólio") @PathVariable Long portfolioId) {
        try {
            BigDecimal totalSold = transactionService.getTotalSold(portfolioId);
            return ResponseEntity.ok(totalSold);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Contar transações", 
               description = "Retorna o número total de transações cadastradas")
    public ResponseEntity<Long> getTransactionCount() {
        long count = transactionService.count();
        return ResponseEntity.ok(count);
    }
}

