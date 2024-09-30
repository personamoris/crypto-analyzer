package com.service.crypto_analyzer.repos;

import com.service.crypto_analyzer.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Crypto} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing various methods to interact with the database
 * for storing and retrieving cryptocurrency data. Additional custom query methods are also defined.
 */
@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    /**
     * Finds a list of {@link Crypto} entities based on the symbol of the cryptocurrency.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @return a list of {@link Crypto} entities matching the given symbol
     */
    List<Crypto> findBySymbol(String symbol);

    /**
     * Finds a list of {@link Crypto} entities based on the symbol and timestamp.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., BTC, ETH)
     * @param timestamp the specific timestamp for which data should be retrieved
     * @return a list of {@link Crypto} entities matching the given symbol and timestamp
     */
    List<Crypto> findBySymbolAndTimestamp(String symbol, long timestamp);

    /**
     * Finds a list of {@link Crypto} entities whose timestamps fall between the specified start and end times.
     *
     * @param startOfDay the start timestamp (inclusive)
     * @param endOfDay the end timestamp (inclusive)
     * @return a list of {@link Crypto} entities within the specified time range
     */
    List<Crypto> findByTimestampBetween(Long startOfDay, Long endOfDay);
}
