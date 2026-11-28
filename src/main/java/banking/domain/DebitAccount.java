package banking.domain;

public class DebitAccount extends BankAccount {

    public DebitAccount(AccountNumber number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
    }

    @Override
    public synchronized void withdraw(double amount) {

        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        decreaseBalance(amount);
    }

}