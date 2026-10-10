package banking;

import java.util.List;

public class BankAccountUtils {
    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount(new AccountNumber("1111111111"), "Demo User 1", 1000));
        target.add(new DebitAccount(new AccountNumber("2222222222"), "Demo User 2", 2000));
    }
    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target lists must not be null");
        }

        for (T item : source) {
            target.add(item);
        }
    }
}