package com.ljakovic.simpleproducts.util;

import com.ljakovic.simpleproducts.client.HnbClient;
import com.ljakovic.simpleproducts.client.dto.HnbRateDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceCalculator {

    private final HnbClient hnbClient;

    public PriceCalculator(HnbClient hnbClient) {
        this.hnbClient = hnbClient;
    }

    /**
     * Calculates the price in the specified currency based on the given price in EUR
     * Fetches the exchange rate from an external service (HNB) and uses it to convert the price
     *
     * @param priceEur The price in EUR to be converted
     * @param currency The target currency code, e.g. "USD"
     * @return The converted price in the specified currency
     */
    public BigDecimal calculatePrice(final BigDecimal priceEur, final String currency) {
        final HnbRateDto hnbRateDto = hnbClient.getRate(currency);
        final BigDecimal sellRate = new BigDecimal(hnbRateDto.getSellingRate().replace(",", "."));
        return priceEur.multiply(sellRate);
    }
}
