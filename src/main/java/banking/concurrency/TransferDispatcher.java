
package banking.concurrency;

import banking.application.TransferService;
import banking.domain.BankAccount;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class TransferDispatcher {

    private final ExecutorService executor;
    private final TransferService transferService;
    private final Map<String, BankAccount> accounts;

    private final AtomicInteger receivedRequests = new AtomicInteger(0);
    private final AtomicInteger submittedRequests = new AtomicInteger(0);
    private final AtomicInteger completedRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);

    private final AtomicInteger printedTasks = new AtomicInteger(0);

    public TransferDispatcher(
            ExecutorService executor,
            TransferService transferService,
            Map<String, BankAccount> accounts
    ) {
        this.executor = executor;
        this.transferService = transferService;
        this.accounts = accounts;
    }

    public void dispatch(TransferRequest request) {

        receivedRequests.incrementAndGet();

        try {
            executor.submit(() -> processRequest(request));

            submittedRequests.incrementAndGet();

        } catch (RejectedExecutionException exception) {
            System.out.println(
                    "Request " + request.id()
                            + " rejected by executor: "
                            + exception.getMessage()
            );
        }
    }

    private void processRequest(TransferRequest request) {

        int taskNumber = printedTasks.incrementAndGet();

        if (taskNumber <= 10) {
            System.out.println(
                    "Request " + request.id()
                            + " is processed by "
                            + Thread.currentThread().getName()
            );
        }

        try {
            BankAccount from = accounts.get(request.fromAccount());
            BankAccount to = accounts.get(request.toAccount());

            if (from == null || to == null) {
                throw new IllegalArgumentException(
                        "Unknown bank account"
                );
            }

            transferService.transfer(
                    from,
                    to,
                    request.amount()
            );

            completedRequests.incrementAndGet();

        } catch (IllegalArgumentException exception) {

            failedRequests.incrementAndGet();

            System.out.println(
                    "Request " + request.id()
                            + " failed: "
                            + exception.getMessage()
            );

        } catch (RuntimeException exception) {

            failedRequests.incrementAndGet();

            System.out.println(
                    "Request " + request.id()
                            + " failed: "
                            + exception.getMessage()
            );
        }
    }

    public int getReceivedRequests() {
        return receivedRequests.get();
    }

    public int getSubmittedRequests() {
        return submittedRequests.get();
    }

    public int getCompletedRequests() {
        return completedRequests.get();
    }

    public int getFailedRequests() {
        return failedRequests.get();
    }
}
