package banking;

import java.util.List;

public class StreamMapDemo {

    public static void main(String[] args) {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bank = new BankService(repository, null, null);

        bank.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bank.openAccount(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        bank.openAccount(new CreditAccount(new AccountNumber("3333333333"), "Charlie", 3000, 50000));

        System.out.println("Доменная модель (BankAccount)");
        System.out.println("Содержит: номер, владельца, баланс, методы withdraw/deposit/transfer");
        System.out.println("Используется внутри сервиса для бизнес-логики.\n");

        System.out.println("DTO (AccountSummary)");
        System.out.println("Содержит: только номер, тип, баланс");
        System.out.println("Используется для передачи во внешний слой (UI, API).\n");

        System.out.println("Результат getAccountSummaries()");
        List<AccountSummary> summaries = bank.getAccountSummaries();
        for (AccountSummary summary : summaries) {
            System.out.printf("  [%s] %s : %.2f%n",
                    summary.type(),
                    summary.number().value(),
                    summary.balance()
            );
        }

        System.out.println("\n--- Сравнение размеров ---");
        System.out.println("BankAccount (полная модель): " +
                "много полей и методов");
        System.out.println("AccountSummary (DTO): " +
                "только 3 поля, неизменяемый record");
    }
}