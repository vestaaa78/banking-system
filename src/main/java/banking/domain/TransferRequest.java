package banking.domain;

public record TransferRequest(
        AccountNumber from,
        AccountNumber to,
        double amount
) {
}
