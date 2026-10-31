package banking;

import java.util.Optional;
import java.util.OptionalDouble;

public class NumericStreamDemo {

    public static void main(String[] args) {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bank = new BankService(repository, null, null);

        bank.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bank.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));
        bank.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 15000, 1000));
        bank.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Dave", 3000, 50000));

        System.out.println("Числовые операции Stream API\n");

        System.out.println("1. Общая сумма балансов:");
        double total = bank.totalBalance();
        System.out.println("   " + total);

        System.out.println("\n2. Средний баланс:");
        OptionalDouble average = bank.averageBalance();
        if (average.isPresent()) {
            System.out.println("   " + average.getAsDouble());
        } else {
            System.out.println("   нет счетов");
        }

        System.out.println("\n3. Самый богатый счёт:");
        Optional<BankAccount> richest = bank.richestAccount();
        richest.ifPresent(account ->
                System.out.println("   " + account.getOwner() + " с балансом " + account.getBalance()));

        System.out.println("\n4. Средний баланс (с дефолтом):");
        double avgOrDefault = bank.averageBalance().orElse(0.0);
        System.out.println("   " + avgOrDefault);

        System.out.println("\n5. Метка самого богатого счёта:");
        String label = bank.richestAccount()
                .map(a -> "VIP: " + a.getOwner() + " (" + a.getBalance() + ")")
                .orElse("No VIP clients");
        System.out.println("   " + label);

        System.out.println("\n6. Пустой репозиторий:");
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyBank = new BankService(emptyRepo, null, null);
        System.out.println("   Сумма: " + emptyBank.totalBalance());
        System.out.println("   Среднее: " + emptyBank.averageBalance());
        System.out.println("   Самый богатый: " + emptyBank.richestAccount());
    }
}