package com.service.crypto_analyzer.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing normalized cryptocurrency data.
 * <p>
 * This class is used to encapsulate the symbol of a cryptocurrency, its maximum and minimum prices,
 * and the normalized value which represents the price variation as (max price - min price) / min price.
 */
public class NormalizedCrypto {

    private String symbol;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal normalizedValue;

    /**
     * Default constructor for creating an empty {@code NormalizedCryptoDTO}.
     */
    public NormalizedCrypto() {
    }

    /**
     * Constructor for creating a {@code NormalizedCryptoDTO} with a symbol and normalized value.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @param normalizedValue the normalized value representing the price variation
     */
    public NormalizedCrypto(String symbol, BigDecimal normalizedValue) {
        this.symbol = symbol;
        this.normalizedValue = normalizedValue;
    }

    /**
     * Returns the symbol of the cryptocurrency.
     *
     * @return the symbol of the cryptocurrency
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol of the cryptocurrency.
     *
     * @param symbol the symbol to set (e.g., BTC, ETH)
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the maximum price of the cryptocurrency.
     *
     * @return the maximum price
     */
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    /**
     * Sets the maximum price of the cryptocurrency.
     *
     * @param maxPrice the maximum price to set
     */
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * Returns the minimum price of the cryptocurrency.
     *
     * @return the minimum price
     */
    public BigDecimal getMinPrice() {
        return minPrice;
    }

    /**
     * Sets the minimum price of the cryptocurrency.
     *
     * @param minPrice the minimum price to set
     */
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * Returns the normalized value representing the price variation.
     *
     * @return the normalized value
     */
    public BigDecimal getNormalizedValue() {
        return normalizedValue;
    }

    /**
     * Sets the normalized value representing the price variation.
     *
     * @param normalizedValue the normalized value to set
     */
    public void setNormalizedValue(BigDecimal normalizedValue) {
        this.normalizedValue = normalizedValue;
    }
}