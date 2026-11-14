package banking.adapters;

import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.ports.AccountRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountNumber, BankAccount> data = new HashMap<>();

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return Optional.ofNullable(data.get(number));
    }

    @Override
    public void save(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        data.put(account.getId(), account);
    }

    @Override
    public boolean existsById(AccountNumber number) {
        if (number == null) {
            return false;
        }
        return data.containsKey(number);
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        if (number == null) {
            return false;
        }
        return data.remove(number) != null;
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public int size() {
        return data.size();
    }
}