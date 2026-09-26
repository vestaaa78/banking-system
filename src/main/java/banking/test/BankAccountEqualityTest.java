package banking.test;

import banking.BankAccount;
import banking.CreditAccount;
import banking.DebitAccount;
import banking.SavingsAccount;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BankAccountEqualityTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount debit = new DebitAccount("ACC-001", "Ivan", 10000.0);
        BankAccount savings = new SavingsAccount("ACC-001", "Petr", 5000.0, 1000.0);
        BankAccount credit = new CreditAccount("ACC-001", "Alex", 0.0, 5000.0);

        // Act & Assert: Они должны быть равны, так как номер счёта является бизнес-ключом
        assertEquals(debit, savings, "Debit и Savings с одинаковым номером должны быть равны");
        assertEquals(savings, credit, "Savings и Credit с одинаковым номером должны быть равны");
        assertEquals(debit, credit, "Debit и Credit с одинаковым номером должны быть равны");
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount account1 = new DebitAccount("ACC-001", "Ivan", 10000.0);
        BankAccount account2 = new DebitAccount("ACC-002", "Ivan", 10000.0);
        BankAccount account3 = new SavingsAccount("ACC-003", "Ivan", 10000.0, 1000.0);

        assertNotEquals(account1, account2);
        assertNotEquals(account1, account3);
    }

    @Test
    void accountEqualsItself() {
        BankAccount account = new DebitAccount("ACC-001", "Ivan", 10000.0);

        assertEquals(account, account);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount account = new DebitAccount("ACC-001", "Ivan", 10000.0);

        assertNotEquals(account, null);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount debit = new DebitAccount("ACC-001", "Ivan", 10000.0);
        BankAccount savings = new SavingsAccount("ACC-001", "Petr", 5000.0, 1000.0);

        assertEquals(debit.hashCode(), savings.hashCode(),
                "Равные счета должны иметь одинаковый hashCode");
    }
}
