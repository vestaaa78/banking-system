package banking;

public class SavingsAccount extends BankAccount {

    private final double minimumBalance;

    public SavingsAccount(String number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        if (minimumBalance < 0) {
            throw new IllegalArgumentException("Минимальный остаток не может быть отрицательным");
        }
        this.minimumBalance = minimumBalance;
    }

    @Override
    public boolean withdraw(double amount) {
        if (!isValidAmount(amount)) {
            return false;
        }
        if (getBalance() - amount >= minimumBalance) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
}