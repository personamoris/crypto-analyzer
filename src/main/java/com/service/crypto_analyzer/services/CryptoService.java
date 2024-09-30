package com.service.crypto_analyzer;

import com.service.crypto_analyzer.model.Crypto;
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
    public double calculateMinPrice(List<Crypto> cryptos) {
        logger.debug("Calculating minimum price for a list of {} cryptos", cryptos.size());
        double minPrice = cryptos.stream().mapToDouble(Crypto::getPrice).min().orElse(0.0);
        logger.debug("Calculated min price: {}", minPrice);
        return minPrice;
    }

    /**
     * Calculates the maximum price from a list of cryptocurrency data.
     *
     * @param cryptos a list of {@link Crypto} entities
     * @return the maximum price
     */
    public double calculateMaxPrice(List<Crypto> cryptos) {
        logger.debug("Calculating maximum price for a list of {} cryptos", cryptos.size());
        double maxPrice = cryptos.stream().mapToDouble(Crypto::getPrice).max().orElse(0.0);
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
    public double calculateNormalizedRange(List<Crypto> cryptos) {
        logger.debug("Calculating normalized range for {} cryptos", cryptos.size());
        double minPrice = calculateMinPrice(cryptos);
        double maxPrice = calculateMaxPrice(cryptos);
        double normalizedRange = (maxPrice - minPrice) / minPrice;
        BigDecimal bd = new BigDecimal(normalizedRange).setScale(3, RoundingMode.HALF_UP);
        logger.debug("Calculated normalized range: {}", bd.doubleValue());
        return bd.doubleValue();
    }

    /**
     * Retrieves a list of normalized cryptocurrency data, sorted in descending order by normalized value.
     * The normalized value is calculated as (max price - min price) / min price for each symbol.
     *
     * @return a list of {@link NormalizedCryptoDTO} objects representing the sorted normalized values
     */
    public List<NormalizedCryptoDTO> getSortedCryptosByNormalizedValue() {
        logger.info("Fetching and sorting cryptocurrencies by normalized value");
        Map<String, List<Crypto>> groupedBySymbol = getCryptoFindAll().stream()
                .collect(Collectors.groupingBy(Crypto::getSymbol));

        return groupedBySymbol.entrySet().stream().map(entry -> {
                    String symbol = entry.getKey();
                    List<Crypto> cryptosForSymbol = entry.getValue();
                    double maxPrice = cryptosForSymbol.stream().mapToDouble(Crypto::getPrice).max().orElse(0.0);
                    double minPrice = cryptosForSymbol.stream().mapToDouble(Crypto::getPrice).min().orElse(0.0);
                    double normalizedValue = minPrice > 0 ? (maxPrice - minPrice) / minPrice : 0;

                    NormalizedCryptoDTO normalizedCrypto = new NormalizedCryptoDTO();
                    normalizedCrypto.setSymbol(symbol);
                    normalizedCrypto.setMaxPrice(maxPrice);
                    normalizedCrypto.setMinPrice(minPrice);
                    normalizedCrypto.setNormalizedValue(normalizedValue);
                    return normalizedCrypto;
                }).sorted(Comparator.comparingDouble(NormalizedCryptoDTO::getNormalizedValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the cryptocurrency with the highest normalized range for a specific day.
     *
     * @param dateString the date string in "dd-MM-yyyy" format
     * @return the {@link NormalizedCryptoDTO} with the highest normalized range for the specified day
     */
    public NormalizedCryptoDTO getCryptoWithHighestNormalizedRangeForDay(String dateString) {
        logger.info("Fetching cryptocurrency with the highest normalized range for day: {}", dateString);
        LocalDate day = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(23, 59, 59);

        long startTimestamp = convertGMTToMillis(startOfDay.toString());
        long endTimestamp = convertGMTToMillis(endOfDay.toString());

        List<Crypto> cryptos = cryptoRepository.findByTimestampBetween(startTimestamp, endTimestamp);
        if (cryptos.isEmpty()) {
            logger.warn("No records found for the specified day: {}", dateString);
            throw new RuntimeException("No records found for the specified day.");
        }

        return cryptos.stream()
                .collect(Collectors.groupingBy(Crypto::getSymbol))
                .entrySet().stream()
                .map(entry -> {
                    String symbol = entry.getKey();
                    List<Crypto> cryptoGroup = entry.getValue();
                    double minPrice = calculateMinPrice(cryptoGroup);
                    double maxPrice = calculateMaxPrice(cryptoGroup);
                    double normalizedRange = (maxPrice - minPrice) / minPrice;
                    return new NormalizedCryptoDTO(symbol, normalizedRange);
                })
                .max(Comparator.comparingDouble(NormalizedCryptoDTO::getNormalizedValue))
                .orElseThrow(() -> new RuntimeException("Could not calculate normalized range."));
    }
}