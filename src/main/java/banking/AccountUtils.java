package banking;

import java.util.List;

public final class AccountUtils {

    private AccountUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null) {
            throw new IllegalArgumentException("Список счетов не может быть null");
        }
        if (accounts.isEmpty()) {
            return null;
        }
        return accounts.stream()
                .max(java.util.Comparator.comparingDouble(BankAccount::getBalance))
                .orElse(null);
    }
}