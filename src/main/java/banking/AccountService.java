package banking;

import java.util.Optional;

public class AccountService {

    private final AccountRepository accounts;  // ← ЗАВИСИМОСТЬ ОТ АБСТРАКЦИИ

    public AccountService(AccountRepository accounts) {
        if (accounts == null) {
            throw new IllegalArgumentException("AccountRepository cannot be null");
        }
        this.accounts = accounts;
    }

    public void open(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        accounts.save(account);
    }

    public Optional<BankAccount> find(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return accounts.findById(number);
    }

    public boolean close(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return accounts.deleteById(number);
    }

    public int count() {
        return accounts.size();
    }
}
