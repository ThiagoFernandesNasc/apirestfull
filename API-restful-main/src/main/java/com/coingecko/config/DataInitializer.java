package com.coingecko.config;

import com.coingecko.model.Crypto;
import com.coingecko.model.Portfolio;
import com.coingecko.model.Transaction;
import com.coingecko.repository.CryptoRepository;
import com.coingecko.repository.PortfolioRepository;
import com.coingecko.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CryptoRepository cryptoRepository;
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Criar criptomoedas de exemplo
        if (cryptoRepository.count() == 0) {
            createSampleCryptos();
        }
        
        // Criar portfólios de exemplo
        if (portfolioRepository.count() == 0) {
            createSamplePortfolios();
        }
        
        // Criar transações de exemplo
        if (transactionRepository.count() == 0) {
            createSampleTransactions();
        }
    }
    
    private void createSampleCryptos() {
        List<Crypto> cryptos = List.of(
            createCrypto("Bitcoin", "BTC", new BigDecimal("45000.00"), 
                new BigDecimal("850000000000"), new BigDecimal("25000000000"), 
                new BigDecimal("2.5"), "A primeira e mais valiosa criptomoeda do mundo"),
            createCrypto("Ethereum", "ETH", new BigDecimal("3200.00"), 
                new BigDecimal("385000000000"), new BigDecimal("15000000000"), 
                new BigDecimal("1.8"), "Plataforma de contratos inteligentes líder"),
            createCrypto("Binance Coin", "BNB", new BigDecimal("320.00"), 
                new BigDecimal("50000000000"), new BigDecimal("2000000000"), 
                new BigDecimal("-0.5"), "Token nativo da exchange Binance"),
            createCrypto("Cardano", "ADA", new BigDecimal("0.45"), 
                new BigDecimal("15000000000"), new BigDecimal("800000000"), 
                new BigDecimal("3.2"), "Plataforma blockchain de terceira geração"),
            createCrypto("Solana", "SOL", new BigDecimal("95.00"), 
                new BigDecimal("40000000000"), new BigDecimal("1200000000"), 
                new BigDecimal("5.1"), "Blockchain de alta performance para DeFi")
        );
        
        cryptoRepository.saveAll(cryptos);
    }
    
    private Crypto createCrypto(String name, String symbol, BigDecimal price, 
                               BigDecimal marketCap, BigDecimal volume24h, 
                               BigDecimal change24h, String description) {
        Crypto crypto = new Crypto(name, symbol, price);
        crypto.setMarketCap(marketCap);
        crypto.setVolume24h(volume24h);
        crypto.setChange24h(change24h);
        crypto.setDescription(description);
        return crypto;
    }
    
    private void createSamplePortfolios() {
        List<Portfolio> portfolios = List.of(
            createPortfolio("Portfólio Conservador", 
                "Foco em criptomoedas estáveis e de grande capitalização", 
                new BigDecimal("10000.00")),
            createPortfolio("Portfólio Agressivo", 
                "Foco em criptomoedas de alto potencial e risco", 
                new BigDecimal("5000.00")),
            createPortfolio("Portfólio DeFi", 
                "Foco em protocolos de finanças descentralizadas", 
                new BigDecimal("7500.00"))
        );
        
        portfolioRepository.saveAll(portfolios);
    }
    
    private Portfolio createPortfolio(String name, String description, BigDecimal totalValue) {
        Portfolio portfolio = new Portfolio(name, description);
        portfolio.setTotalValue(totalValue);
        return portfolio;
    }
    
    private void createSampleTransactions() {
        // Buscar entidades criadas em batch
        List<String> symbols = List.of("BTC", "ETH", "ADA");
        List<String> portfolioNames = List.of("Portfólio Conservador", "Portfólio Agressivo");
        
        List<Crypto> cryptos = cryptoRepository.findBySymbolIn(symbols);
        List<Portfolio> portfolios = portfolioRepository.findByNameIn(portfolioNames);
        
        if (cryptos.size() >= 3 && portfolios.size() >= 2) {
            Crypto bitcoin = cryptos.stream().filter(c -> "BTC".equals(c.getSymbol())).findFirst().orElse(null);
            Crypto ethereum = cryptos.stream().filter(c -> "ETH".equals(c.getSymbol())).findFirst().orElse(null);
            Crypto cardano = cryptos.stream().filter(c -> "ADA".equals(c.getSymbol())).findFirst().orElse(null);
            
            Portfolio portfolio1 = portfolios.stream().filter(p -> "Portfólio Conservador".equals(p.getName())).findFirst().orElse(null);
            Portfolio portfolio2 = portfolios.stream().filter(p -> "Portfólio Agressivo".equals(p.getName())).findFirst().orElse(null);
            
            List<Transaction> transactions = new ArrayList<>();
            
            if (bitcoin != null && portfolio1 != null) {
                transactions.add(new Transaction(portfolio1, bitcoin, Transaction.TransactionType.BUY,
                        new BigDecimal("0.1"), new BigDecimal("44000.00"), "Primeira compra de Bitcoin"));
            }
            
            if (ethereum != null && portfolio1 != null) {
                transactions.add(new Transaction(portfolio1, ethereum, Transaction.TransactionType.BUY,
                        new BigDecimal("2.0"), new BigDecimal("3100.00"), "Compra de Ethereum para diversificação"));
            }
            
            if (cardano != null && portfolio2 != null) {
                transactions.add(new Transaction(portfolio2, cardano, Transaction.TransactionType.BUY,
                        new BigDecimal("1000.0"), new BigDecimal("0.40"), "Aposta em Cardano para longo prazo"));
            }
            
            if (bitcoin != null && portfolio2 != null) {
                transactions.add(new Transaction(portfolio2, bitcoin, Transaction.TransactionType.SELL,
                        new BigDecimal("0.05"), new BigDecimal("46000.00"), "Realização de lucros"));
            }
            
            if (!transactions.isEmpty()) {
                transactionRepository.saveAll(transactions);
            }
        }
    }
}

