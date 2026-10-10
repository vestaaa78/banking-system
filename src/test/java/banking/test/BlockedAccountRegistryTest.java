package banking.test;

import banking.AccountNumber;
import banking.BlockedAccountRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockedAccountRegistryTest {

    private BlockedAccountRegistry registry;
    private AccountNumber number1;
    private AccountNumber number2;

    @BeforeEach
    void setUp() {
        registry = new BlockedAccountRegistry();
        number1 = new AccountNumber("1111111111");
        number2 = new AccountNumber("2222222222");
    }

    @Test
    void shouldBlockAccount() {
        registry.block(number1);

        assertTrue(registry.isBlocked(number1));
        assertEquals(1, registry.size());
    }

    @Test
    void shouldUnblockAccount() {
        registry.block(number1);

        registry.unblock(number1);

        assertFalse(registry.isBlocked(number1));
        assertEquals(0, registry.size());
    }

    @Test
    void shouldNotAddDuplicateBlockedAccounts() {
        registry.block(number1);
        registry.block(number1);

        assertEquals(1, registry.size());
        assertTrue(registry.isBlocked(number1));
    }

    @Test
    void shouldHandleMultipleAccounts() {
        registry.block(number1);
        registry.block(number2);

        assertEquals(2, registry.size());
        assertTrue(registry.isBlocked(number1));
        assertTrue(registry.isBlocked(number2));
    }

    @Test
    void shouldReturnFalseForNonBlockedAccount() {
        assertFalse(registry.isBlocked(number1));
    }

    @Test
    void shouldThrowExceptionOnNull() {
        assertThrows(IllegalArgumentException.class, () -> registry.block(null));
        assertThrows(IllegalArgumentException.class, () -> registry.unblock(null));
    }
}
