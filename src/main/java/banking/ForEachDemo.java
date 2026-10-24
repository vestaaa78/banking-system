package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForEachDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация forEach с Consumer \n");

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "Charlie", 3000, 5000));

        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );
        List<AccountSummary> summaries = CollectionFunctions.map(accounts, toSummary);

        System.out.println("1. forEach с lambda-выражением:");
        Consumer<AccountSummary> printer = summary -> System.out.println(summary);
        CollectionFunctions.forEach(summaries, printer);
        System.out.println();

        System.out.println("2. forEach с method reference (System.out::println):");
        CollectionFunctions.forEach(summaries, System.out::println);
        System.out.println();

        System.out.println("3. forEach с композицией Consumer:");
        Consumer<AccountSummary> logNumber = summary -> System.out.print("Номер: " + summary.number().value());
        Consumer<AccountSummary> logBalance = summary -> System.out.println(" | Баланс: " + summary.balance());
        Consumer<AccountSummary> composed = logNumber.andThen(logBalance);
        CollectionFunctions.forEach(summaries, composed);
    }
}