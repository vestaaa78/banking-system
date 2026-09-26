package banking;

public class CreditAccount extends BankAccount {

    private final double creditLimit;

    public CreditAccount(AccountNumber number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        if (creditLimit <= 0) {
            throw new IllegalArgumentException("Кредитный лимит должен быть положительным");
        }
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (!isValidAmount(amount)) {
            return false;
        }

        if (getBalance() - amount >= -creditLimit) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{\n" +
                " number='" + getNumber() + "',\n" +
                " owner='" + getOwner() + "',\n" +
                " balance=" + getBalance() + ",\n" +
                " creditLimit=" + creditLimit + "\n" +
                "}";
    }
}

