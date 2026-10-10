package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountUtilsSuperTest {

    @Test
    void shouldAddDebitAccountsToDebitList() {
        List<DebitAccount> list = new ArrayList<>();

        BankAccountUtils.addDemoDebitAccounts(list);

        assertEquals(2, list.size());
        assertEquals("Demo User 1", list.get(0).getOwner());
        assertEquals("Demo User 2", list.get(1).getOwner());
    }

    @Test
    void shouldAddDebitAccountsToBankAccountList() {
        List<BankAccount> list = new ArrayList<>();

        BankAccountUtils.addDemoDebitAccounts(list);

        assertEquals(2, list.size());
        BankAccount account = list.get(0);
        assertEquals("Demo User 1", account.getOwner());
    }

    @Test
    void shouldAddDebitAccountsToObjectList() {
        List<Object> list = new ArrayList<>();

        BankAccountUtils.addDemoDebitAccounts(list);

        assertEquals(2, list.size());
        Object value = list.get(0);
        assertTrue(value instanceof DebitAccount);
    }

    @Test
    void shouldPreserveExistingElements() {
        List<BankAccount> list = new ArrayList<>();
        list.add(new SavingsAccount(new AccountNumber("3333333333"), "Existing", 5000, 1000));

        BankAccountUtils.addDemoDebitAccounts(list);

        assertEquals(3, list.size());
        assertTrue(list.get(0) instanceof SavingsAccount);
        assertTrue(list.get(1) instanceof DebitAccount);
        assertTrue(list.get(2) instanceof DebitAccount);
    }
}