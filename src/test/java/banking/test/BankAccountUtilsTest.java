package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountUtilsTest {

    @Test
    void shouldCalculateTotalBalanceOfDebitAccounts() {
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "A", 1000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "B", 2000));

        double total = BankAccountUtils.totalBalance(accounts);

        assertEquals(3000.0, total);
    }

    @Test
    void shouldCalculateTotalBalanceOfMixedAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "A", 1000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "B", 5000, 1000));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "C", 500, 2000));

        double total = BankAccountUtils.totalBalance(accounts);

        assertEquals(6500.0, total);
    }

    @Test
    void shouldReturnZeroForEmptyList() {
        List<CreditAccount> accounts = new ArrayList<>();

        assertEquals(0.0, BankAccountUtils.totalBalance(accounts));
    }
}