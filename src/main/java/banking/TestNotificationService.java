package banking;

import java.util.ArrayList;
import java.util.List;

public class TestNotificationService implements NotificationService {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void notify(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}