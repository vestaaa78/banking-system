
package banking.concurrency;

import banking.application.TransferService;
import banking.domain.BankAccount;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class BankClient implements Runnable {

    private final String clientName;
    private final List<BankAccount> accounts;
    private final TransferService transferService;
    private final int operationsCount;

    private final AtomicInteger successfulTransfers = new AtomicInteger(0);
    private final AtomicInteger failedTransfers = new AtomicInteger(0);

    public BankClient(
            String clientName,
            List<BankAccount> accounts,
            TransferService transferService,
            int operationsCount
    ) {
        this.clientName = clientName;
        this.accounts = accounts;
        this.transferService = transferService;
        this.operationsCount = operationsCount;
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int i = 0; i < operationsCount; i++) {

            if (Thread.currentThread().isInterrupted()) {
                System.out.println(clientName + " interrupted");
                return;
            }

            try {
                int fromIndex = random.nextInt(accounts.size());

                int toIndex = random.nextInt(accounts.size());

                while (fromIndex == toIndex) {
                    toIndex = random.nextInt(accounts.size());
                }

                BankAccount from = accounts.get(fromIndex);
                BankAccount to = accounts.get(toIndex);

                double amount = 100 + random.nextInt(900);

                transferService.transfer(from, to, amount);

                successfulTransfers.incrementAndGet();

            } catch (RuntimeException exception) {
                failedTransfers.incrementAndGet();

                System.out.println(
                        clientName + ": transfer failed: "
                                + exception.getMessage()
                );
            }
        }

        System.out.println(
                clientName + " finished. Successful: "
                        + successfulTransfers.get()
                        + ", failed: "
                        + failedTransfers.get()
        );
    }

    public int getSuccessfulTransfers() {
        return successfulTransfers.get();
    }

    public int getFailedTransfers() {
        return failedTransfers.get();
    }
}
