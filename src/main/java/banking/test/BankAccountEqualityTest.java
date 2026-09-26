package banking.test;

import banking.BankAccount;
import banking.CreditAccount;
import banking.DebitAccount;
import banking.SavingsAccount;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BankAccountEqualityTest {
    @Test
    void accountsWithSameNumberAreEqualRegardlessOfTypeOrBalance() {
        BankAccount debit = new DebitAccount("ACC-001", "Ivan", 10000.0);
        BankAccount savings = new SavingsAccount("ACC-001", "Ivan", 5000.0, 1000.0);

        BankAccount credit = new CreditAccount("ACC-001", "Ivan", 0.0, 5000.0);

        BankAccount differentNumber = new DebitAccount("ACC-002", "Ivan", 10000.0);

        assertEquals(debit, debit);

        assertEquals(debit, savings);
        assertEquals(savings, debit);

        assertEquals(savings, credit);
        assertEquals(debit, credit);

        assertEquals(debit.hashCode(), savings.hashCode());
        assertEquals(savings.hashCode(), credit.hashCode());
        assertEquals(debit.hashCode(), credit.hashCode());

        assertNotEquals(debit, differentNumber);
    }
}
