package banking.adapters;

import banking.domain.Transaction;
import banking.ports.TransferListener;

public class AlertTransferListener implements TransferListener {

    private final double threshold;
    private int alertCount = 0;

    public AlertTransferListener(double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        this.threshold = threshold;
    }

    @Override
    public void onTransferCompleted(Transaction transaction) {
        if (transaction.amount() >= threshold) {
            alertCount++;
            System.out.println("[ALERT] Large transfer detected: " +
                    transaction.amount() + " (threshold: " + threshold + ")");
        }
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void reset() {
        alertCount = 0;
    }
}