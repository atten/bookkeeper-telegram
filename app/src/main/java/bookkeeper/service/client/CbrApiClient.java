package bookkeeper.service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CbrApiClient {
    private static final String API_URL = "http://cbr.ru/scripts/XML_daily.asp";
    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Inject
    public CbrApiClient() { }

    public Map<Currency, BigDecimal> getRubExchangeRates(LocalDate date) throws IOException {
        var url = String.format("%s?date_req=%s", API_URL, date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        var request = new Request.Builder().url(url).build();
        var xmlMapper = new XmlMapper();
        try (var response = CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            var xml = response.body().string();
            var dto = xmlMapper.readValue(xml, Body.class);
            var map = new HashMap<Currency, BigDecimal>();
            for (var item : dto.items) {
                map.put(item.getCurrency(), item.getExchangeRate());
            }
            return map;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Valute {
        @JsonProperty("CharCode") String charCode;
        @JsonProperty("Value") String value;

        BigDecimal getExchangeRate() {
            return new BigDecimal(value.replace(',', '.'));
        }

        Currency getCurrency() {
            return Currency.getInstance(charCode);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Body {
        @JsonProperty("Valute")
        @JacksonXmlElementWrapper(useWrapping = false) List<Valute> items;
    }
}
