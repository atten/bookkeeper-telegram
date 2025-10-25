package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class RemoveTransferCallback extends CallbackMessage {
    private final long transferId;

    RemoveTransferCallback(long transferId) {
        this.transferId = transferId;
    }

}
