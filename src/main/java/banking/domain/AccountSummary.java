package banking.domain;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance
)
{}
