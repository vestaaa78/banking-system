package banking.application;

import banking.domain.*;
import banking.ports.NotificationService;

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

    private void executeTransfer(
            BankAccount from,
            BankAccount to,
            double totalDebit,
            double amount
    ) {
        from.withdraw(totalDebit);
        to.deposit(amount);

        synchronized (journal) {
            transactionCounter++;

            journal.add(new Transaction(
                    transactionCounter,
                    TransactionType.TRANSFER,
                    from.getNumber(),
                    to.getNumber(),
                    amount,
                    TransactionStatus.SUCCESS
            ));
        }
    }


    public void transfer(BankAccount from, BankAccount to, double amount) {
        synchronized (journal) {
            transactionCounter++;

            journal.add(new Transaction(
                    transactionCounter,
                    TransactionType.TRANSFER,
                    from.getNumber(),
                    to.getNumber(),
                    amount,
                    TransactionStatus.SUCCESS
            ));
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException(
                    "Transfer limit exceeded: " + amount
            );
        }

        double commission = commissionPolicy.calculate(amount);
        double totalDebit = amount + commission;

        BankAccount firstLock;
        BankAccount secondLock;

        if (from.getNumber().value().compareTo(
                to.getNumber().value()
        ) < 0) {

            firstLock = from;
            secondLock = to;

        } else {

            firstLock = to;
            secondLock = from;
        }
        synchronized (firstLock) {
            synchronized (secondLock) {

                executeTransfer(
                        from,
                        to,
                        totalDebit,
                        amount
                );
            }
        }

        notificationService.notify(
                "Transfer " + amount + " completed"
        );
    }
}