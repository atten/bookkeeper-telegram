package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.service.query.AssetQuery;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static bookkeeper.service.telegram.StringUtils.*;

@Slf4j
class AssetsResponseFactory {
    private final AssetQuery assetQuery;

    @Inject
    AssetsResponseFactory(AssetQuery assetQuery) {
        this.assetQuery = assetQuery;
    }

    String getTotalAssets(TelegramUser user, int monthOffset, int page, int pageSize) {
        var exchangeDate = AssetQuery.getExchangeDate(monthOffset);
        var exchangeRates = assetQuery.getExchangeRates(user, exchangeDate);
        var assets = assetQuery.getMonthlyAssets(user, monthOffset);
        var netAssets = AssetQuery.getNetAssetsValue(assets);

        var content = new StringJoiner("\n\n");
        assets
            .stream()
            .skip((long) page * pageSize)
            .limit(pageSize)
            .map(asset ->
                "<b>%s</b>\n%s | %s".formatted(
                    getAccountDisplayName(asset.account()),
                    "%,.2f %s".formatted(asset.balance(), asset.account().getCurrency().getSymbol()),
                    "%.1f%%".formatted(asset.getExchangeBalance().floatValue() / netAssets * 100)
                )
            )
            .forEach(content::add);

        var result = new StringJoiner("\n\n");
        result
            .add(ICON_ACCOUNT + " Сводка по непустым счетам на конец <b>%s</b>:".formatted(getMonthYearRelative(monthOffset)))
            .add(content.toString())
            .add("________________________________")
            .add(ICON_RATES + " <b>Курс на %s</b>:\n%s".formatted(getDateShort(exchangeDate), exchangeRatesVerbose(exchangeRates)))
            .add(ICON_ASSETS + " <b>Итог за %s</b>: %,.2f %s".formatted(getMonthName(monthOffset), netAssets, assetQuery.getExchangeCurrency().getSymbol()));

        var pagesCount = (int) Math.ceil((double) assets.size() / pageSize);
        result.add("Страница %s / %s".formatted(page + 1, pagesCount));

        return result.toString();
    }

    /**
     * Example: EUR 100, USD 99
     */
    private String exchangeRatesVerbose(Map<Currency, BigDecimal> rates) {
        return rates
            .entrySet()
            .stream()
            .filter(entry -> !entry.getValue().equals(BigDecimal.ONE))  // skip native currency
            .map(entry -> "%s %s".formatted(entry.getKey().getCurrencyCode(), entry.getValue().floatValue()))
            .collect(Collectors.joining(" | "));
    }
}
