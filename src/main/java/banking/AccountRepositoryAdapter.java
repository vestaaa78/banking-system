package banking;

import java.util.List;
import java.util.Optional;

public class AccountRepositoryAdapter implements AccountRepository {

    private final Repository<AccountNumber, BankAccount> delegate;

    public AccountRepositoryAdapter(Repository<AccountNumber, BankAccount> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate repository cannot be null");
        }
        this.delegate = delegate;
    }

    @Override
    public void save(BankAccount account) {
        delegate.save(account);
    }

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        return Optional.ofNullable(delegate.findById(number));
    }

    @Override
    public boolean existsById(AccountNumber number) {
        return delegate.existsById(number);
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        return delegate.deleteById(number);
    }

    @Override
    public List<BankAccount> findAll() {
        return delegate.findAll();
    }

    @Override
    public int size() {
        return delegate.size();
    }
}