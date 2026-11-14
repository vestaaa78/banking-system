package banking.ports;

import banking.domain.AccountNumber;
import banking.domain.BankAccount;

import java.util.Optional;

public interface AccountReader {
    Optional<BankAccount> findById(AccountNumber number);

    boolean existsById(AccountNumber number);
}