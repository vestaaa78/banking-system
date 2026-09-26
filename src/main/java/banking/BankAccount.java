package banking;
import java.util.Objects;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{\n" +
                " number='" + number + "',\n" +
                " owner='" + owner + "',\n" +
                " balance=" + balance + "\n" +
                "}";
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
}