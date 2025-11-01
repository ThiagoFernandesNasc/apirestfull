package com.coingecko.controller;

import com.coingecko.model.Portfolio;
import com.coingecko.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolio", description = "API para gerenciamento de portfólios")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @GetMapping
    @Operation(summary = "Listar todos os portfólios", 
               description = "Retorna uma lista com todos os portfólios cadastrados")
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        List<Portfolio> portfolios = portfolioService.findAll();
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar portfólio por ID", 
               description = "Retorna um portfólio específico pelo seu ID")
    public ResponseEntity<Portfolio> getPortfolioById(
            @Parameter(description = "ID do portfólio") @PathVariable Long id) {
        Optional<Portfolio> portfolio = portfolioService.findById(id);
        return portfolio.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    @Operation(summary = "Buscar portfólio por nome", 
               description = "Retorna um portfólio específico pelo seu nome")
    public ResponseEntity<Portfolio> getPortfolioByName(
            @Parameter(description = "Nome do portfólio") @PathVariable String name) {
        Optional<Portfolio> portfolio = portfolioService.findByName(name);
        return portfolio.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/name")
    @Operation(summary = "Buscar portfólios por nome (busca parcial)", 
               description = "Retorna portfólios que contenham o termo no nome")
    public ResponseEntity<List<Portfolio>> searchPortfoliosByName(
            @Parameter(description = "Termo de busca") @RequestParam String name) {
        List<Portfolio> portfolios = portfolioService.searchByName(name);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/value-range")
    @Operation(summary = "Buscar portfólios por faixa de valor", 
               description = "Retorna portfólios dentro de uma faixa de valor total")
    public ResponseEntity<List<Portfolio>> getPortfoliosByValueRange(
            @Parameter(description = "Valor mínimo") @RequestParam BigDecimal minValue,
            @Parameter(description = "Valor máximo") @RequestParam BigDecimal maxValue) {
        List<Portfolio> portfolios = portfolioService.findByValueRange(minValue, maxValue);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/top/value")
    @Operation(summary = "Listar portfólios por valor total", 
               description = "Retorna portfólios ordenados por valor total")
    public ResponseEntity<List<Portfolio>> getPortfoliosByValue() {
        List<Portfolio> portfolios = portfolioService.findAllOrderByTotalValue();
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Listar portfólios por data de criação", 
               description = "Retorna portfólios ordenados por data de criação")
    public ResponseEntity<List<Portfolio>> getPortfoliosByCreatedAt() {
        List<Portfolio> portfolios = portfolioService.findAllOrderByCreatedAt();
        return ResponseEntity.ok(portfolios);
    }
    
    @PostMapping
    @Operation(summary = "Criar novo portfólio", 
               description = "Cria um novo portfólio no sistema")
    public ResponseEntity<Portfolio> createPortfolio(@Valid @RequestBody Portfolio portfolio) {
        try {
            Portfolio createdPortfolio = portfolioService.save(portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar portfólio", 
               description = "Atualiza um portfólio existente")
    public ResponseEntity<Portfolio> updatePortfolio(
            @Parameter(description = "ID do portfólio") @PathVariable Long id,
            @Valid @RequestBody Portfolio portfolioDetails) {
        try {
            Portfolio updatedPortfolio = portfolioService.update(id, portfolioDetails);
            return ResponseEntity.ok(updatedPortfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar portfólio", 
               description = "Remove um portfólio do sistema")
    public ResponseEntity<Void> deletePortfolio(
            @Parameter(description = "ID do portfólio") @PathVariable Long id) {
        try {
            portfolioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/update-value")
    @Operation(summary = "Atualizar valor total do portfólio", 
               description = "Recalcula e atualiza o valor total do portfólio baseado nas transações")
    public ResponseEntity<Void> updatePortfolioValue(
            @Parameter(description = "ID do portfólio") @PathVariable Long id) {
        try {
            portfolioService.updateTotalValue(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/total-invested")
    @Operation(summary = "Obter total investido no portfólio", 
               description = "Retorna o valor total investido no portfólio")
    public ResponseEntity<BigDecimal> getTotalInvested(
            @Parameter(description = "ID do portfólio") @PathVariable Long id) {
        try {
            BigDecimal totalInvested = portfolioService.getTotalInvested(id);
            return ResponseEntity.ok(totalInvested);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/total-sold")
    @Operation(summary = "Obter total vendido do portfólio", 
               description = "Retorna o valor total vendido do portfólio")
    public ResponseEntity<BigDecimal> getTotalSold(
            @Parameter(description = "ID do portfólio") @PathVariable Long id) {
        try {
            BigDecimal totalSold = portfolioService.getTotalSold(id);
            return ResponseEntity.ok(totalSold);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Contar portfólios", 
               description = "Retorna o número total de portfólios cadastrados")
    public ResponseEntity<Long> getPortfolioCount() {
        long count = portfolioService.count();
        return ResponseEntity.ok(count);
    }
}

