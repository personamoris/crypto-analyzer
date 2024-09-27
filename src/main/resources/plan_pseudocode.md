# Crypto Recommendation Service Plan

## General Plan for Implementation

1. **CSV File Reading:**
    - Create a service that reads all the CSV files for each cryptocurrency.
    - Store the data for each cryptocurrency in a data structure (e.g., a map containing the crypto symbol as the key and a list of objects with timestamp, price, and other relevant details).

2. **Calculating Values (oldest/newest/min/max) for Each Cryptocurrency:**
    - Create methods to calculate the oldest date, newest date, minimum value, and maximum value from the stored data for each cryptocurrency.

3. **Endpoint for Sorting Cryptocurrencies by Normalized Range:**
    - Calculate the normalized range for each cryptocurrency and sort them in descending order.
    - Expose this information via a REST endpoint.

4. **Endpoint for Returning Cryptocurrency Details (oldest/newest/min/max):**
    - Create an endpoint that, given a crypto symbol, will return the oldest, newest, min, and max values.

5. **Endpoint for Returning the Cryptocurrency with the Highest Normalized Range for a Specific Day:**
    - Create a method to extract and compare the normalized range for all cryptocurrencies on a specific day and return the one with the highest range.

6. **Considerations for Scalability and Security:**
    - Ensure the system can scale to accommodate more cryptocurrencies and additional requirements (such as checking for supported cryptocurrencies and rate-limiting).


## Pseudocode

```java
// Basic classes for cryptocurrencies
class CryptoPrice {
    long timestamp;
    String symbol;
    double price;
    
    // Constructor, getters, and setters
}

// Structure for min/max/newest/oldest values
class CryptoStatistics {
    double minPrice;
    double maxPrice;
    CryptoPrice oldestPrice;
    CryptoPrice newestPrice;

    // Constructor, getters, and setters
}

// DTO for returning the normalized range
class NormalizedCryptoDTO {
    String symbol;
    double normalizedRange;

    // Constructor, getters, and setters
}

// Recommendation service
class CryptoRecommendationService {
    
    // Structure to store the data for each cryptocurrency
    Map<String, List<CryptoPrice>> cryptoDataMap = new HashMap<>();
    
    // Method to read CSV files and populate cryptoDataMap
    public void readCsvFiles(List<String> filePaths) {
        for (String filePath : filePaths) {
            // Read each file and add data to the map
            List<CryptoPrice> prices = readCsvFile(filePath);
            String symbol = getSymbolFromFile(filePath); // Extract symbol from the file name
            cryptoDataMap.put(symbol, prices);
        }
    }

    // Method to calculate min/max/oldest/newest values for each cryptocurrency
    public CryptoStatistics calculateStatistics(String symbol) {
        List<CryptoPrice> prices = cryptoDataMap.get(symbol);
        
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        CryptoPrice oldestPrice = prices.get(0);
        CryptoPrice newestPrice = prices.get(0);
        
        for (CryptoPrice price : prices) {
            if (price.price < minPrice) {
                minPrice = price.price;
            }
            if (price.price > maxPrice) {
                maxPrice = price.price;
            }
            if (price.timestamp < oldestPrice.timestamp) {
                oldestPrice = price;
            }
            if (price.timestamp > newestPrice.timestamp) {
                newestPrice = price;
            }
        }

        return new CryptoStatistics(minPrice, maxPrice, oldestPrice, newestPrice);
    }

    // Method for sorting cryptocurrencies by normalized range
    public List<NormalizedCryptoDTO> getSortedCryptosByNormalizedRange() {
        List<NormalizedCryptoDTO> normalizedCryptoList = new ArrayList<>();
        
        for (String symbol : cryptoDataMap.keySet()) {
            CryptoStatistics stats = calculateStatistics(symbol);
            double normalizedRange = (stats.maxPrice - stats.minPrice) / stats.minPrice;
            normalizedCryptoList.add(new NormalizedCryptoDTO(symbol, normalizedRange));
        }

        // Sort in descending order by normalized range
        normalizedCryptoList.sort((a, b) -> Double.compare(b.normalizedRange, a.normalizedRange));

        return normalizedCryptoList;
    }

    // Endpoint for returning min/max/oldest/newest values for a cryptocurrency
    public CryptoStatistics getCryptoDetails(String symbol) {
        return calculateStatistics(symbol);
    }

    // Endpoint for returning the cryptocurrency with the highest normalized range on a specific day
    public String getCryptoWithHighestRangeForDay(long dayTimestamp) {
        String highestRangeCrypto = null;
        double highestRange = -1;
        
        for (String symbol : cryptoDataMap.keySet()) {
            List<CryptoPrice> prices = cryptoDataMap.get(symbol);
            for (CryptoPrice price : prices) {
                if (isSameDay(price.timestamp, dayTimestamp)) {
                    CryptoStatistics stats = calculateStatistics(symbol);
                    double normalizedRange = (stats.maxPrice - stats.minPrice) / stats.minPrice;
                    if (normalizedRange > highestRange) {
                        highestRange = normalizedRange;
                        highestRangeCrypto = symbol;
                    }
                }
            }
        }

        return highestRangeCrypto;
    }
    
    // Helper: Reads a CSV file and returns a list of CryptoPrice
    private List<CryptoPrice> readCsvFile(String filePath) {
        // Implementation to read file and populate CryptoPrice objects
        // Returns a list of prices
        return new ArrayList<>();
    }

    // Helper: Checks if two timestamps are on the same day
    private boolean isSameDay(long timestamp1, long timestamp2) {
        // Implementation for comparing timestamps
        return false;
    }
}