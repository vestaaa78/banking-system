package banking.ports;

import banking.domain.BankAccount;

public interface AccountWriter {
    void save(BankAccount account);
}