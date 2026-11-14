package banking.adapters;

import banking.ports.NotificationService;

public class LoggingNotificationDecorator implements NotificationService {

    private final NotificationService delegate;

    public LoggingNotificationDecorator(NotificationService delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        this.delegate = delegate;
    }

    @Override
    public void notify(String message) {
        System.out.println("[LOG] " + message);
        delegate.notify(message);
    }
}