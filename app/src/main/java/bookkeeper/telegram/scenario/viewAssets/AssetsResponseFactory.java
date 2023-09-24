package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.query.AssetQuery;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.StringUtils.*;

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
                String.format(
                    "<b>%s</b>\n%s | %s",
                    getAccountDisplayName(asset.account()),
                    String.format("%,.2f %s", asset.balance(), asset.account().getCurrency().getSymbol()),
                    String.format("%.1f%%", asset.getExchangeBalance().floatValue() / netAssets * 100)
                )
            )
            .forEach(content::add);

        var result = new StringJoiner("\n\n");
        result
            .add(String.format(ICON_ACCOUNT + " Сводка по непустым счетам на конец <b>%s</b>:", getMonthYearRelative(monthOffset)))
            .add(content.toString())
            .add("________________________________")
            .add(String.format(ICON_RATES + " <b>Курс на %s</b>:\n%s", getDateShort(exchangeDate), exchangeRatesVerbose(exchangeRates)))
            .add(String.format(ICON_ASSETS + " <b>Итог за %s</b>: %,.2f %s", getMonthName(monthOffset), netAssets, assetQuery.getExchangeCurrency().getSymbol()));

        var pagesCount = (int) Math.ceil((double) assets.size() / pageSize);
        result.add(String.format("Страница %s / %s", page + 1, pagesCount));

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
            .map(entry -> String.format("%s %s", entry.getKey().getCurrencyCode(), entry.getValue()))
            .collect(Collectors.joining(" | "));
    }
}
