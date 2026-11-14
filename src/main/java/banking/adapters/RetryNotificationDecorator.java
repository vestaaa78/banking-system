package banking.adapters;

import banking.ports.NotificationService;

public class RetryNotificationDecorator implements NotificationService {

    private final NotificationService delegate;
    private final int maxAttempts;

    public RetryNotificationDecorator(NotificationService delegate, int maxAttempts) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be at least 1");
        }
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void notify(String message) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                delegate.notify(message);
                return;
            } catch (Exception e) {
                lastException = e;
                System.out.println("[RETRY] Attempt " + attempt + " failed: " + e.getMessage());
            }
        }

        throw new RuntimeException(
                "Failed to send notification after " + maxAttempts + " attempts",
                lastException
        );
    }
}