package banking.ports;

import banking.domain.AccountNumber;

public interface AccountDeleter {
    boolean deleteById(AccountNumber number);
}