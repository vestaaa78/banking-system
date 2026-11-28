
package banking.concurrency;

public record TransferRequest(
        long id,
        String fromAccount,
        String toAccount,
        double amount
) {
}
