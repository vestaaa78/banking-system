package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
class TransferApplicationServiceTest {

    private static final AccountNumber NUM_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUM_TO = new AccountNumber("2222222222");
    private static final AccountNumber NUM_MISSING = new AccountNumber("9999999999");

    private TransferApplicationService service;
    private Repository<AccountNumber, BankAccount> repository;
    private BlockedAccountRegistry blockedAccounts;
    private BankAccount from;
    private BankAccount to;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        blockedAccounts = new BlockedAccountRegistry();

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                new TransactionJournal()
        );

        service = new TransferApplicationService(repository, transferService);

        from = new DebitAccount(NUM_FROM, "Alice", 10000);
        to = new DebitAccount(NUM_TO, "Bob", 2000);

        repository.save(from);
        repository.save(to);
    }

    @Test
    void successfulTransfer() {
        service.transfer(NUM_FROM, NUM_TO, 3000);

        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }

    @Test
    void transferWithMissingSenderThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(NUM_MISSING, NUM_TO, 500)
        );

        assertTrue(exception.getMessage().contains("Source account not found"));

        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferWithMissingReceiverThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(NUM_FROM, NUM_MISSING, 500)
        );

        assertTrue(exception.getMessage().contains("Target account not found"));
        assertEquals(10000, from.getBalance());
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        blockedAccounts.block(NUM_FROM);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 500)
        );

        assertEquals("Source account is blocked", exception.getMessage());

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferWithInsufficientFundsThrowsException() {
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 50000)
        );

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferApplicationServiceDoesNotHaveReportMethods() {
        Method[] methods = TransferApplicationService.class.getDeclaredMethods();

        for (Method method : methods) {
            String name = method.getName().toLowerCase();

            assertFalse(name.contains("report"),
                    "TransferApplicationService не должен иметь методов для отчётов, но найден: " + method.getName());
            assertFalse(name.contains("export"),
                    "TransferApplicationService не должен иметь методов для экспорта, но найден: " + method.getName());
            assertFalse(name.contains("summary"),
                    "TransferApplicationService не должен иметь методов для summary, но найден: " + method.getName());
            assertFalse(name.contains("statistics"),
                    "TransferApplicationService не должен иметь методов для статистики, но найден: " + method.getName());
        }
    }

    @Test
    void transferApplicationServiceHasOnlyTransferMethod() {
        Method[] publicMethods = Arrays.stream(TransferApplicationService.class.getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isPublic(m.getModifiers()))
                .toArray(Method[]::new);

        assertEquals(1, publicMethods.length,
                "TransferApplicationService должен иметь ровно один публичный метод — transfer");
        assertEquals("transfer", publicMethods[0].getName());
    }

    @Test
    void existingTransferRulesPreserved_negativeAmount() {
        assertThrows(InvalidAmountException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, -100));
    }

    @Test
    void existingTransferRulesPreserved_zeroAmount() {
        assertThrows(InvalidAmountException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 0));
    }

    @Test
    void existingTransferRulesPreserved_transferLimit() {
        assertThrows(TransferLimitExceededException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 100000));
    }

    @Test
    void existingTransferRulesPreserved_sameAccount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(NUM_FROM, NUM_FROM, 500));
    }

    @Test
    void transferIsAtomic_noPartialExecution() {
        double fromBalanceBefore = from.getBalance();
        double toBalanceBefore = to.getBalance();

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 50000));

        assertEquals(fromBalanceBefore, from.getBalance(),
                "Баланс отправителя не должен измениться при неудачном переводе");
        assertEquals(toBalanceBefore, to.getBalance(),
                "Баланс получателя не должен измениться при неудачном переводе");
    }
}