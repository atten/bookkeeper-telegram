package bookkeeper.service.client;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class MockedCbrApiClient extends CbrApiClient {
    @Inject
    public MockedCbrApiClient() {

    }

    @Override
    public Map<Currency, BigDecimal> getRubExchangeRates(LocalDate date) {
        var result = new HashMap<Currency, BigDecimal>();
        result.put(Currency.getInstance("USD"), new BigDecimal("30"));
        result.put(Currency.getInstance("EUR"), new BigDecimal("40"));
        return result;
    }
}
