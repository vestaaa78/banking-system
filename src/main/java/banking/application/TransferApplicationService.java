package banking.application;

import banking.domain.*;
import banking.ports.AccountRepository;

public class TransferApplicationService {

    private final AccountRepository accounts;
    private final TransferService transferService;
    private final TransferEventPublisher eventPublisher;

    public TransferApplicationService(
            AccountRepository accounts,
            TransferService transferService
    ) {
        this(accounts, transferService, null);
    }

    public TransferApplicationService(
            AccountRepository accounts,
            TransferService transferService,
            TransferEventPublisher eventPublisher
    ) {
        if (accounts == null) {
            throw new IllegalArgumentException("AccountRepository cannot be null");
        }
        if (transferService == null) {
            throw new IllegalArgumentException("TransferService cannot be null");
        }
        this.accounts = accounts;
        this.transferService = transferService;
        this.eventPublisher = eventPublisher;
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        if (fromNumber == null || toNumber == null) {
            throw new IllegalArgumentException("Account numbers cannot be null");
        }

        BankAccount from = accounts.findById(fromNumber)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + fromNumber.value()));

        BankAccount to = accounts.findById(toNumber)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found: " + toNumber.value()));

        transferService.transfer(from, to, amount);

        if (eventPublisher != null) {
            Transaction transaction = new Transaction(
                    System.nanoTime(),
                    TransactionType.TRANSFER,
                    fromNumber,
                    amount,
                    TransactionStatus.SUCCESS
            );
            eventPublisher.publish(transaction);
        }
    }
}