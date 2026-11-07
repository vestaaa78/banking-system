package banking;

public class TransferApplicationService {

    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public TransferApplicationService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService
    ) {
        if (accounts == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        if (transferService == null) {
            throw new IllegalArgumentException("TransferService cannot be null");
        }
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        if (fromNumber == null || toNumber == null) {
            throw new IllegalArgumentException("Account numbers cannot be null");
        }

        BankAccount from = accounts.findById(fromNumber);
        if (from == null) {
            throw new IllegalArgumentException("Source account not found: " + fromNumber.value());
        }

        BankAccount to = accounts.findById(toNumber);
        if (to == null) {
            throw new IllegalArgumentException("Target account not found: " + toNumber.value());
        }

        transferService.transfer(from, to, amount);
    }
}