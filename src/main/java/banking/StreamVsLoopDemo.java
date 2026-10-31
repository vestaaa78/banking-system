package banking;

import java.util.ArrayList;
import java.util.List;

public class StreamVsLoopDemo {

    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        accounts.add(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        accounts.add(new CreditAccount(new AccountNumber("4444444444"), "Dave", 5000, 50000));
        accounts.add(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));

        System.out.println("Императивный стиль (цикл for-each)");
        List<BankAccount> resultLoop = filterByBalanceLoop(accounts, 100_000);
        printAccounts(resultLoop);

        System.out.println("\nДекларативный стиль (Stream API)");
        List<BankAccount> resultStream = filterByBalanceStream(accounts, 100_000);
        printAccounts(resultStream);

        System.out.println("\nРезультаты идентичны? " + resultLoop.equals(resultStream));
    }

    public static List<BankAccount> filterByBalanceLoop(List<BankAccount> accounts, double threshold) {
        List<BankAccount> result = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= threshold) {
                result.add(account);
            }
        }
        return result;
    }

    public static List<BankAccount> filterByBalanceStream(List<BankAccount> accounts, double threshold) {
        return accounts.stream()
                .filter(a -> a.getBalance() >= threshold)
                .toList();
    }

    private static void printAccounts(List<BankAccount> accounts) {
        for (BankAccount acc : accounts) {
            System.out.printf("  %-10s : %s (баланс: %.0f)%n",
                    acc.getOwner(),
                    acc.getClass().getSimpleName(),
                    acc.getBalance());
        }
    }
}