package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;


class ViewAssetsWithOffsetCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;
    @Getter
    private final int page;
    @Getter
    private final int pageSize;

    ViewAssetsWithOffsetCallback(int monthOffset, int page, int pageSize) {
        this.monthOffset = monthOffset;
        this.page = page;
        this.pageSize = pageSize;
    }
}
