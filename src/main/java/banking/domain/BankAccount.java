package banking.domain;

import java.util.Objects;

public abstract class BankAccount implements Identifiable<AccountNumber> {

    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        synchronized (this) {
            increaseBalance(amount);
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        synchronized (this) {
            if (amount > getAvailableAmount()) {
                throw new InsufficientFundsException(
                        "Insufficient funds. Available: " + getAvailableAmount() + ", requested: " + amount
                );
            }
            decreaseBalance(amount);
        }
    }

    public AccountNumber getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    protected void increaseBalance(double amount) {
        balance += amount;
    }

    protected void decreaseBalance(double amount) {
        balance -= amount;
    }

    protected double getAvailableAmount() {
        return balance;
    }

    @Override
    public AccountNumber getId() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof BankAccount)) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(this.number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                " number=" + number.value() +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }
}