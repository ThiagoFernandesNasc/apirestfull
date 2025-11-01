package com.coingecko.repository;

import com.coingecko.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    Optional<Portfolio> findByName(String name);
    
    List<Portfolio> findByNameContainingIgnoreCase(String name);
    
    List<Portfolio> findByNameIn(List<String> names);
    
    @Query("SELECT p FROM Portfolio p WHERE p.totalValue >= :minValue AND p.totalValue <= :maxValue")
    List<Portfolio> findByValueRange(@Param("minValue") java.math.BigDecimal minValue, 
                                    @Param("maxValue") java.math.BigDecimal maxValue);
    
    @Query("SELECT p FROM Portfolio p ORDER BY p.totalValue DESC")
    List<Portfolio> findAllOrderByTotalValueDesc();
    
    @Query("SELECT p FROM Portfolio p ORDER BY p.createdAt DESC")
    List<Portfolio> findAllOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
}

