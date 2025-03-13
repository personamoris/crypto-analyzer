package com.service.crypto_analyzer.dto;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.service.crypto_analyzer.model.Crypto;
import com.service.crypto_analyzer.repos.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for reading cryptocurrency data from CSV files and saving it into the database.
 * It handles both inserting new records and updating existing ones based on the symbol and timestamp.
 */
@Component
public class FileReaderToDatabase {

    @Autowired
    private CryptoRepository cryptoRepository;

    /**
     * Reads cryptocurrency data from a list of CSV files and stores or updates it in the database.
     *
     * @param fileNames a list of CSV file names containing cryptocurrency data
     * @return a list of {@link Crypto} objects read from the files
     */
    public List<Crypto> readCryptoData(List<String> fileNames) {
        List<Crypto> cryptos = new ArrayList<>();

        for (String fileName : fileNames) {
            String csvFilePath = getClass().getResource("/prices/" + fileName).getFile();

            try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
                String[] values;
                boolean isFirstLine = true; // Flag to ignore the header line

                while ((values = csvReader.readNext()) != null) {
                    // Skip the header and empty lines
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    if (values.length == 0 || values[0].trim().isEmpty()) {
                        continue;
                    }

                    // Create Crypto object
                    Crypto crypto = new Crypto();
                    crypto.setTimestamp(Long.parseLong(values[0]));
                    crypto.setSymbol(values[1]);
                    crypto.setPrice(new BigDecimal(values[2]));
                    cryptos.add(crypto);
                }
            } catch (IOException | CsvValidationException e) {
                System.err.println("File not found: " + csvFilePath);
                e.printStackTrace();
            }
        }
        saveOrUpdateCryptoData(cryptos);
        return cryptos;
    }

    /**
     * Saves or updates cryptocurrency data in the database.
     * If an entry with the same symbol and timestamp already exists, its price is updated.
     * Otherwise, a new entry is inserted.
     *
     * @param cryptos a list of {@link Crypto} objects to be saved or updated in the database
     */
    public void saveOrUpdateCryptoData(List<Crypto> cryptos) {
        for (Crypto crypto : cryptos) {
            List<Crypto> existingCrypto = cryptoRepository.findBySymbolAndTimestamp(crypto.getSymbol(), crypto.getTimestamp());
            if (!existingCrypto.isEmpty()) {
                Crypto current = existingCrypto.get(0);
                current.setPrice(crypto.getPrice()); // Update price if record exists
                cryptoRepository.save(current);
            } else {
                cryptoRepository.save(crypto); // Insert new record if it doesn't exist
            }
        }
    }
}