package banking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionJournal {

    private final List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public int size() {
        return transactions.size();
    }

    public void removeRejected() {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                iterator.remove();
            }
        }
    }

    public List<Transaction> findByAccount(AccountNumber number) {
        if (number == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : transactions) {
            if (tx.account().equals(number)) {
                result.add(tx);
            }
        }
        return result;
    }
}