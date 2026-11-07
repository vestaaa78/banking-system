package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeAccountRepository implements AccountRepository {

    private final List<BankAccount> accounts = new ArrayList<>();
    private boolean shouldFail = false;
    private String failureMessage = "Simulated failure";

    public void enableFailureMode(String message) {
        this.shouldFail = true;
        this.failureMessage = message;
    }

    public void disableFailureMode() {
        this.shouldFail = false;
    }

    private void checkFailure() {
        if (shouldFail) {
            throw new RuntimeException(failureMessage);
        }
    }

    @Override
    public void save(BankAccount account) {
        checkFailure();
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        accounts.removeIf(a -> a.getId().equals(account.getId()));
        accounts.add(account);
    }

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        checkFailure();
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        return accounts.stream()
                .filter(a -> a.getId().equals(number))
                .findFirst();
    }

    @Override
    public boolean existsById(AccountNumber number) {
        checkFailure();
        return findById(number).isPresent();
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        checkFailure();
        return accounts.removeIf(a -> a.getId().equals(number));
    }

    @Override
    public List<BankAccount> findAll() {
        checkFailure();
        return new ArrayList<>(accounts);
    }

    @Override
    public int size() {
        checkFailure();
        return accounts.size();
    }
}