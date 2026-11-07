package banking;

public class CountingNotificationDecorator implements NotificationService {

    private final NotificationService delegate;
    private int count = 0;

    public CountingNotificationDecorator(NotificationService delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        this.delegate = delegate;
    }

    @Override
    public void notify(String message) {
        count++;
        delegate.notify(message);
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}