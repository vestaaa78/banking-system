package banking;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionStatistics {

    public Map<AccountNumber, Integer> countByAccount(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Список транзакций не может быть null");
        }

        Map<AccountNumber, Integer> result = new HashMap<>();
        for (Transaction tx : transactions) {
            if (tx != null && tx.account() != null) {
                AccountNumber number = tx.account();
                int count = result.getOrDefault(number, 0);
                result.put(number, count + 1);
            }
        }
        return result;
    }

    public Map<AccountNumber, Double> totalAmountByAccount(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Список транзакций не может быть null");
        }

        Map<AccountNumber, Double> result = new HashMap<>();
        for (Transaction tx : transactions) {
            if (tx != null && tx.account() != null) {
                AccountNumber number = tx.account();
                double currentTotal = result.getOrDefault(number, 0.0);
                result.put(number, currentTotal + tx.amount());
            }
        }
        return result;
    }
}
