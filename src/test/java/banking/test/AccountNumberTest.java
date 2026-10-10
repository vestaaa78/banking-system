package banking.test;

import banking.AccountNumber;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountNumberTest {

    @Test
    void validNumberIsCreated() {
        AccountNumber number = new AccountNumber("1234567890");

        assertEquals("1234567890", number.value());
    }

    @Test
    void validNumberWithLeadingZerosIsCreated() {
        AccountNumber number = new AccountNumber("0000000001");
        assertEquals("0000000001", number.value());
    }

    @Test
    void nullNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber(null);
        });
    }

    @Test
    void emptyStringIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("");
        });
    }

    @Test
    void shortNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("123456789");
        });
    }

    @Test
    void veryShortNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("123");
        });
    }

    @Test
    void longNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("12345678901");
        });
    }

    @Test
    void numberWithLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("123456789a");
        });
    }

    @Test
    void numberWithOnlyLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("abcdefghij");
        });
    }

    @Test
    void numberWithSpacesIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("123 567890");
        });
    }

    @Test
    void numberWithSpecialCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("123-567890");
        });
    }

    @Test
    void numberWithDecimalPointIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AccountNumber("12345.7890");
        });
    }
}