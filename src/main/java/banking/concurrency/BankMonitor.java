
package banking.concurrency;

import banking.domain.BankAccount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BankMonitor implements Runnable {

    private final List<BankAccount> accounts;
    private final double expectedTotal;
    private final long intervalMillis;

    private volatile boolean running = true;

    public BankMonitor(
            List<BankAccount> accounts,
            double expectedTotal,
            long intervalMillis
    ) {
        this.accounts = accounts;
        this.expectedTotal = expectedTotal;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {

        System.out.println("Bank Monitor started");

        while (running && !Thread.currentThread().isInterrupted()) {

            double total = calculateConsistentTotal();

            if (Math.abs(total - expectedTotal) < 0.001) {
                System.out.printf(
                        "[MONITOR] total=%.2f OK%n",
                        total
                );
            } else {
                System.out.printf(
                        "[MONITOR] total=%.2f ERROR (expected=%.2f)%n",
                        total,
                        expectedTotal
                );
            }

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Bank Monitor stopped");
    }

    private double calculateConsistentTotal() {

        List<BankAccount> orderedAccounts =
                new ArrayList<>(accounts);

        orderedAccounts.sort(
                Comparator.comparing(
                        account -> account.getNumber().value()
                )
        );

        return calculateWithLocks(orderedAccounts, 0);
    }

    private double calculateWithLocks(
            List<BankAccount> orderedAccounts,
            int index
    ) {

        if (index == orderedAccounts.size()) {

            double total = 0;

            for (BankAccount account : orderedAccounts) {
                total += account.getBalance();
            }

            return total;
        }

        BankAccount account = orderedAccounts.get(index);

        synchronized (account) {
            return calculateWithLocks(
                    orderedAccounts,
                    index + 1
            );
        }
    }

    public void stop() {
        running = false;
    }
}
