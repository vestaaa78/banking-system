package banking;

public class CreditAccount extends BankAccount {
    private final double creditLimit;

    public CreditAccount(AccountNumber number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        this.creditLimit = creditLimit;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance() + creditLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available: " + getAvailableAmount() + ", requested: " + amount
            );
        }
        decreaseBalance(amount);
    }

}

