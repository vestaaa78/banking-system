package banking;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance
)
{}
