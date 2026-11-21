package banking.concurrency;

import banking.application.BlockedAccountRegistry;
import banking.application.TransactionJournal;
import banking.application.TransferService;
import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.CommissionPolicy;
import banking.domain.DebitAccount;
import banking.ports.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class BankSimulation {

    private static final int CLIENTS_COUNT = 5;
    private static final int OPERATIONS_PER_CLIENT = 50_000;
    private static final double INITIAL_BALANCE = 100_000.0;

    public static void main(String[] args) throws InterruptedException {
        List<BankAccount> accounts = new ArrayList<>();
        for (int i = 1; i <= CLIENTS_COUNT; i++) {
            accounts.add(new DebitAccount(
                    new AccountNumber("100000000" + i),
                    "Client " + i,
                    INITIAL_BALANCE
            ));
        }

        CommissionPolicy commissionPolicy = amount -> 0.0;
        NotificationService notificationService = message -> {};
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransactionJournal journal = new TransactionJournal();

        TransferService transferService = new TransferService(
                commissionPolicy,
                notificationService,
                blockedAccounts,
                journal
        );

        double initialTotal = calculateTotalBalance(accounts);
        System.out.println("=== НАЧАЛО СИМУЛЯЦИИ ===");
        System.out.println("INITIAL_TOTAL: " + initialTotal);

        List<Thread> threads = new ArrayList<>();
        List<BankClient> clients = new ArrayList<>();

        for (int i = 0; i < CLIENTS_COUNT; i++) {
            BankClient client = new BankClient(
                    "Client-" + (i + 1),
                    accounts,
                    transferService,
                    OPERATIONS_PER_CLIENT
            );
            clients.add(client);

            Thread thread = new Thread(client, "Thread-" + (i + 1));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        double finalTotal = calculateTotalBalance(accounts);
        System.out.println("\n=== КОНЕЦ СИМУЛЯЦИИ ===");
        System.out.println("FINAL_TOTAL:   " + finalTotal);

        double difference = initialTotal - finalTotal;
        System.out.println("РАЗНИЦА:       " + difference);

        if (Math.abs(difference) < 0.001) {
            System.out.println(" ИНВАРИАНТ СОХРАНЕН: INITIAL_TOTAL == FINAL_TOTAL");
        } else {
            System.out.println(" ИНВАРИАНТ НАРУШЕН: Деньги исчезли или появились из ниот!");
        }

        int totalSuccessful = clients.stream().mapToInt(BankClient::getSuccessfulTransfers).sum();
        int totalFailed = clients.stream().mapToInt(BankClient::getFailedTransfers).sum();
        System.out.println("\nУспешных операций: " + totalSuccessful);
        System.out.println("Неудачных операций: " + totalFailed);
        System.out.println("Записей в журнале: " + journal.size());
    }

    private static double calculateTotalBalance(List<BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
}