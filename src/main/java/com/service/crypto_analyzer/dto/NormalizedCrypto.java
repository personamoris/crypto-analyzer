package com.service.crypto_analyzer.dto;
/**
 * Data Transfer Object (DTO) representing normalized cryptocurrency data.
 * <p>
 * This class is used to encapsulate the symbol of a cryptocurrency, its maximum and minimum prices,
 * and the normalized value which represents the price variation as (max price - min price) / min price.
 */
public class NormalizedCryptoDTO {

    private String symbol;
    private double maxPrice;
    private double minPrice;
    private double normalizedValue;

    /**
     * Default constructor for creating an empty {@code NormalizedCryptoDTO}.
     */
    public NormalizedCryptoDTO() {
    }

    /**
     * Constructor for creating a {@code NormalizedCryptoDTO} with a symbol and normalized value.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @param normalizedValue the normalized value representing the price variation
     */
    public NormalizedCryptoDTO(String symbol, double normalizedValue) {
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
    public double getMaxPrice() {
        return maxPrice;
    }

    /**
     * Sets the maximum price of the cryptocurrency.
     *
     * @param maxPrice the maximum price to set
     */
    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * Returns the minimum price of the cryptocurrency.
     *
     * @return the minimum price
     */
    public double getMinPrice() {
        return minPrice;
    }

    /**
     * Sets the minimum price of the cryptocurrency.
     *
     * @param minPrice the minimum price to set
     */
    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * Returns the normalized value representing the price variation.
     *
     * @return the normalized value
     */
    public double getNormalizedValue() {
        return normalizedValue;
    }

    /**
     * Sets the normalized value representing the price variation.
     *
     * @param normalizedValue the normalized value to set
     */
    public void setNormalizedValue(double normalizedValue) {
        this.normalizedValue = normalizedValue;
    }
}