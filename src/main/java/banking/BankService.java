package banking;

import java.util.List;
import java.util.function.Predicate;

public class BankService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public BankService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService
    ) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        accounts.save(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return accounts.findById(number);
    }

    public boolean closeAccount(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return accounts.deleteById(number);
    }

    public int accountCount() {
        return accounts.size();
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
    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }

        List<BankAccount> allAccounts = accounts.findAll();
        return CollectionFunctions.filter(allAccounts, condition);
    }

    private List<BankAccount> getAllAccounts() {
        throw new UnsupportedOperationException(
                "Repository должен иметь метод findAll() для поддержки findAccounts()"
        );
    }

}