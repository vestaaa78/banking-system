package banking.test;

import banking.AccountNumber;
import banking.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void shouldCreatePairWithDifferentTypes() {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        AccountNumber accNum = new AccountNumber("1234567890");
        Pair<AccountNumber, String> owner = new Pair<>(accNum, "Ivan");

        assertEquals("Ivan", age.key());
        assertEquals(20, age.value());

        assertEquals(accNum, owner.key());
        assertEquals("Ivan", owner.value());
    }

    @Test
    void pairShouldHaveCorrectEqualsAndHashCode() {
        Pair<String, Integer> p1 = new Pair<>("A", 1);
        Pair<String, Integer> p2 = new Pair<>("A", 1);
        Pair<String, Integer> p3 = new Pair<>("A", 2);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());

        assertNotEquals(p1, p3);
    }
}