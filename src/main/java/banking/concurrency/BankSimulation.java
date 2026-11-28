
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

    public static void main(String[] args) throws InterruptedException {

        List<BankAccount> accounts = new ArrayList<>();

        accounts.add(new DebitAccount(
                new AccountNumber("1000000001"),
                "Client A",
                100_000
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000002"),
                "Client B",
                100_000
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000003"),
                "Client C",
                100_000
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000004"),
                "Client D",
                100_000
        ));

        accounts.add(new DebitAccount(
                new AccountNumber("1000000005"),
                "Client E",
                100_000
        ));

        double initialTotal = calculateTotalBalance(accounts);

        System.out.println("=== BANK SIMULATION ===");
        System.out.printf("Initial total: %.2f%n", initialTotal);

        CommissionPolicy commissionPolicy = amount -> 0.0;

        NotificationService notificationService = message -> {
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

        List<BankClient> clients = new ArrayList<>();
        List<Thread> clientThreads = new ArrayList<>();

        int clientsCount = 5;
        int operationsPerClient = 10_000;

        for (int i = 1; i <= clientsCount; i++) {

            BankClient client = new BankClient(
                    "Client " + i,
                    accounts,
                    transferService,
                    operationsPerClient
            );

            clients.add(client);

            Thread clientThread = new Thread(
                    client,
                    "Client-Thread-" + i
            );

            clientThreads.add(clientThread);
        }

        BankMonitor bankMonitor = new BankMonitor(
                accounts,
                initialTotal,
                1000
        );

        Thread bankMonitorThread = new Thread(
                bankMonitor,
                "Bank-Monitor"
        );

        StatisticsMonitor statisticsMonitor = new StatisticsMonitor(
                clients,
                1000
        );

        Thread statisticsThread = new Thread(
                statisticsMonitor,
                "Statistics-Monitor"
        );

        bankMonitorThread.start();
        statisticsThread.start();

        for (Thread thread : clientThreads) {
            thread.start();
        }

        System.out.println("All client threads started.");

        for (Thread thread : clientThreads) {
            thread.join();
        }

        System.out.println("All clients have finished.");

        bankMonitor.stop();
        statisticsMonitor.stop();

        bankMonitorThread.interrupt();
        statisticsThread.interrupt();

        bankMonitorThread.join();
        statisticsThread.join();

        System.out.println("All monitor threads have finished.");

        System.out.println();
        System.out.println("=== FINAL BALANCES ===");

        for (BankAccount account : accounts) {
            System.out.printf(
                    "%s: %.2f%n",
                    account.getOwner(),
                    account.getBalance()
            );
        }

        double finalTotal = calculateTotalBalance(accounts);

        int successfulTransfers = 0;
        int failedTransfers = 0;
        int totalAttempts = 0;

        for (BankClient client : clients) {
            successfulTransfers += client.getSuccessfulTransfers();
            failedTransfers += client.getFailedTransfers();
            totalAttempts += client.getTotalAttempts();
        }

        System.out.println();
        System.out.println("=== FINAL REPORT ===");

        System.out.printf("Initial total: %.2f%n", initialTotal);
        System.out.printf("Final total: %.2f%n", finalTotal);

        System.out.println(
                "Successful transfers: " + successfulTransfers
        );

        System.out.println(
                "Failed transfers: " + failedTransfers
        );

        System.out.println(
                "Total attempts: " + totalAttempts
        );

        if (Math.abs(initialTotal - finalTotal) < 0.001) {
            System.out.println("Bank invariant: OK");
        } else {
            System.out.println("Bank invariant: ERROR");
        }

        if (successfulTransfers + failedTransfers == totalAttempts) {
            System.out.println("Statistics invariant: OK");
        } else {
            System.out.println("Statistics invariant: ERROR");
        }

        boolean allWorkersStopped = true;

        for (Thread thread : clientThreads) {
            if (thread.isAlive()) {
                allWorkersStopped = false;
            }
        }

        if (bankMonitorThread.isAlive() || statisticsThread.isAlive()) {
            allWorkersStopped = false;
        }

        if (allWorkersStopped) {
            System.out.println("All workers stopped.");
        } else {
            System.out.println("Some workers are still running.");
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
