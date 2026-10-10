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

        T richestAccount = accounts.get(0);
        double maxBalance = richestAccount.getBalance();

        for (int i = 1; i < accounts.size(); i++) {
            T currentAccount = accounts.get(i);
            if (currentAccount.getBalance() > maxBalance) {
                maxBalance = currentAccount.getBalance();
                richestAccount = currentAccount;
            }
        }

        return richestAccount;
    }
}