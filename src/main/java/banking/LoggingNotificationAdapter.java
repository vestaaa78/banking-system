package banking;

import java.util.ArrayList;
import java.util.List;

public class LoggingNotificationAdapter implements NotificationService {

    private final List<String> log = new ArrayList<>();

    @Override
    public void notify(String message) {
        log.add("[LOG] " + message);
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