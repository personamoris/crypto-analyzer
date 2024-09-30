package com.service.crypto_analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * REST controller that provides API endpoints for cryptocurrency data operations.
 * <p>
 * This controller handles requests related to retrieving cryptocurrency statistics and calculating
 * the highest normalized range.
 */
@RestController
@RequestMapping("/api/cryptos")
public class CryptoController {

    private final FileReaderToDatabase fileReaderToDatabase;
    private final CryptoService cryptoService;

    /**
     * Constructor to initialize {@code CryptoController} with necessary services.
     *
     * @param fileReaderToDatabase service to handle reading data from CSV files
     * @param cryptoService service to manage cryptocurrency data operations
     */
    @Autowired
    public CryptoController(FileReaderToDatabase fileReaderToDatabase, CryptoService cryptoService) {
        this.fileReaderToDatabase = fileReaderToDatabase;
        this.cryptoService = cryptoService;
    }

    /**
     * Retrieves statistics for a specific cryptocurrency based on its symbol.
     * The stats include the oldest price, newest price, minimum price, and maximum price.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @return a formatted string containing the cryptocurrency statistics
     * @throws IOException if an error occurs while reading the data
     */
    @GetMapping("/{symbol}/stats")
    public String getCryptoStats(@PathVariable String symbol) throws IOException {
        List<Crypto> cryptos = cryptoService.getCryptoDataBySymbol(symbol);

        if (cryptos.isEmpty()) {
            return "Criptomoneda nu a fost găsită.";
        }

        double minPrice = cryptoService.calculateMinPrice(cryptos);
        double maxPrice = cryptoService.calculateMaxPrice(cryptos);
        Crypto oldest = cryptoService.getOldestCrypto(cryptos);
        Crypto newest = cryptoService.getNewestCrypto(cryptos);

        return String.format("Crypto %s:\nOldest Price: %f\nNewest Price: %f\nMin Price: %f\nMax Price: %f",
                symbol, oldest.getPrice(), newest.getPrice(), minPrice, maxPrice);
    }

    /**
     * Retrieves the list of cryptocurrencies ordered by the highest normalized range, in descending order.
     * The normalized range is calculated based on the price variation of each cryptocurrency.
     *
     * @return a formatted string containing the list of cryptocurrencies and their normalized range values
     */
    @GetMapping("/highest-range")
    public String getCryptoWithHighestNormalizedRange() {
        List<NormalizedCryptoDTO> sortedCryptosByNormalizedValue = cryptoService.getSortedCryptosByNormalizedValue();
        StringBuilder response = new StringBuilder();
        for (NormalizedCryptoDTO normalizedCrypto : sortedCryptosByNormalizedValue) {
            response.append(String.format("Crypto: %s  Valoare normalizat: %f\n", normalizedCrypto.getSymbol(), normalizedCrypto.getNormalizedValue()));
        }
        return response.toString();
    }

    /**
     * Retrieves the cryptocurrency with the highest normalized range for a specific day.
     *
     * @param date the date in the format YYYY-MM-DD for which the highest normalized range is calculated
     * @return a formatted string containing the cryptocurrency and its normalized range for the specified day
     */
    @GetMapping("/{date}/highest-normalized-range")
    public String getCryptoWithHighestNormalizedRangeForDay(@PathVariable String date) {
        // Calls the service method to find the cryptocurrency with the highest normalized range for the specified date
        NormalizedCryptoDTO normalizedCryptoDTO = cryptoService.getCryptoWithHighestNormalizedRangeForDay(date);
        return String.format("Crypto %s:\nNormalized Range: %.4f",
                normalizedCryptoDTO.getSymbol(),
                normalizedCryptoDTO.getNormalizedValue());
    }
}