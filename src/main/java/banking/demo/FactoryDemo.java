package banking.demo;

import banking.application.AccountFactory;
import banking.application.DefaultAccountFactory;
import banking.domain.*;

public class FactoryDemo {

    public static void main(String[] args) {
        System.out.println(" Демонстрация паттерна Factory \n");

        AccountFactory factory = new DefaultAccountFactory();

        System.out.println("1. Создание DebitAccount:");
        BankAccount debit = factory.create(
                AccountType.DEBIT,
                new AccountNumber("1111111111"),
                "Alice",
                5000
        );
        printAccount(debit);

        System.out.println("\n2. Создание SavingsAccount:");
        BankAccount savings = factory.create(
                AccountType.SAVINGS,
                new AccountNumber("2222222222"),
                "Bob",
                10000
        );
        printAccount(savings);
        if (savings instanceof SavingsAccount s) {
            System.out.println("   Минимальный неснижаемый остаток: " + s.getMinimumBalance());
        }

        System.out.println("\n3. Создание CreditAccount:");
        BankAccount credit = factory.create(
                AccountType.CREDIT,
                new AccountNumber("3333333333"),
                "Charlie",
                3000
        );
        printAccount(credit);
        if (credit instanceof CreditAccount c) {
            System.out.println("   Кредитный лимит: " + c.getCreditLimit());
        }

        System.out.println("\n4. Преимущества фабрики:");
        System.out.println("   - Клиент не знает о конкретных классах");
        System.out.println("   - Логика создания централизована");
        System.out.println("   - Легко добавить новый тип счёта");
        System.out.println("   - Можно подменить фабрику в тестах");
    }

    private static void printAccount(BankAccount account) {
        System.out.printf("   Тип: %s, Номер: %s, Владелец: %s, Баланс: %.0f%n",
                account.getClass().getSimpleName(),
                account.getId().value(),
                account.getOwner(),
                account.getBalance()
        );
    }
}