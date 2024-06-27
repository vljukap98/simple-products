package com.ljakovic.simpleproducts.util;

import com.ljakovic.simpleproducts.client.HnbClient;
import com.ljakovic.simpleproducts.client.dto.HnbRateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceCalculator {

    private final HnbClient hnbClient;

    @Autowired
    public PriceCalculator(HnbClient hnbClient) {
        this.hnbClient = hnbClient;
    }

    public BigDecimal calculatePrice(final BigDecimal priceEur, final String currency) {
        final HnbRateDto hnbRateDto = hnbClient.getRate(currency);
        final BigDecimal sellRate = new BigDecimal(hnbRateDto.getSellingRate().replace(",", "."));
        return priceEur.multiply(sellRate);
    }
}
