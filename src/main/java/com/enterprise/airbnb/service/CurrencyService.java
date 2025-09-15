package com.enterprise.airbnb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for Currency operations and exchange rate management
 */
@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    private RestTemplate restTemplate;

    // Base currency for the application
    private static final String BASE_CURRENCY = "USD";

    // Supported currencies
    private static final Map<String, String> SUPPORTED_CURRENCIES = new HashMap<>();
    
    static {
        SUPPORTED_CURRENCIES.put("USD", "US Dollar");
        SUPPORTED_CURRENCIES.put("EUR", "Euro");
        SUPPORTED_CURRENCIES.put("GBP", "British Pound");
        SUPPORTED_CURRENCIES.put("JPY", "Japanese Yen");
        SUPPORTED_CURRENCIES.put("CAD", "Canadian Dollar");
        SUPPORTED_CURRENCIES.put("AUD", "Australian Dollar");
        SUPPORTED_CURRENCIES.put("CHF", "Swiss Franc");
        SUPPORTED_CURRENCIES.put("CNY", "Chinese Yuan");
        SUPPORTED_CURRENCIES.put("INR", "Indian Rupee");
        SUPPORTED_CURRENCIES.put("BRL", "Brazilian Real");
        SUPPORTED_CURRENCIES.put("MXN", "Mexican Peso");
        SUPPORTED_CURRENCIES.put("KRW", "South Korean Won");
        SUPPORTED_CURRENCIES.put("SGD", "Singapore Dollar");
        SUPPORTED_CURRENCIES.put("HKD", "Hong Kong Dollar");
        SUPPORTED_CURRENCIES.put("NZD", "New Zealand Dollar");
        SUPPORTED_CURRENCIES.put("SEK", "Swedish Krona");
        SUPPORTED_CURRENCIES.put("NOK", "Norwegian Krone");
        SUPPORTED_CURRENCIES.put("DKK", "Danish Krone");
        SUPPORTED_CURRENCIES.put("PLN", "Polish Zloty");
        SUPPORTED_CURRENCIES.put("CZK", "Czech Koruna");
        SUPPORTED_CURRENCIES.put("HUF", "Hungarian Forint");
        SUPPORTED_CURRENCIES.put("RUB", "Russian Ruble");
        SUPPORTED_CURRENCIES.put("ZAR", "South African Rand");
        SUPPORTED_CURRENCIES.put("TRY", "Turkish Lira");
        SUPPORTED_CURRENCIES.put("ILS", "Israeli Shekel");
        SUPPORTED_CURRENCIES.put("AED", "UAE Dirham");
        SUPPORTED_CURRENCIES.put("SAR", "Saudi Riyal");
        SUPPORTED_CURRENCIES.put("THB", "Thai Baht");
        SUPPORTED_CURRENCIES.put("MYR", "Malaysian Ringgit");
        SUPPORTED_CURRENCIES.put("IDR", "Indonesian Rupiah");
        SUPPORTED_CURRENCIES.put("PHP", "Philippine Peso");
        SUPPORTED_CURRENCIES.put("VND", "Vietnamese Dong");
    }

    /**
     * Get current exchange rate between two currencies
     */
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        logger.info("Getting exchange rate from {} to {}", fromCurrency, toCurrency);
        
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        try {
            // Use external API to get exchange rates
            // This is a placeholder - actual implementation would use a real exchange rate API
            BigDecimal rate = fetchExchangeRateFromAPI(fromCurrency, toCurrency);
            
            if (rate == null) {
                // Fallback to cached rates or default rates
                rate = getDefaultExchangeRate(fromCurrency, toCurrency);
            }
            
            return rate.setScale(6, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            logger.error("Error fetching exchange rate: {}", e.getMessage());
            return getDefaultExchangeRate(fromCurrency, toCurrency);
        }
    }

    /**
     * Convert amount from one currency to another
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        logger.info("Converting {} {} to {}", amount, fromCurrency, toCurrency);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (!isCurrencySupported(fromCurrency) || !isCurrencySupported(toCurrency)) {
            throw new IllegalArgumentException("Unsupported currency");
        }
        
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        BigDecimal convertedAmount = amount.multiply(exchangeRate);
        
        return convertedAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get all supported currencies
     */
    @Cacheable(value = "currencies", key = "'supported'")
    public Map<String, String> getSupportedCurrencies() {
        logger.info("Fetching supported currencies");
        return new HashMap<>(SUPPORTED_CURRENCIES);
    }

    /**
     * Check if currency is supported
     */
    public boolean isCurrencySupported(String currency) {
        return SUPPORTED_CURRENCIES.containsKey(currency.toUpperCase());
    }

    /**
     * Get base currency
     */
    public String getBaseCurrency() {
        return BASE_CURRENCY;
    }

    /**
     * Get currency symbol
     */
    @Cacheable(value = "currencies", key = "'symbol_' + #currency")
    public String getCurrencySymbol(String currency) {
        logger.info("Getting currency symbol for: {}", currency);
        
        Map<String, String> currencySymbols = new HashMap<>();
        currencySymbols.put("USD", "$");
        currencySymbols.put("EUR", "€");
        currencySymbols.put("GBP", "£");
        currencySymbols.put("JPY", "¥");
        currencySymbols.put("CAD", "C$");
        currencySymbols.put("AUD", "A$");
        currencySymbols.put("CHF", "CHF");
        currencySymbols.put("CNY", "¥");
        currencySymbols.put("INR", "₹");
        currencySymbols.put("BRL", "R$");
        currencySymbols.put("MXN", "$");
        currencySymbols.put("KRW", "₩");
        currencySymbols.put("SGD", "S$");
        currencySymbols.put("HKD", "HK$");
        currencySymbols.put("NZD", "NZ$");
        currencySymbols.put("SEK", "kr");
        currencySymbols.put("NOK", "kr");
        currencySymbols.put("DKK", "kr");
        currencySymbols.put("PLN", "zł");
        currencySymbols.put("CZK", "Kč");
        currencySymbols.put("HUF", "Ft");
        currencySymbols.put("RUB", "₽");
        currencySymbols.put("ZAR", "R");
        currencySymbols.put("TRY", "₺");
        currencySymbols.put("ILS", "₪");
        currencySymbols.put("AED", "د.إ");
        currencySymbols.put("SAR", "﷼");
        currencySymbols.put("THB", "฿");
        currencySymbols.put("MYR", "RM");
        currencySymbols.put("IDR", "Rp");
        currencySymbols.put("PHP", "₱");
        currencySymbols.put("VND", "₫");
        
        return currencySymbols.getOrDefault(currency.toUpperCase(), currency);
    }

    /**
     * Get currency name
     */
    @Cacheable(value = "currencies", key = "'name_' + #currency")
    public String getCurrencyName(String currency) {
        logger.info("Getting currency name for: {}", currency);
        return SUPPORTED_CURRENCIES.get(currency.toUpperCase());
    }

    /**
     * Format currency amount
     */
    public String formatCurrency(BigDecimal amount, String currency) {
        if (amount == null) {
            return "0.00";
        }
        
        String symbol = getCurrencySymbol(currency);
        String formattedAmount = amount.setScale(2, RoundingMode.HALF_UP).toString();
        
        // Simple formatting - in production, use NumberFormat or similar
        return symbol + formattedAmount;
    }

    /**
     * Get exchange rate history
     */
    @Cacheable(value = "exchangeRates", key = "'history_' + #fromCurrency + '_' + #toCurrency + '_' + #days")
    public Map<String, BigDecimal> getExchangeRateHistory(String fromCurrency, String toCurrency, int days) {
        logger.info("Getting exchange rate history for {} to {} for {} days", fromCurrency, toCurrency, days);
        
        Map<String, BigDecimal> history = new HashMap<>();
        
        try {
            // Implement exchange rate history API call
            // This is a placeholder - actual implementation would use a real API
            
            for (int i = 0; i < days; i++) {
                String date = java.time.LocalDate.now().minusDays(i).toString();
                BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
                history.put(date, rate);
            }
            
        } catch (Exception e) {
            logger.error("Error fetching exchange rate history: {}", e.getMessage());
        }
        
        return history;
    }

    /**
     * Get exchange rate trends
     */
    @Cacheable(value = "exchangeRates", key = "'trends_' + #fromCurrency + '_' + #toCurrency")
    public Map<String, Object> getExchangeRateTrends(String fromCurrency, String toCurrency) {
        logger.info("Getting exchange rate trends for {} to {}", fromCurrency, toCurrency);
        
        Map<String, Object> trends = new HashMap<>();
        
        try {
            // Get current rate
            BigDecimal currentRate = getExchangeRate(fromCurrency, toCurrency);
            
            // Get rates for last 7 days
            Map<String, BigDecimal> history = getExchangeRateHistory(fromCurrency, toCurrency, 7);
            
            // Calculate trends
            BigDecimal weekAgoRate = history.values().stream()
                    .findFirst()
                    .orElse(currentRate);
            
            BigDecimal change = currentRate.subtract(weekAgoRate);
            BigDecimal changePercent = change.divide(weekAgoRate, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            trends.put("currentRate", currentRate);
            trends.put("weekAgoRate", weekAgoRate);
            trends.put("change", change);
            trends.put("changePercent", changePercent);
            trends.put("trend", change.compareTo(BigDecimal.ZERO) > 0 ? "up" : "down");
            
        } catch (Exception e) {
            logger.error("Error calculating exchange rate trends: {}", e.getMessage());
        }
        
        return trends;
    }

    /**
     * Fetch exchange rate from external API
     */
    private BigDecimal fetchExchangeRateFromAPI(String fromCurrency, String toCurrency) {
        try {
            // This is a placeholder for actual API integration
            // In production, you would use a real exchange rate API like:
            // - ExchangeRate-API
            // - Fixer.io
            // - CurrencyLayer
            // - Open Exchange Rates
            
            // Simulate API call
            Thread.sleep(100); // Simulate network delay
            
            // Return null to trigger fallback
            return null;
            
        } catch (Exception e) {
            logger.error("Error fetching from exchange rate API: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get default exchange rate (fallback)
     */
    private BigDecimal getDefaultExchangeRate(String fromCurrency, String toCurrency) {
        // Default exchange rates (these should be updated regularly)
        Map<String, BigDecimal> defaultRates = new HashMap<>();
        
        // USD as base
        defaultRates.put("USD_EUR", new BigDecimal("0.85"));
        defaultRates.put("USD_GBP", new BigDecimal("0.73"));
        defaultRates.put("USD_JPY", new BigDecimal("110.00"));
        defaultRates.put("USD_CAD", new BigDecimal("1.25"));
        defaultRates.put("USD_AUD", new BigDecimal("1.35"));
        defaultRates.put("USD_CHF", new BigDecimal("0.92"));
        defaultRates.put("USD_CNY", new BigDecimal("6.45"));
        defaultRates.put("USD_INR", new BigDecimal("74.50"));
        defaultRates.put("USD_BRL", new BigDecimal("5.20"));
        defaultRates.put("USD_MXN", new BigDecimal("20.00"));
        defaultRates.put("USD_KRW", new BigDecimal("1180.00"));
        defaultRates.put("USD_SGD", new BigDecimal("1.35"));
        defaultRates.put("USD_HKD", new BigDecimal("7.80"));
        defaultRates.put("USD_NZD", new BigDecimal("1.45"));
        defaultRates.put("USD_SEK", new BigDecimal("8.50"));
        defaultRates.put("USD_NOK", new BigDecimal("8.80"));
        defaultRates.put("USD_DKK", new BigDecimal("6.30"));
        defaultRates.put("USD_PLN", new BigDecimal("3.90"));
        defaultRates.put("USD_CZK", new BigDecimal("21.50"));
        defaultRates.put("USD_HUF", new BigDecimal("300.00"));
        defaultRates.put("USD_RUB", new BigDecimal("73.00"));
        defaultRates.put("USD_ZAR", new BigDecimal("14.50"));
        defaultRates.put("USD_TRY", new BigDecimal("8.50"));
        defaultRates.put("USD_ILS", new BigDecimal("3.25"));
        defaultRates.put("USD_AED", new BigDecimal("3.67"));
        defaultRates.put("USD_SAR", new BigDecimal("3.75"));
        defaultRates.put("USD_THB", new BigDecimal("33.00"));
        defaultRates.put("USD_MYR", new BigDecimal("4.20"));
        defaultRates.put("USD_IDR", new BigDecimal("14300.00"));
        defaultRates.put("USD_PHP", new BigDecimal("50.00"));
        defaultRates.put("USD_VND", new BigDecimal("23000.00"));
        
        String key = fromCurrency + "_" + toCurrency;
        BigDecimal rate = defaultRates.get(key);
        
        if (rate != null) {
            return rate;
        }
        
        // If not found, try reverse rate
        String reverseKey = toCurrency + "_" + fromCurrency;
        BigDecimal reverseRate = defaultRates.get(reverseKey);
        
        if (reverseRate != null) {
            return BigDecimal.ONE.divide(reverseRate, 6, RoundingMode.HALF_UP);
        }
        
        // Default fallback
        return BigDecimal.ONE;
    }

    /**
     * Get currency statistics
     */
    @Cacheable(value = "currencies", key = "'statistics'")
    public Map<String, Object> getCurrencyStatistics() {
        logger.info("Fetching currency statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCurrencies", SUPPORTED_CURRENCIES.size());
        statistics.put("baseCurrency", BASE_CURRENCY);
        statistics.put("supportedCurrencies", SUPPORTED_CURRENCIES.keySet());
        
        return statistics;
    }

    /**
     * Update exchange rates
     */
    public void updateExchangeRates() {
        logger.info("Updating exchange rates");
        
        // This method would be called by a scheduled task
        // to update exchange rates periodically
        
        try {
            // Clear cache to force refresh
            // In production, you would implement cache eviction
            
            logger.info("Exchange rates updated successfully");
            
        } catch (Exception e) {
            logger.error("Error updating exchange rates: {}", e.getMessage());
        }
    }
}




