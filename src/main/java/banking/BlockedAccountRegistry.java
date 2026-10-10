package banking;

import java.util.HashSet;
import java.util.Set;

public class BlockedAccountRegistry {

    private final Set<AccountNumber> blocked = new HashSet<>();

    public void block(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        blocked.add(number);
    }

    public void unblock(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        blocked.remove(number);
    }

    public boolean isBlocked(AccountNumber number) {
        if (number == null) {
            return false;
        }
        return blocked.contains(number);
    }

    public int size() {
        return blocked.size();
    }
}