package banking.ports;

import banking.domain.BankAccount;

import java.util.List;

public interface AccountCatalog {
    List<BankAccount> findAll();

    int size();
}