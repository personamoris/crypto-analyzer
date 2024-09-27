package com.service.crypto_analyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CryptoAnalyzerApplicationTests {

	private final CryptoService cryptoService = new CryptoService();

	@Test
	public void testCalculateOldestPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641020400000L, 46797.61),
				new Crypto("BTC", 1641042000000L, 41743.58)
		);

		Crypto oldestCrypto = cryptoService.getOldestCrypto(cryptos);
		assertEquals(46813.21, oldestCrypto.getPrice(), 0.01);
	}

	@Test
	public void testCalculateNewestPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641020400000L, 46797.61),
				new Crypto("BTC", 1643695200000L, 38416.79)  // Newest
		);

		Crypto newestCrypto = cryptoService.getNewestCrypto(cryptos);
		assertEquals(38416.79, newestCrypto.getPrice(), 0.01);
	}

	@Test
	public void testCalculateMinPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641042000000L, 34875.00),  // Min
				new Crypto("BTC", 1643695200000L, 47222.66)
		);

		double minPrice = cryptoService.calculateMinPrice(cryptos);
		assertEquals(34875.00, minPrice, 0.01);
	}

	@Test
	public void testCalculateMaxPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641042000000L, 34875.00),
				new Crypto("BTC", 1643695200000L, 47222.66)  // Max
		);

		double maxPrice = cryptoService.calculateMaxPrice(cryptos);
		assertEquals(47222.66, maxPrice, 0.01);
	}

	@Test
	public void testCalculateNormalizedRange() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641042000000L, 34875.00),  // Min
				new Crypto("BTC", 1643695200000L, 47222.66)  // Max
		);

		double normalizedRange = cryptoService.calculateNormalizedRange(cryptos);
		assertEquals(0.354, normalizedRange, 0.001);  // Intervalul normalizat calculat
	}

	@Test
	public void testCryptoWithHighestNormalizedRange() {
		List<Crypto> btc = Arrays.asList(
				new Crypto("BTC", 1641009600000L, 46813.21),
				new Crypto("BTC", 1641042000000L, 34875.00),
				new Crypto("BTC", 1643695200000L, 47222.66)
		);

		List<Crypto> eth = Arrays.asList(
				new Crypto("ETH", 1641024000000L, 3715.32),
				new Crypto("ETH", 1641042000000L, 2336.52),
				new Crypto("ETH", 1643695200000L, 3823.82)
		);

		double btcNormalized = cryptoService.calculateNormalizedRange(btc);
		double ethNormalized = cryptoService.calculateNormalizedRange(eth);

		assertTrue(ethNormalized > btcNormalized);
	}

}
