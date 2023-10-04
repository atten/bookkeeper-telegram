package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;


public class ViewAssetsCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;
    @Getter
    private final int page;
    @Getter
    private final int pageSize;

    ViewAssetsCallback(int monthOffset, int page, int pageSize) {
        this.monthOffset = monthOffset;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static ViewAssetsCallback firstPage() {
        return new ViewAssetsCallback(0, 0, 5);
    }
}
