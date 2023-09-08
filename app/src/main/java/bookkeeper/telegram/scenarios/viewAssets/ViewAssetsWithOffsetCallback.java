package bookkeeper.telegram.scenarios.viewAssets;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;


class ViewAssetsWithOffsetCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;

    ViewAssetsWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
