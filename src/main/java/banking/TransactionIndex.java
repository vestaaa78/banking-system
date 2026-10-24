package banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionIndex {

    private final Map<AccountNumber, List<Transaction>> byAccount = new HashMap<>();
    public void add(Transaction tx) {
        if (tx == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        AccountNumber number = tx.account();

        List<Transaction> list = byAccount.computeIfAbsent(number, k -> new ArrayList<>());

        list.add(tx);
    }
    public void remove(Transaction tx) {
        if (tx == null) {
            return;
        }
        AccountNumber number = tx.account();
        List<Transaction> list = byAccount.get(number);
        if (list != null) {
            list.remove(tx);
            if (list.isEmpty()) {
                byAccount.remove(number);
            }
        }
    }

    public List<Transaction> findByAccount(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        List<Transaction> list = byAccount.get(number);

        if (list == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(list);
    }
}