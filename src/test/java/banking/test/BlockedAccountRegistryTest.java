package banking.test;

import banking.AccountNumber;
import banking.BlockedAccountRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockedAccountRegistryTest {

    private BlockedAccountRegistry registry;
    private AccountNumber number;

    @BeforeEach
    void setUp() {
        registry = new BlockedAccountRegistry();
        number = new AccountNumber("0000000001");
    }

    @Test
    void isBlockedReturnsTrueAfterBlocking() {
        registry.block(number);

        assertTrue(registry.isBlocked(number), "Счёт должен быть заблокирован после вызова block()");
    }

    @Test
    void blockingSameAccountTwiceDoesNotCreateDuplicate() {
        registry.block(number);
        registry.block(number);

        assertEquals(1, registry.size(), "Повторная блокировка не должна увеличивать размер множества");
        assertTrue(registry.isBlocked(number));
    }

    @Test
    void unblockRemovesNumber() {
        registry.block(number);

        registry.unblock(number);

        assertFalse(registry.isBlocked(number), "После unblock() счёт не должен быть заблокирован");
        assertEquals(0, registry.size(), "Размер реестра должен стать 0");
    }

    @Test
    void isBlockedReturnsFalseForUnknownNumber() {
        AccountNumber unknownNumber = new AccountNumber("9999999999");

        assertFalse(registry.isBlocked(unknownNumber), "Неизвестный счёт не должен быть заблокирован");
    }
}