package banking;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
        List<BankAccount> result = new ArrayList<>(accounts.findAll());
        result.sort(comparator);
        return result;
    }
    public List<BankAccount> findRichAccounts(double minBalance) {
        return accounts.findAll().stream()
                .filter(account -> account.getBalance() >= minBalance)
                .toList();
    }
    public List<BankAccount> findDebitAccounts() {
        return accounts.findAll().stream()
                .filter(DebitAccount.class::isInstance)
                .toList();
    }
    public List<BankAccount> findAccountsByOwner(String owner) {
        return accounts.findAll().stream()
                .filter(account -> account.getOwner().equals(owner))
                .toList();
    }
    public List<AccountSummary> getAccountSummaries() {
        return accounts.findAll().stream()
                .map(this::toSummary)
                .toList();
    }
    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );
    }
}