package com.coingecko.service;

import com.coingecko.model.Crypto;
import com.coingecko.repository.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CryptoService {
    
    @Autowired
    private CryptoRepository cryptoRepository;
    
    public List<Crypto> findAll() {
        return cryptoRepository.findAll();
    }
    
    public Optional<Crypto> findById(Long id) {
        return cryptoRepository.findById(id);
    }
    
    public Optional<Crypto> findBySymbol(String symbol) {
        return cryptoRepository.findBySymbol(symbol);
    }
    
    public Optional<Crypto> findByName(String name) {
        return cryptoRepository.findByName(name);
    }
    
    public List<Crypto> searchByName(String name) {
        return cryptoRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Crypto> searchBySymbol(String symbol) {
        return cryptoRepository.findBySymbolContainingIgnoreCase(symbol);
    }
    
    public List<Crypto> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return cryptoRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public List<Crypto> findByChangeRange(BigDecimal minChange, BigDecimal maxChange) {
        return cryptoRepository.findByChangeRange(minChange, maxChange);
    }
    
    public List<Crypto> findAllOrderByMarketCap() {
        return cryptoRepository.findAllOrderByMarketCapDesc();
    }
    
    public List<Crypto> findAllOrderByVolume24h() {
        return cryptoRepository.findAllOrderByVolume24hDesc();
    }
    
    public List<Crypto> findAllOrderByChange24h() {
        return cryptoRepository.findAllOrderByChange24hDesc();
    }
    
    public Crypto save(Crypto crypto) {
        // Verificar se já existe uma crypto com o mesmo símbolo
        if (cryptoRepository.existsBySymbol(crypto.getSymbol())) {
            throw new RuntimeException("Já existe uma criptomoeda com o símbolo: " + crypto.getSymbol());
        }
        
        // Verificar se já existe uma crypto com o mesmo nome
        if (cryptoRepository.existsByName(crypto.getName())) {
            throw new RuntimeException("Já existe uma criptomoeda com o nome: " + crypto.getName());
        }
        
        return cryptoRepository.save(crypto);
    }
    
    public Crypto update(Long id, Crypto cryptoDetails) {
        Crypto crypto = cryptoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Criptomoeda não encontrada com id: " + id));
        
        // Verificar se o novo símbolo já existe (se mudou)
        if (!crypto.getSymbol().equals(cryptoDetails.getSymbol()) && 
            cryptoRepository.existsBySymbol(cryptoDetails.getSymbol())) {
            throw new RuntimeException("Já existe uma criptomoeda com o símbolo: " + cryptoDetails.getSymbol());
        }
        
        // Verificar se o novo nome já existe (se mudou)
        if (!crypto.getName().equals(cryptoDetails.getName()) && 
            cryptoRepository.existsByName(cryptoDetails.getName())) {
            throw new RuntimeException("Já existe uma criptomoeda com o nome: " + cryptoDetails.getName());
        }
        
        crypto.setName(cryptoDetails.getName());
        crypto.setSymbol(cryptoDetails.getSymbol());
        crypto.setCurrentPrice(cryptoDetails.getCurrentPrice());
        crypto.setMarketCap(cryptoDetails.getMarketCap());
        crypto.setVolume24h(cryptoDetails.getVolume24h());
        crypto.setChange24h(cryptoDetails.getChange24h());
        crypto.setDescription(cryptoDetails.getDescription());
        
        return cryptoRepository.save(crypto);
    }
    
    public void deleteById(Long id) {
        if (!cryptoRepository.existsById(id)) {
            throw new RuntimeException("Criptomoeda não encontrada com id: " + id);
        }
        cryptoRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return cryptoRepository.existsById(id);
    }
    
    public long count() {
        return cryptoRepository.count();
    }
}

