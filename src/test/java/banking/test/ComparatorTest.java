//package banking.test;
//
//import banking.AccountNumber;
//import banking.BankAccount;
//import banking.DebitAccount;
//import org.junit.jupiter.api.Test;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//class ComparatorTest {
//
//    @Test
//    void shouldSortAccountsByBalanceAscending() {
//        List<BankAccount> accounts = new ArrayList<>();
//        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000.0));
//        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 1000.0));
//        accounts.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 10000.0));
//
//        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
//        accounts.sort(byBalance);
//
//        assertEquals(1000.0, accounts.get(0).getBalance(), "Первый элемент должен иметь минимальный баланс");
//        assertEquals(5000.0, accounts.get(1).getBalance());
//        assertEquals(10000.0, accounts.get(2).getBalance(), "Последний элемент должен иметь максимальный баланс");
//    }
//
//    @Test
//    void shouldSortAccountsByBalanceDescending() {
//        List<BankAccount> accounts = new ArrayList<>();
//        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000.0));
//        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 1000.0));
//
//        Comparator<BankAccount> byBalanceDesc = Comparator.comparingDouble(BankAccount::getBalance).reversed();
//        accounts.sort(byBalanceDesc);
//
//        assertEquals(5000.0, accounts.get(0).getBalance());
//        assertEquals(1000.0, accounts.get(1).getBalance());
//    }
//}