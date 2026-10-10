package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTest {

    @Test
    void shouldCopyDebitAccountsToBankAccountList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 1000));
        source.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 2000));

        List<BankAccount> target = new ArrayList<>();

        BankAccountUtils.copy(source, target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
        assertEquals("Alice", target.get(0).getOwner());
        assertEquals("Bob", target.get(1).getOwner());
    }

    @Test
    void shouldCopyDebitAccountsToObjectList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 3000));

        List<Object> target = new ArrayList<>();

        BankAccountUtils.copy(source, target);

        assertEquals(1, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
        assertEquals("Charlie", ((DebitAccount) target.get(0)).getOwner());
    }

    @Test
    void shouldCopyBankAccountsToBankAccountListAndPreserveOrder() {
        List<BankAccount> source = new ArrayList<>();
        BankAccount acc1 = new DebitAccount(new AccountNumber("4444444444"), "Dave", 4000);
        BankAccount acc2 = new SavingsAccount(new AccountNumber("5555555555"), "Eve", 5000, 1000);
        source.add(acc1);
        source.add(acc2);

        List<BankAccount> target = new ArrayList<>();

        BankAccountUtils.copy(source, target);

        assertEquals(2, target.size());

        assertSame(acc1, target.get(0), "Первый элемент должен остаться первым");
        assertSame(acc2, target.get(1), "Второй элемент должен остаться вторым");
    }

    @Test
    void shouldThrowExceptionIfSourceOrNull() {
        List<DebitAccount> source = new ArrayList<>();
        List<BankAccount> target = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> BankAccountUtils.copy(null, target));
        assertThrows(IllegalArgumentException.class, () -> BankAccountUtils.copy(source, null));
    }
}