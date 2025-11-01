package com.coingecko.controller;

import com.coingecko.model.Crypto;
import com.coingecko.service.CryptoService;
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
@RequestMapping("/api/cryptos")
@Tag(name = "Crypto", description = "API para gerenciamento de criptomoedas")
public class CryptoController {
    
    @Autowired
    private CryptoService cryptoService;
    
    @GetMapping
    @Operation(summary = "Listar todas as criptomoedas", 
               description = "Retorna uma lista com todas as criptomoedas cadastradas")
    public ResponseEntity<List<Crypto>> getAllCryptos() {
        List<Crypto> cryptos = cryptoService.findAll();
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar criptomoeda por ID", 
               description = "Retorna uma criptomoeda específica pelo seu ID")
    public ResponseEntity<Crypto> getCryptoById(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long id) {
        Optional<Crypto> crypto = cryptoService.findById(id);
        return crypto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/symbol/{symbol}")
    @Operation(summary = "Buscar criptomoeda por símbolo", 
               description = "Retorna uma criptomoeda específica pelo seu símbolo")
    public ResponseEntity<Crypto> getCryptoBySymbol(
            @Parameter(description = "Símbolo da criptomoeda") @PathVariable String symbol) {
        Optional<Crypto> crypto = cryptoService.findBySymbol(symbol);
        return crypto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    @Operation(summary = "Buscar criptomoeda por nome", 
               description = "Retorna uma criptomoeda específica pelo seu nome")
    public ResponseEntity<Crypto> getCryptoByName(
            @Parameter(description = "Nome da criptomoeda") @PathVariable String name) {
        Optional<Crypto> crypto = cryptoService.findByName(name);
        return crypto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/name")
    @Operation(summary = "Buscar criptomoedas por nome (busca parcial)", 
               description = "Retorna criptomoedas que contenham o termo no nome")
    public ResponseEntity<List<Crypto>> searchCryptosByName(
            @Parameter(description = "Termo de busca") @RequestParam String name) {
        List<Crypto> cryptos = cryptoService.searchByName(name);
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/search/symbol")
    @Operation(summary = "Buscar criptomoedas por símbolo (busca parcial)", 
               description = "Retorna criptomoedas que contenham o termo no símbolo")
    public ResponseEntity<List<Crypto>> searchCryptosBySymbol(
            @Parameter(description = "Termo de busca") @RequestParam String symbol) {
        List<Crypto> cryptos = cryptoService.searchBySymbol(symbol);
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/price-range")
    @Operation(summary = "Buscar criptomoedas por faixa de preço", 
               description = "Retorna criptomoedas dentro de uma faixa de preço")
    public ResponseEntity<List<Crypto>> getCryptosByPriceRange(
            @Parameter(description = "Preço mínimo") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Preço máximo") @RequestParam BigDecimal maxPrice) {
        List<Crypto> cryptos = cryptoService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/change-range")
    @Operation(summary = "Buscar criptomoedas por faixa de variação", 
               description = "Retorna criptomoedas dentro de uma faixa de variação 24h")
    public ResponseEntity<List<Crypto>> getCryptosByChangeRange(
            @Parameter(description = "Variação mínima (%)") @RequestParam BigDecimal minChange,
            @Parameter(description = "Variação máxima (%)") @RequestParam BigDecimal maxChange) {
        List<Crypto> cryptos = cryptoService.findByChangeRange(minChange, maxChange);
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/top/market-cap")
    @Operation(summary = "Listar criptomoedas por capitalização de mercado", 
               description = "Retorna criptomoedas ordenadas por capitalização de mercado")
    public ResponseEntity<List<Crypto>> getCryptosByMarketCap() {
        List<Crypto> cryptos = cryptoService.findAllOrderByMarketCap();
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/top/volume")
    @Operation(summary = "Listar criptomoedas por volume 24h", 
               description = "Retorna criptomoedas ordenadas por volume 24h")
    public ResponseEntity<List<Crypto>> getCryptosByVolume() {
        List<Crypto> cryptos = cryptoService.findAllOrderByVolume24h();
        return ResponseEntity.ok(cryptos);
    }
    
    @GetMapping("/top/change")
    @Operation(summary = "Listar criptomoedas por variação 24h", 
               description = "Retorna criptomoedas ordenadas por variação 24h")
    public ResponseEntity<List<Crypto>> getCryptosByChange() {
        List<Crypto> cryptos = cryptoService.findAllOrderByChange24h();
        return ResponseEntity.ok(cryptos);
    }
    
    @PostMapping
    @Operation(summary = "Criar nova criptomoeda", 
               description = "Cria uma nova criptomoeda no sistema")
    public ResponseEntity<Crypto> createCrypto(@Valid @RequestBody Crypto crypto) {
        try {
            Crypto createdCrypto = cryptoService.save(crypto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCrypto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar criptomoeda", 
               description = "Atualiza uma criptomoeda existente")
    public ResponseEntity<Crypto> updateCrypto(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long id,
            @Valid @RequestBody Crypto cryptoDetails) {
        try {
            Crypto updatedCrypto = cryptoService.update(id, cryptoDetails);
            return ResponseEntity.ok(updatedCrypto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar criptomoeda", 
               description = "Remove uma criptomoeda do sistema")
    public ResponseEntity<Void> deleteCrypto(
            @Parameter(description = "ID da criptomoeda") @PathVariable Long id) {
        try {
            cryptoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Contar criptomoedas", 
               description = "Retorna o número total de criptomoedas cadastradas")
    public ResponseEntity<Long> getCryptoCount() {
        long count = cryptoService.count();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Endpoint para verificar se a API está funcionando")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API funcionando corretamente!");
    }
}

