package banking;

import java.util.List;

public class StreamSortedDemo {

    public static void main(String[] args) {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();

        BankService bank = new BankService(repository, null, blockedAccounts);
        bank.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 5000));
        bank.openAccount(new SavingsAccount(new AccountNumber("1111111111"), "Alice", 150000, 10000));
        bank.openAccount(new CreditAccount(new AccountNumber("5555555555"), "Eve", 2000, 50000));
        bank.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 100000));
        bank.openAccount(new SavingsAccount(new AccountNumber("4444444444"), "Alice", 50000, 5000));

        System.out.println("=== Исходный порядок (порядок открытия) ===");
        printAccounts(repository.findAll());

        System.out.println("\n=== 1. richestFirst (по убыванию баланса) ===");
        printAccounts(bank.richestFirst());

        System.out.println("\n=== 2. sortedByAccountNumber (по номеру) ===");
        printAccounts(bank.sortedByAccountNumber());

        System.out.println("\n=== 3. sortedByTypeThenBalance (по типу, затем по балансу) ===");
        printAccounts(bank.sortedByTypeThenBalance());

        System.out.println("\n=== 4. sortedByOwnerThenNumber (по владельцу, затем по номеру) ===");
        printAccounts(bank.sortedByOwnerThenNumber());

        System.out.println("\n=== Проверка: исходный порядок не изменился ===");
        printAccounts(repository.findAll());
    }

    private static void printAccounts(List<BankAccount> accounts) {
        for (BankAccount acc : accounts) {
            System.out.printf("  %-10s %-15s : %.0f%n",
                    acc.getId().value(),
                    acc.getClass().getSimpleName(),
                    acc.getBalance());
        }
    }
}