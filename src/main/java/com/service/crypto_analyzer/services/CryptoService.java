package com.service.crypto_analyzer.services;

import com.service.crypto_analyzer.dto.NormalizedCrypto;
import com.service.crypto_analyzer.model.Crypto;
import com.service.crypto_analyzer.repos.CryptoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class that handles business logic related to cryptocurrency data, including
 * calculations of min/max prices, date conversions, and the determination of the highest
 * normalized range.
 */
@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);

    private final CryptoRepository cryptoRepository;

    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    /**
     * Converts a GMT date and time string to a timestamp in milliseconds.
     *
     * @param dateTimeString the date and time string in GMT (e.g., "2023-09-30T10:15:30")
     * @return the corresponding timestamp in milliseconds
     */
    public long convertGMTToMillis(String dateTimeString) {
        logger.debug("Converting GMT date time string to millis: {}", dateTimeString);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        long millis = zonedDateTime.toInstant().toEpochMilli();
        logger.debug("Converted millis: {}", millis);
        return millis;
    }

    /**
     * Converts a timestamp in milliseconds to a formatted GMT date and time string.
     *
     * @param millis the timestamp in milliseconds
     * @return a formatted string representing the date and time in GMT
     */
    public String convertMillisToGMT(long millis) {
        logger.debug("Converting millis to GMT: {}", millis);
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(formatter);
        logger.debug("Converted date time string: {}", formattedDate);
        return formattedDate;
    }

    /**
     * Retrieves all cryptocurrency data for a specific symbol from the database.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @return a list of {@link Crypto} entities matching the given symbol
     */
    public List<Crypto> getCryptoDataBySymbol(String symbol) {
        logger.info("Fetching cryptocurrency data for symbol: {}", symbol);
        return cryptoRepository.findBySymbol(symbol);
    }

    /**
     * Retrieves all cryptocurrency data from the database.
     *
     * @return a list of all {@link Crypto} entities
     */
    public List<Crypto> getCryptoFindAll() {
        logger.info("Fetching all cryptocurrency data");
        return cryptoRepository.findAll();
    }

    /**
     * Calculates the minimum price from a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the minimum price
     */
    public BigDecimal calculateMinPrice(List<Crypto> cryptos) {
        logger.debug("Calculating minimum price for a list of {} cryptos", cryptos.size());
        BigDecimal minPrice = cryptos.stream().map(Crypto::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        logger.debug("Calculated min price: {}", minPrice);
        return minPrice;
    }

    /**
     * Calculates the maximum price from a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the maximum price
     */
    public BigDecimal calculateMaxPrice(List<Crypto> cryptos) {
        logger.debug("Calculating maximum price for a list of {} cryptos", cryptos.size());
        BigDecimal maxPrice = cryptos.stream().map(Crypto::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        logger.debug("Calculated max price: {}", maxPrice);
        return maxPrice;
    }

    /**
     * Retrieves the oldest cryptocurrency data (by timestamp) from a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the oldest {@link Crypto} entity, or null if the list is empty
     */
    public Crypto getOldestCrypto(List<Crypto> cryptos) {
        logger.debug("Fetching oldest cryptocurrency data from a list of {} cryptos", cryptos.size());
        return cryptos.stream().min(Comparator.comparingLong(Crypto::getTimestamp)).orElse(null);
    }

    /**
     * Retrieves the newest cryptocurrency data (by timestamp) from a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the newest {@link Crypto} entity, or null if the list is empty
     */
    public Crypto getNewestCrypto(List<Crypto> cryptos) {
        logger.debug("Fetching newest cryptocurrency data from a list of {} cryptos", cryptos.size());
        return cryptos.stream().max(Comparator.comparingLong(Crypto::getTimestamp)).orElse(null);
    }

    /**
     * Calculates the normalized range (max - min) / min for a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the normalized range, rounded to three decimal places
     */
    public BigDecimal calculateNormalizedRange(List<Crypto> cryptos) {
        logger.debug("Calculating normalized range for {} cryptos", cryptos.size());
        BigDecimal minPrice = calculateMinPrice(cryptos);
        BigDecimal maxPrice = calculateMaxPrice(cryptos);
        if (minPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal normalizedRange = (maxPrice.subtract(minPrice)).divide(minPrice, 3, RoundingMode.HALF_UP);
            logger.debug("Calculated normalized range: {}", normalizedRange);
            return normalizedRange;
        } else {
            logger.warn("Min price is zero, cannot calculate normalized range");
            return BigDecimal.ZERO;
        }
    }

    /**
     * Retrieves a list of normalized cryptocurrency data, sorted in descending order by normalized value.
     * The normalized value is calculated as (max price - min price) / min price for each symbol.
     *
     * @return a list of {@link NormalizedCrypto} objects representing the sorted normalized values
     */
    public List<NormalizedCrypto> getSortedCryptosByNormalizedValue() {
        logger.info("Fetching and sorting cryptocurrencies by normalized value");
        Map<String, List<Crypto>> groupedBySymbol = getCryptoFindAll().stream()
                .collect(Collectors.groupingBy(Crypto::getSymbol));

        return groupedBySymbol.entrySet().stream().map(entry -> {
                    String symbol = entry.getKey();
                    List<Crypto> cryptosForSymbol = entry.getValue();
                    BigDecimal maxPrice = cryptosForSymbol.stream().map(Crypto::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal minPrice = cryptosForSymbol.stream().map(Crypto::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal normalizedValue = BigDecimal.ZERO;
                    if (minPrice.compareTo(BigDecimal.ZERO) > 0) {
                        normalizedValue = maxPrice.subtract(minPrice)
                                .divide(minPrice, 3, RoundingMode.HALF_UP);
                    }


                    NormalizedCrypto normalizedCrypto = new NormalizedCrypto();
                    normalizedCrypto.setSymbol(symbol);
                    normalizedCrypto.setMaxPrice(maxPrice);
                    normalizedCrypto.setMinPrice(minPrice);
                    normalizedCrypto.setNormalizedValue(normalizedValue.doubleValue());
                    return normalizedCrypto;
                }).sorted(Comparator.comparingDouble(NormalizedCrypto::getNormalizedValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the cryptocurrency with the highest normalized range for a specific day.
     *
     * @param dateString the date string in "dd-MM-yyyy" format
     * @return the {@link NormalizedCrypto} with the highest normalized range for the specified day
     */
    public NormalizedCrypto getCryptoWithHighestNormalizedRangeForDay(String dateString) {
        logger.info("Fetching cryptocurrency with the highest normalized range for day: {}", dateString);
        LocalDate day = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(23, 59, 59);

        long startTimestamp = convertGMTToMillis(startOfDay.toString());
        long endTimestamp = convertGMTToMillis(endOfDay.toString());

        List<Crypto> cryptos = cryptoRepository.findByTimestampBetween(startTimestamp, endTimestamp);
        if (cryptos.isEmpty()) {
            logger.warn("No records found for the specified day: {}", dateString);
            System.err.println(dateString);
            throw new RuntimeException("No records found for the specified day.");
        }

        return cryptos.stream()
                .collect(Collectors.groupingBy(Crypto::getSymbol))
                .entrySet().stream()
                .map(entry -> {
                    String symbol = entry.getKey();
                    List<Crypto> cryptoGroup = entry.getValue();
                    BigDecimal minPrice = calculateMinPrice(cryptoGroup);
                    BigDecimal maxPrice = calculateMaxPrice(cryptoGroup);
                    BigDecimal normalizedValue = BigDecimal.ZERO;
                    if (minPrice.compareTo(BigDecimal.ZERO) > 0) {
                        normalizedValue = maxPrice.subtract(minPrice)
                                .divide(minPrice, 3, RoundingMode.HALF_UP);
                    }
                    return new NormalizedCrypto(symbol, normalizedValue.doubleValue());
                })
                .max(Comparator.comparingDouble(NormalizedCrypto::getNormalizedValue))
                .orElseThrow(() -> new RuntimeException("Could not calculate normalized range."));
    }
}