package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;


@Getter
public class ViewAssetsCallback extends CallbackMessage {
    private final int monthOffset;
    private final int page;
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
