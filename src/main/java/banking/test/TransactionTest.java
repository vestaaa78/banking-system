package banking.test;

import banking.AccountNumber;
import banking.Transaction;
import banking.TransactionStatus;
import banking.TransactionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateTransactionWithCorrectFields() {
        AccountNumber accountNumber = new AccountNumber("1234567890");
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 5000;
        TransactionStatus status = TransactionStatus.SUCCESS;

        Transaction transaction = new Transaction(type, accountNumber, amount, status);

        assertEquals(type, transaction.type());
        assertEquals(accountNumber, transaction.account());
        assertEquals(amount, transaction.amount());
        assertEquals(status, transaction.status());
    }

    @Test
    void shouldCreateRejectedTransaction() {
        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("1111111111"),
                100000,
                TransactionStatus.REJECTED
        );

        assertEquals(TransactionType.WITHDRAWAL, transaction.type());
        assertEquals(TransactionStatus.REJECTED, transaction.status());
        assertEquals(100000, transaction.amount());
    }

    @Test
    void equalTransactionsShouldBeEqual() {
        AccountNumber number = new AccountNumber("1234567890");
        Transaction tx1 = new Transaction(TransactionType.DEPOSIT, number, 5000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(TransactionType.DEPOSIT, number, 5000, TransactionStatus.SUCCESS);

        assertEquals(tx1, tx2);
        assertEquals(tx1.hashCode(), tx2.hashCode());
    }

    @Test
    void transactionsWithDifferentAmountsAreNotEqual() {
        AccountNumber number = new AccountNumber("1234567890");
        Transaction tx1 = new Transaction(TransactionType.DEPOSIT, number, 5000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(TransactionType.DEPOSIT, number, 3000, TransactionStatus.SUCCESS);

        assertNotEquals(tx1, tx2);
    }
}
