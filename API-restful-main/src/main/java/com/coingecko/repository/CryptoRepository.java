package com.coingecko.repository;

import com.coingecko.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {
    
    Optional<Crypto> findBySymbol(String symbol);
    
    Optional<Crypto> findByName(String name);
    
    List<Crypto> findByNameContainingIgnoreCase(String name);
    
    List<Crypto> findBySymbolContainingIgnoreCase(String symbol);
    
    List<Crypto> findBySymbolIn(List<String> symbols);
    
    @Query("SELECT c FROM Crypto c WHERE c.currentPrice >= :minPrice AND c.currentPrice <= :maxPrice")
    List<Crypto> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT c FROM Crypto c WHERE c.change24h >= :minChange AND c.change24h <= :maxChange")
    List<Crypto> findByChangeRange(@Param("minChange") BigDecimal minChange, @Param("maxChange") BigDecimal maxChange);
    
    @Query("SELECT c FROM Crypto c ORDER BY c.marketCap DESC")
    List<Crypto> findAllOrderByMarketCapDesc();
    
    @Query("SELECT c FROM Crypto c ORDER BY c.volume24h DESC")
    List<Crypto> findAllOrderByVolume24hDesc();
    
    @Query("SELECT c FROM Crypto c ORDER BY c.change24h DESC")
    List<Crypto> findAllOrderByChange24hDesc();
    
    boolean existsBySymbol(String symbol);
    
    boolean existsByName(String name);
}

