package banking.adapters;

import banking.ports.NotificationService;

public class FailingNotificationService implements NotificationService {

    private final int failCount;
    private int currentAttempt = 0;
    private final NotificationService fallback;

    public FailingNotificationService(int failCount, NotificationService fallback) {
        this.failCount = failCount;
        this.fallback = fallback;
    }

    @Override
    public void notify(String message) {
        currentAttempt++;
        if (currentAttempt <= failCount) {
            throw new RuntimeException("Simulated failure #" + currentAttempt);
        }
        if (fallback != null) {
            fallback.notify(message);
        }
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }
}