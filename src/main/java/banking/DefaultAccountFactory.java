package banking;

public class DefaultAccountFactory implements AccountFactory {

    private static final double DEFAULT_MINIMUM_BALANCE = 1000.0;
    private static final double DEFAULT_CREDIT_LIMIT = 50_000.0;

    @Override
    public BankAccount create(
            AccountType type,
            AccountNumber number,
            String owner,
            double balance
    ) {
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner cannot be null or blank");
        }

        return switch (type) {
            case DEBIT -> new DebitAccount(number, owner, balance);
            case SAVINGS -> new SavingsAccount(number, owner, balance, DEFAULT_MINIMUM_BALANCE);
            case CREDIT -> new CreditAccount(number, owner, balance, DEFAULT_CREDIT_LIMIT);
        };
    }
}