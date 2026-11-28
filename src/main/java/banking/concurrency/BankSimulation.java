
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankSimulation {

    public static void main(String[] args) {

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

        System.out.println("=== BANK SIMULATION 2.0 ===");
        System.out.printf("Initial total: %.2f%n", initialTotal);

        Map<String, BankAccount> accountMap = new HashMap<>();

        for (BankAccount account : accounts) {
            accountMap.put(
                    account.getNumber().value(),
                    account
            );
        }

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

        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        TransferDispatcher dispatcher = new TransferDispatcher(
                executor,
                transferService,
                accountMap
        );

        List<TransferRequest> requests = createRequests(1000);

        System.out.println("Created requests: " + requests.size());

        for (TransferRequest request : requests) {
            dispatcher.dispatch(request);
        }

        System.out.println("All requests were sent to dispatcher.");

        executor.shutdown();

        try {
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("Waiting for tasks to finish...");
            }

        } catch (InterruptedException exception) {

            executor.shutdownNow();

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Main thread was interrupted",
                    exception
            );
        }

        System.out.println();
        System.out.println("=== DISPATCHER STATISTICS ===");

        System.out.println(
                "Received requests: "
                        + dispatcher.getReceivedRequests()
        );

        System.out.println(
                "Submitted requests: "
                        + dispatcher.getSubmittedRequests()
        );

        System.out.println(
                "Completed requests: "
                        + dispatcher.getCompletedRequests()
        );

        System.out.println(
                "Failed requests: "
                        + dispatcher.getFailedRequests()
        );

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

        System.out.println();
        System.out.println("=== FINAL REPORT ===");

        System.out.printf("Initial total: %.2f%n", initialTotal);
        System.out.printf("Final total: %.2f%n", finalTotal);

        if (Math.abs(initialTotal - finalTotal) < 0.001) {
            System.out.println("Bank invariant: OK");
        } else {
            System.out.println("Bank invariant: ERROR");
        }

        System.out.println("Executor terminated: " + executor.isTerminated());
        System.out.println("All requests processed.");
    }

    private static List<TransferRequest> createRequests(int count) {

        List<TransferRequest> requests = new ArrayList<>();

        Random random = new Random();

        String[] accountNumbers = {
                "1000000001",
                "1000000002",
                "1000000003",
                "1000000004",
                "1000000005"
        };

        for (int i = 1; i <= count; i++) {

            String from;
            String to;
            double amount;

            if (i % 25 == 0) {

                from = "9999999999";
                to = "1000000002";
                amount = 500;

            }
            else if (i % 20 == 0) {

                from = "1000000001";
                to = "1000000002";
                amount = -100;

            }
            else if (i % 30 == 0) {

                from = "1000000001";
                to = "1000000002";
                amount = 60_000;

            }
            else if (i % 10 == 0) {

                from = "1000000001";
                to = "1000000002";
                amount = 250;

            }
            else {

                int fromIndex = random.nextInt(accountNumbers.length);
                int toIndex;

                do {
                    toIndex = random.nextInt(accountNumbers.length);
                } while (fromIndex == toIndex);

                from = accountNumbers[fromIndex];
                to = accountNumbers[toIndex];

                amount = 100 + random.nextInt(900);
            }

            requests.add(
                    new TransferRequest(
                            i,
                            from,
                            to,
                            amount
                    )
            );
        }

        return requests;
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
