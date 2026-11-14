package banking.ports;

import banking.domain.Transaction;

public interface TransferListener {
    void onTransferCompleted(Transaction transaction);
}
