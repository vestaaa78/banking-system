package banking;

public class TransferService {

    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    private long transactionCounter = 0;

    public TransferService(
            CommissionPolicy commissionPolicy,
            NotificationService notificationService,
            BlockedAccountRegistry blockedAccounts,
            TransactionJournal journal
    ) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded: " + amount);
        }

        double commission = commissionPolicy.calculate(amount);
        double totalDebit = amount + commission;

        from.withdraw(totalDebit);

        to.deposit(amount);

        transactionCounter++;
        journal.add(new Transaction(
                transactionCounter,
                TransactionType.TRANSFER,
                from.getNumber(),
                amount,
                TransactionStatus.SUCCESS
        ));

        notificationService.notify("Transfer " + amount + " completed");
    }
}