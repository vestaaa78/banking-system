package banking;

import java.util.List;
import java.util.function.Predicate;

public class PredicateCompositionDemo {

    public static void main(String[] args) {
        // Инициализация
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                new TransactionJournal()
        );
        BankService bank = new BankService(repository, transferService);

        // Создаём тестовые счета
        bank.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        bank.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        bank.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        bank.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Dave", 5000, 50000));
        bank.openAccount(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));

        blockedAccounts.block(new AccountNumber("2222222222"));

        System.out.println("Композиция Predicate \n");

        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked = account -> !blockedAccounts.isBlocked(account.getId());
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;
        Predicate<BankAccount> credit = CreditAccount.class::isInstance;

        System.out.println("1. Счета с балансом >= 100000:");
        List<BankAccount> richAccounts = bank.findAccounts(rich);
        printAccounts(richAccounts);

        System.out.println("\n2. Незаблокированные счета:");
        List<BankAccount> activeAccounts = bank.findAccounts(notBlocked);
        printAccounts(activeAccounts);

        System.out.println("\n3. Только DebitAccount:");
        List<BankAccount> debitAccounts = bank.findAccounts(debit);
        printAccounts(debitAccounts);

        System.out.println("\n4. Богатые И незаблокированные:");
        Predicate<BankAccount> richAndActive = rich.and(notBlocked);
        List<BankAccount> candidates = bank.findAccounts(richAndActive);
        printAccounts(candidates);

        System.out.println("\n5. Не DebitAccount (Savings + Credit):");
        Predicate<BankAccount> notDebit = debit.negate();
        List<BankAccount> nonDebitAccounts = bank.findAccounts(notDebit);
        printAccounts(nonDebitAccounts);

        System.out.println("\n6. Богатые ИЛИ CreditAccount:");
        Predicate<BankAccount> richOrCredit = rich.or(credit);
        List<BankAccount> richOrCreditAccounts = bank.findAccounts(richOrCredit);
        printAccounts(richOrCreditAccounts);
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
