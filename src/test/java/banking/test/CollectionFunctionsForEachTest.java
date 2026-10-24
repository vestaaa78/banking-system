package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionsForEachTest {

    @Test
    void forEachAppliesActionToEachElement() {
        List<AccountSummary> summaries = new ArrayList<>();
        summaries.add(new AccountSummary(new AccountNumber("1111111111"), "DebitAccount", 5000));
        summaries.add(new AccountSummary(new AccountNumber("2222222222"), "SavingsAccount", 10000));
        summaries.add(new AccountSummary(new AccountNumber("3333333333"), "CreditAccount", 3000));

        List<String> collected = new ArrayList<>();
        Consumer<AccountSummary> collector = summary -> collected.add(summary.number().value());

        CollectionFunctions.forEach(summaries, collector);

        assertEquals(3, collected.size());
        assertEquals("1111111111", collected.get(0));
        assertEquals("2222222222", collected.get(1));
        assertEquals("3333333333", collected.get(2));
    }

    @Test
    void forEachWithMethodReference() {
        List<AccountSummary> summaries = new ArrayList<>();
        summaries.add(new AccountSummary(new AccountNumber("1111111111"), "DebitAccount", 5000));
        summaries.add(new AccountSummary(new AccountNumber("2222222222"), "SavingsAccount", 10000));

        StringBuilder output = new StringBuilder();
        Consumer<AccountSummary> printer = output::append; // method reference

        CollectionFunctions.forEach(summaries, printer);

        String result = output.toString();
        assertTrue(result.contains("1111111111"));
        assertTrue(result.contains("2222222222"));
        assertTrue(result.contains("DebitAccount"));
        assertTrue(result.contains("SavingsAccount"));
    }

    @Test
    void forEachWithLambdaPrinter() {
        List<AccountSummary> summaries = new ArrayList<>();
        summaries.add(new AccountSummary(new AccountNumber("1111111111"), "DebitAccount", 5000));

        List<String> lines = new ArrayList<>();
        Consumer<AccountSummary> printer = summary ->
                lines.add(summary.number().value() + " : " + summary.balance());

        CollectionFunctions.forEach(summaries, printer);

        assertEquals(1, lines.size());
        assertEquals("1111111111 : 5000.0", lines.get(0));
    }

    @Test
    void forEachOnEmptyListDoesNothing() {
        List<AccountSummary> summaries = new ArrayList<>();
        int[] callCount = {0};
        Consumer<AccountSummary> counter = summary -> callCount[0]++;

        CollectionFunctions.forEach(summaries, counter);
        assertEquals(0, callCount[0], "Действие не должно быть вызвано для пустого списка");
    }

    @Test
    void forEachNullSourceThrowsException() {
        Consumer<AccountSummary> action = summary -> {};

        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.forEach(null, action));
    }

    @Test
    void forEachNullActionThrowsException() {
        List<AccountSummary> summaries = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.forEach(summaries, null));
    }

    @Test
    void forEachPreservesOrder() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        List<Integer> collected = new ArrayList<>();
        Consumer<Integer> collector = collected::add;

        CollectionFunctions.forEach(numbers, collector);

        assertEquals(1, collected.get(0));
        assertEquals(2, collected.get(1));
        assertEquals(3, collected.get(2));
    }

    @Test
    void forEachCanModifyExternalState() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));

        double[] totalBalance = {0};
        Consumer<BankAccount> sumBalance = account -> totalBalance[0] += account.getBalance();

        CollectionFunctions.forEach(accounts, sumBalance);

        assertEquals(15000.0, totalBalance[0]);
    }

    @Test
    void forEachWithComposedConsumer() {
        List<AccountSummary> summaries = new ArrayList<>();
        summaries.add(new AccountSummary(new AccountNumber("1111111111"), "DebitAccount", 5000));

        List<String> log1 = new ArrayList<>();
        List<String> log2 = new ArrayList<>();

        Consumer<AccountSummary> first = summary -> log1.add(summary.number().value());
        Consumer<AccountSummary> second = summary -> log2.add(summary.type());

        Consumer<AccountSummary> composed = first.andThen(second);

        CollectionFunctions.forEach(summaries, composed);

        assertEquals(1, log1.size());
        assertEquals(1, log2.size());
        assertEquals("1111111111", log1.get(0));
        assertEquals("DebitAccount", log2.get(0));
    }
}