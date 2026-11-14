package banking.adapters;

import banking.domain.Transaction;
import banking.ports.TransferListener;

public class StatisticsTransferListener implements TransferListener {

    private int totalTransfers = 0;
    private double totalAmount = 0.0;
    private double maxAmount = 0.0;

    @Override
    public void onTransferCompleted(Transaction transaction) {
        totalTransfers++;
        totalAmount += transaction.amount();
        if (transaction.amount() > maxAmount) {
            maxAmount = transaction.amount();
        }
    }

    public int getTotalTransfers() {
        return totalTransfers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public double getAverageAmount() {
        return totalTransfers == 0 ? 0.0 : totalAmount / totalTransfers;
    }

    public void reset() {
        totalTransfers = 0;
        totalAmount = 0.0;
        maxAmount = 0.0;
    }
}