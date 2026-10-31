package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateCompositionDemo {

    public static void main(String[] args) {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                new TransactionJournal()
        );

        BankService bank = new BankService(repository, transferService, blockedAccounts);

        bank.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        bank.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        bank.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        bank.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Dave", 5000, 50000));
        bank.openAccount(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));

        blockedAccounts.block(new AccountNumber("2222222222"));

        System.out.println("Композиция Predicate\n");

        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked = account -> !blockedAccounts.isBlocked(account.getId());
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;
        Predicate<BankAccount> credit = CreditAccount.class::isInstance;

        System.out.println("1. Счета с балансом >= 100000:");
        printAccounts(bank.findAccounts(rich));

        System.out.println("\n2. Незаблокированные счета:");
        printAccounts(bank.findAccounts(notBlocked));

        System.out.println("\n3. Только DebitAccount:");
        printAccounts(bank.findAccounts(debit));

        System.out.println("\n4. Богатые И незаблокированные:");
        printAccounts(bank.findAccounts(rich.and(notBlocked)));

        System.out.println("\n5. Не DebitAccount:");
        printAccounts(bank.findAccounts(debit.negate()));

        System.out.println("\n6. Богатые ИЛИ CreditAccount:");
        printAccounts(bank.findAccounts(rich.or(credit)));
    }

    private static void printAccounts(List<BankAccount> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("   (пусто)");
            return;
        }
        for (BankAccount acc : accounts) {
            System.out.printf("   %-10s : %s (баланс: %.0f)%n",
                    acc.getOwner(),
                    acc.getClass().getSimpleName(),
                    acc.getBalance());
        }
    }
}