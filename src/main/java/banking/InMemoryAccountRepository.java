package banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountNumber, BankAccount> storage = new HashMap<>();

    @Override
    public void save(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        storage.put(account.getId(), account);
    }

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return Optional.ofNullable(storage.get(number));
    }

    @Override
    public boolean existsById(AccountNumber number) {
        if (number == null) {
            return false;
        }
        return storage.containsKey(number);
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        if (number == null) {
            return false;
        }
        return storage.remove(number) != null;
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public int size() {
        return storage.size();
    }
}