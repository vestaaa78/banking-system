package banking;

public class TransferService {

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            return false;
        }
        if (from == to) {
            return false;
        }

        double commission = commissionPolicy.calculate(amount);
        double totalDebit = amount + commission;

        try {
            from.withdraw(totalDebit);
            to.deposit(amount);
            notificationService.notify("Transfer " + amount + " completed");
            return true;
        } catch (InsufficientFundsException | InvalidAmountException e) {
            return false;
        }
    }
}