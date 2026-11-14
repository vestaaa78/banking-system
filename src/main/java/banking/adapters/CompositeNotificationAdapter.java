package banking.adapters;

import banking.ports.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class CompositeNotificationAdapter implements NotificationService {

    private final List<NotificationService> delegates = new ArrayList<>();

    public CompositeNotificationAdapter(NotificationService... adapters) {
        for (NotificationService adapter : adapters) {
            if (adapter == null) {
                throw new IllegalArgumentException("Notification adapter cannot be null");
            }
            delegates.add(adapter);
        }
    }

    public void addAdapter(NotificationService adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Notification adapter cannot be null");
        }
        delegates.add(adapter);
    }

    @Override
    public void notify(String message) {
        for (NotificationService delegate : delegates) {
            delegate.notify(message);
        }
    }

    public int getAdapterCount() {
        return delegates.size();
    }
}