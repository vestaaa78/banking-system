package banking;

import java.util.ArrayList;
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
}