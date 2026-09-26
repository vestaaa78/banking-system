package banking;

public abstract class BankAccount {

    private final String number;
    private final String owner;
    private double balance;

    protected BankAccount(String number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public abstract boolean withdraw(double amount);

    public double getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    protected boolean isValidAmount(double amount) {
        return amount > 0;
    }
}