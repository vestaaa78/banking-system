package banking;

public class SavingsAccount extends BankAccount {

    private final double minimumBalance;

    public SavingsAccount(AccountNumber number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        if (minimumBalance < 0) {
            throw new IllegalArgumentException("Минимальный остаток не может быть отрицательным");
        }
        this.minimumBalance = minimumBalance;
    }

    @Override
    protected double getAvailableAmount() {
        return Math.max(0, getBalance() - minimumBalance);
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