package banking;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    void save(BankAccount account);
    Optional<BankAccount> findById(AccountNumber number);
    boolean existsById(AccountNumber number);
    boolean deleteById(AccountNumber number);
    List<BankAccount> findAll();
    int size();
}