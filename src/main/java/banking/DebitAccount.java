package banking;

public class DebitAccount extends BankAccount {

    public DebitAccount(AccountNumber number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
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