package bookkeeper.telegram.scenario.viewAnnualWorth;

import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.service.query.AssetQuery;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.StringJoiner;

import static bookkeeper.service.telegram.StringUtils.getMonthName;

class AnnualWorthResponseFactory {
    private final AssetQuery assetQuery;

    @Inject
    AnnualWorthResponseFactory(AssetQuery assetQuery) {
        this.assetQuery = assetQuery;
    }

    String getAnnualWorth(TelegramUser user) {
        var rows = new StringJoiner("\n");
        var currentMonth = LocalDate.now().getMonth().getValue();
        float prevNetAssets = 0;

        // start from 0 month (0 = Dec of previous year, Jan = 1)
        for (int month = 0; month <= currentMonth; month++) {
            var monthOffset = month - currentMonth;
            var assets = assetQuery.getMonthlyAssets(user, monthOffset);
            var netAssets = AssetQuery.getNetAssetsValue(assets);
            var netAssetsDelta = netAssets - prevNetAssets;
            var netAssetMillions = "%.2f".formatted(netAssets / 1000 / 1000);
            var netAssetsDeltaKilos = "%+.2f".formatted(netAssetsDelta / 1000);
            var monthStr = StringUtils.capitalize(getMonthName(monthOffset));

            // don't include Dec of previous year
            if (month > 0) {
                rows.add(
                    "`%3.3s %5.5sM (%5.5sK)`"
                        .formatted(monthStr, netAssetMillions, netAssetsDeltaKilos)
                        // if delta does not fully fit into 5 characters, remove redundant comma
                        .replace(",K", " K")
                        // if delta has empty fraction part, remove extra precision
                        .replace(",00K", "K")
                );
            }

            prevNetAssets = netAssets;
        }

        return rows.toString();
    }
}
