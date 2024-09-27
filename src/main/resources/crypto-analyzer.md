# Crypto Investment

## Description
It’s time for developers to invest their salaries on cryptos. The problem is that we have no idea about cryptos, so we are feeling a little bit afraid of which crypto to choose. But is this actually a problem? Of course not! We are developers and we always implement solutions for all the problems we face.

For this one, we decided to build a recommendation service. Initially, we will build something simple, and through iterations, we are going to transform it into a gold mine.

In the `CRYPTO_NAME_values.csv` (e.g. `BTC_values.csv`), you can find one month’s prices for one crypto in USD. The file has the following format:

1641009600000 BTC 46813.21

There are separate files for each crypto.

## Requirements for the Recommendation Service:

- Reads all the prices from the CSV files.
- Calculates oldest/newest/min/max for each crypto for the whole month.
- Exposes an endpoint that will return a descending sorted list of all the cryptos, comparing the normalized range (i.e. `(max-min)/min`).
- Exposes an endpoint that will return the oldest/newest/min/max values for a requested crypto.
- Exposes an endpoint that will return the crypto with the highest normalized range for a specific day.

## Things to Consider:

- **Documentation** is our best friend, so it will be good to share one for the endpoints.
- Initially, the cryptos are only five, but what if we want to include more? Will the recommendation service be able to scale?
- New cryptos pop up every day, so we might need to safeguard recommendation service endpoints from cryptos that are not currently supported.
- For some cryptos, it might be safe to invest by just checking only one month's time frame. However, for others, it might be more accurate to check six months or even a year. Will the recommendation service be able to handle this?

## Extra Mile for the Recommendation Service (Optional):

- In our company, we run everything on **Kubernetes**, so containerizing the recommendation service will add great value.
- **Malicious users** will always exist, so it will be really beneficial if at least we can rate-limit them (based on IP).