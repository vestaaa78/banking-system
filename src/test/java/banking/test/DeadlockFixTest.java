package banking.test;

import banking.application.BlockedAccountRegistry;
import banking.application.TransactionJournal;
import banking.application.TransferService;
import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.CommissionPolicy;
import banking.domain.DebitAccount;
import banking.ports.NotificationService;

import java.util.concurrent.atomic.AtomicInteger;

public class DeadlockFixTest {

    private static final int OPERATIONS = 10_000;

    public static void main(String[] args) throws InterruptedException {

        BankAccount accountA = new DebitAccount(
                new AccountNumber("1000000001"),
                "Client A",
                100_000
        );

        BankAccount accountB = new DebitAccount(
                new AccountNumber("1000000002"),
                "Client B",
                100_000
        );

        CommissionPolicy commissionPolicy = amount -> 0.0;
        NotificationService notificationService = message -> { };

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        TransactionJournal journal = new TransactionJournal();

        TransferService transferService = new TransferService(
                commissionPolicy,
                notificationService,
                blockedAccounts,
                journal
        );

        AtomicInteger errors = new AtomicInteger(0);

        Thread thread1 = new Thread(() -> {

            for (int i = 0; i < OPERATIONS; i++) {
                try {
                    transferService.transfer(accountA, accountB, 1.0);
                } catch (RuntimeException exception) {
                    errors.incrementAndGet();
                    System.out.println(
                            "A -> B error: " + exception.getMessage()
                    );
                }
            }

            System.out.println("Thread 1 finished: A -> B");

        });

        Thread thread2 = new Thread(() -> {

            for (int i = 0; i < OPERATIONS; i++) {
                try {
                    transferService.transfer(accountB, accountA, 1.0);
                } catch (RuntimeException exception) {
                    errors.incrementAndGet();
                    System.out.println(
                            "B -> A error: " + exception.getMessage()
                    );
                }
            }

            System.out.println("Thread 2 finished: B -> A");

        });

        System.out.println("Starting simultaneous transfers...");

        thread1.start();
        thread2.start();

        thread1.join(30_000);
        thread2.join(30_000);

        System.out.println("\n========== DEADLOCK FIX TEST ==========");

        System.out.println("Thread 1 alive: " + thread1.isAlive());
        System.out.println("Thread 2 alive: " + thread2.isAlive());

        if (thread1.isAlive() || thread2.isAlive()) {
            System.out.println("ERROR: Threads did not finish in time");

            thread1.interrupt();
            thread2.interrupt();
        } else {
            System.out.println("SUCCESS: Both threads finished");
        }

        System.out.println("\n========== FINAL BALANCES ==========");

        System.out.printf(
                "Account A: %.2f%n",
                accountA.getBalance()
        );

        System.out.printf(
                "Account B: %.2f%n",
                accountB.getBalance()
        );

        double finalTotal =
                accountA.getBalance() + accountB.getBalance();

        System.out.printf("Final total: %.2f%n", finalTotal);
        System.out.println("Expected total: 200000.00");

        System.out.println("Transactions: " + journal.size());
        System.out.println("Errors: " + errors.get());

        if (!thread1.isAlive()
                && !thread2.isAlive()
                && Math.abs(finalTotal - 200_000.0) < 0.001
                && errors.get() == 0
                && journal.size() == OPERATIONS * 2) {

            System.out.println("\nTEST PASSED");

        } else {
            System.out.println("\nTEST FAILED");
        }
    }
}

