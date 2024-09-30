package com.service.crypto_analyzer;

import com.service.crypto_analyzer.dto.FileReaderToDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

/**
 * Main class for the Crypto Advisor application.
 *
 * <p>This class is the entry point of the Spring Boot application and contains the main method to run the application.
 * It also defines a {@code CommandLineRunner} bean to load cryptocurrency data from CSV files into the database when the application starts.
 */
@SpringBootApplication
public class CryptoAnalyzerApplication {

	/**
	 * The main method to launch the Crypto Advisor application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoAnalyzerApplication.class, args);
	}

	/**
	 * Bean that creates a {@code CommandLineRunner} to automatically read and save cryptocurrency data from CSV files
	 * into the database when the application starts.
	 *
	 * @param fileReaderToDatabase the service responsible for reading and saving cryptocurrency data to the database
	 * @return a {@code CommandLineRunner} that executes on application startup
	 */
	@Bean
	public CommandLineRunner run(FileReaderToDatabase fileReaderToDatabase) {
		return args -> {
			// List of CSV files that contain cryptocurrency data
			List<String> csvFiles = Arrays.asList("BTC_values.csv", "DOGE_values.csv", "ETH_values.csv", "LTC_values.csv", "XRP_values.csv");

			// Reads the cryptocurrency data from the CSV files and saves it to the database
			fileReaderToDatabase.readCryptoData(csvFiles);
		};
	}
}