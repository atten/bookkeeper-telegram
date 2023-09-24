package bookkeeper.telegram.scenario.viewAnnualWorth;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.query.AssetQuery;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.StringJoiner;

import static bookkeeper.telegram.shared.StringUtils.getMonthName;

class AnnualWorthResponseFactory {
    private final AssetQuery assetQuery;

    @Inject
    AnnualWorthResponseFactory(AssetQuery assetQuery) {
        this.assetQuery = assetQuery;
    }

    String getAnnualWorth(TelegramUser user) {
        var rows = new StringJoiner("\n");

        var currentMonth = LocalDate.now().getMonth().getValue();
        for (int month = 1; month <= currentMonth; month++) {
            var monthOffset = month - currentMonth;
            var assets = assetQuery.getMonthlyAssets(user, monthOffset);
            var netAssetMillions = String.format("%.2f", AssetQuery.getNetAssetsValue(assets) / 1000 / 1000);
            var monthStr = StringUtils.capitalize(getMonthName(monthOffset));

            rows.add(String.format("`%3.3s %5.5sM`", monthStr, netAssetMillions));

        }

        return rows.toString();
    }
}
