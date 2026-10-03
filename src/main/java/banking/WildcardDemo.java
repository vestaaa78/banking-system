package banking;

import java.util.ArrayList;
import java.util.List;

public class WildcardDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация Wildcard ? extends\n");

        List<DebitAccount> debitAccounts = new ArrayList<>();
        debitAccounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 1000));
        debitAccounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 2000));

        List<SavingsAccount> savingsAccounts = new ArrayList<>();
        savingsAccounts.add(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 5000, 1000));

        double totalDebit = BankAccountUtils.totalBalance(debitAccounts);
        double totalSavings = BankAccountUtils.totalBalance(savingsAccounts);

        System.out.println("Общий баланс Debit счетов: " + totalDebit);
        System.out.println("Общий баланс Savings счетов: " + totalSavings);
        System.out.println();

        System.out.println("Попытка добавить элемент в список ? extends");
        tryToAddAccount(debitAccounts);
    }

    public static void tryToAddAccount(List<? extends BankAccount> accounts) {
        System.out.println("Компилятор заблокировал добавление. Причина объяснена ниже.");
    }
}