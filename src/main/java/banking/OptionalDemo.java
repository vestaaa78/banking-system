package banking;

import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bank = new BankService(repository, null, null);

        bank.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bank.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 150000));
        bank.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));

        System.out.println("Демонстрация Optional\n");

        System.out.println("1. orElseThrow (ошибка, если не найдено):");
        try {
            BankAccount account = bank.findFirstRichAccount(100_000)
                    .orElseThrow(() -> new IllegalStateException("No rich account"));
            System.out.println("   Найден: " + account.getOwner() + " с балансом " + account.getBalance());
        } catch (IllegalStateException e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }

        System.out.println("\n2. map + orElse (преобразование или дефолт):");
        String label = bank.findFirstRichAccount(100_000)
                .map(a -> a.getId().value() + " : " + a.getBalance())
                .orElse("not found");
        System.out.println("   Метка: " + label);

        System.out.println("\n3. ifPresent (выполнить действие):");
        bank.findFirstRichAccount(100_000)
                .ifPresent(account -> System.out.println("   Обработка счёта: " + account.getOwner()));

        System.out.println("\n4. orElseGet (ленивое создание дефолта):");
        BankAccount accountOrDefault = bank.findFirstRichAccount(1_000_000)
                .orElseGet(() -> new DebitAccount(new AccountNumber("0000000000"), "Default", 0));
        System.out.println("   Результат: " + accountOrDefault.getOwner());

        System.out.println("\n5. Цепочка map:");
        String description = bank.findFirstRichAccount(100_000)
                .map(a -> a.getOwner())
                .map(String::toUpperCase)
                .map(name -> "VIP Client: " + name)
                .orElse("No VIP");
        System.out.println("   " + description);

        System.out.println("\n6. Пустой Optional (нет богатых счетов):");
        Optional<BankAccount> empty = bank.findFirstRichAccount(1_000_000);
        System.out.println("   isPresent: " + empty.isPresent());
        System.out.println("   isEmpty: " + empty.isEmpty());
    }
}