package banking.application;

import banking.domain.Transaction;
import banking.ports.TransferListener;

import java.util.ArrayList;
import java.util.List;
public class TransferEventPublisher {

    private final List<TransferListener> listeners = new ArrayList<>();
    public void subscribe(TransferListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    public void unsubscribe(TransferListener listener) {
        listeners.remove(listener);
    }
    public void publish(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        List<TransferListener> snapshot = new ArrayList<>(listeners);
        for (TransferListener listener : snapshot) {
            listener.onTransferCompleted(transaction);
        }
    }
    public int getSubscriberCount() {
        return listeners.size();
    }
    public void clear() {
        listeners.clear();
    }
}