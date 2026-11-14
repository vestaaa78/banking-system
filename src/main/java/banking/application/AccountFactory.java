package banking.application;

import banking.domain.AccountNumber;
import banking.domain.AccountType;
import banking.domain.BankAccount;

public interface AccountFactory {
    BankAccount create(
            AccountType type,
            AccountNumber number,
            String owner,
            double initialBalance
    );
}