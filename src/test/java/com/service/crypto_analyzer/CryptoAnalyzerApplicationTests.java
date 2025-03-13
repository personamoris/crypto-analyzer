package com.service.crypto_analyzer;

import com.service.crypto_analyzer.model.Crypto;
import com.service.crypto_analyzer.repos.CryptoRepository;
import com.service.crypto_analyzer.services.CryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CryptoAnalyzerApplicationTests {

	@MockBean
	private CryptoRepository cryptoRepository;

	@Autowired
	private CryptoService cryptoService;


	@Test
	public void testCalculateOldestPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641020400000L, new BigDecimal("46797.61")),
				new Crypto("BTC", 1641042000000L, new BigDecimal("41743.58"))
		);

		Crypto oldestCrypto = cryptoService.getOldestCrypto(cryptos);

		assertEquals(new BigDecimal("46813.21"), oldestCrypto.getPrice());
	}

	@Test
	public void testCalculateNewestPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641020400000L, new BigDecimal("46797.61")),
				new Crypto("BTC", 1643695200000L, new BigDecimal("38416.79")) // Newest
		);

		Crypto newestCrypto = cryptoService.getNewestCrypto(cryptos);

		assertEquals(new BigDecimal("38416.79"), newestCrypto.getPrice());
	}

	@Test
	public void testCalculateMinPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641042000000L, new BigDecimal("34875.00")),  // Min
				new Crypto("BTC", 1643695200000L, new BigDecimal("47222.66"))
		);

		BigDecimal minPrice = cryptoService.calculateMinPrice(cryptos);
		assertEquals(new BigDecimal("34875.00"), minPrice);
	}

	@Test
	public void testCalculateMaxPrice() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641042000000L, new BigDecimal("34875.00")),
				new Crypto("BTC", 1643695200000L, new BigDecimal("47222.66"))  // Max
		);

		BigDecimal maxPrice = cryptoService.calculateMaxPrice(cryptos);
		assertEquals(new BigDecimal("47222.66"), maxPrice);
	}

	@Test
	public void testCalculateNormalizedRange() {
		List<Crypto> cryptos = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641042000000L, new BigDecimal("34875.00")),  // Min
				new Crypto("BTC", 1643695200000L, new BigDecimal("47222.66"))  // Max
		);

		BigDecimal normalizedRange = cryptoService.calculateNormalizedRange(cryptos);
		assertEquals(new BigDecimal("0.354"), normalizedRange);
	}

	@Test
	public void testCryptoWithHighestNormalizedRange() {
		List<Crypto> btc = Arrays.asList(
				new Crypto("BTC", 1641009600000L, new BigDecimal("46813.21")),
				new Crypto("BTC", 1641042000000L, new BigDecimal("34875.00")),
				new Crypto("BTC", 1643695200000L, new BigDecimal("47222.66"))
		);

		List<Crypto> eth = Arrays.asList(
				new Crypto("ETH", 1641024000000L, new BigDecimal("3715.32")),
				new Crypto("ETH", 1641042000000L, new BigDecimal("2336.52")),
				new Crypto("ETH", 1643695200000L, new BigDecimal("3823.82"))
		);

		BigDecimal btcNormalized = cryptoService.calculateNormalizedRange(btc);
		BigDecimal ethNormalized = cryptoService.calculateNormalizedRange(eth);

		assertTrue(ethNormalized.compareTo(btcNormalized) > 0);
	}
}
