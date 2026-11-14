package banking.adapters;

import banking.domain.Transaction;
import banking.ports.TransferListener;

import java.util.ArrayList;
import java.util.List;

public class LoggingTransferListener implements TransferListener {

    private final List<String> log = new ArrayList<>();

    @Override
    public void onTransferCompleted(Transaction transaction) {
        String entry = String.format(
                "[LOG] Transfer #%d: %s -> %.2f (%s)",
                transaction.id(),
                transaction.account().value(),
                transaction.amount(),
                transaction.status()
        );
        log.add(entry);
    }

    public List<String> getLog() {
        return new ArrayList<>(log);
    }

    public void clear() {
        log.clear();
    }

    public int size() {
        return log.size();
    }
}