
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
    private static final int OPERATIONS_PER_CLIENT = 10_000;
    private static final double INITIAL_BALANCE = 100_000.0;

    public static void main(String[] args) {

        List<BankAccount> accounts = new ArrayList<>();

        accounts.add(new DebitAccount(
                new AccountNumber("1000000001"),
                "Client A",
                INITIAL_BALANCE
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000002"),
                "Client B",
                INITIAL_BALANCE
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000003"),
                "Client C",
                INITIAL_BALANCE
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000004"),
                "Client D",
                INITIAL_BALANCE
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000005"),
                "Client E",
                INITIAL_BALANCE
        ));

        CommissionPolicy commissionPolicy = amount -> 0.0;

        NotificationService notificationService =
                message -> {
                };

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        TransactionJournal journal = new TransactionJournal();

        TransferService transferService = new TransferService(
                commissionPolicy,
                notificationService,
                blockedAccounts,
                journal
        );

        System.out.println("=== BANK SIMULATION ===");
        System.out.println("Initial balances:");

        printBalances(accounts, transferService);

        List<Thread> threads = new ArrayList<>();
        List<BankClient> clients = new ArrayList<>();

        for (int i = 0; i < CLIENTS_COUNT; i++) {

            BankClient client = new BankClient(
                    "Client " + (i + 1),
                    accounts,
                    transferService,
                    OPERATIONS_PER_CLIENT
            );

            Thread thread = new Thread(
                    client,
                    "BankClient-" + (i + 1)
            );

            clients.add(client);
            threads.add(thread);

            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();

                for (Thread clientThread : threads) {
                    clientThread.interrupt();
                }

                break;
            }
        }

        System.out.println("\n=== FINAL BANK STATE ===");

        printBalances(accounts, transferService);

        int successful = 0;
        int failed = 0;

        for (BankClient client : clients) {
            successful += client.getSuccessfulTransfers();
            failed += client.getFailedTransfers();
        }

        System.out.println("\n=== STATISTICS ===");
        System.out.println("Clients: " + CLIENTS_COUNT);
        System.out.println("Operations per client: " + OPERATIONS_PER_CLIENT);
        System.out.println("Successful transfers: " + successful);
        System.out.println("Failed transfers: " + failed);
        System.out.println("Transactions in journal: " + journal.size());

        double totalBalance = calculateTotalBalance(accounts);

        System.out.println("\n=== BALANCE CHECK ===");
        System.out.println("Expected total: 500000.0");
        System.out.println("Actual total: " + totalBalance);

        if (Math.abs(totalBalance - 500_000.0) < 0.001) {
            System.out.println("SUCCESS: Total balance is preserved.");
        } else {
            System.out.println("ERROR: Total balance has changed!");
        }

        boolean allFinished = true;

        for (Thread thread : threads) {
            if (thread.isAlive()) {
                allFinished = false;
                break;
            }
        }

        System.out.println("All client threads finished: " + allFinished);
    }

    private static void printBalances(
            List<BankAccount> accounts,
            TransferService transferService
    ) {
        synchronized (transferService) {
            for (BankAccount account : accounts) {
                System.out.printf(
                        "%s: %.2f%n",
                        account.getOwner(),
                        account.getBalance()
                );
            }

            System.out.printf(
                    "TOTAL: %.2f%n",
                    calculateTotalBalance(accounts)
            );
        }
    }

    private static double calculateTotalBalance(
            List<BankAccount> accounts
    ) {
        double total = 0;

        for (BankAccount account : accounts) {
            total += account.getBalance();
        }

        return total;
    }
}
