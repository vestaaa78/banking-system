package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountFactoryTest {

    private AccountFactory factory;
    private AccountNumber number;

    @BeforeEach
    void setUp() {
        factory = new DefaultAccountFactory();
        number = new AccountNumber("1111111111");
    }

    @Test
    void createDebitAccount() {
        BankAccount account = factory.create(AccountType.DEBIT, number, "Alice", 5000);

        assertNotNull(account);
        assertInstanceOf(DebitAccount.class, account);
        assertEquals(number, account.getId());
        assertEquals("Alice", account.getOwner());
        assertEquals(5000, account.getBalance());
    }

    @Test
    void createSavingsAccount() {
        BankAccount account = factory.create(AccountType.SAVINGS, number, "Bob", 10000);

        assertNotNull(account);
        assertInstanceOf(SavingsAccount.class, account);
        assertEquals(number, account.getId());
        assertEquals("Bob", account.getOwner());
        assertEquals(10000, account.getBalance());

        // Проверяем дефолтный минимальный остаток
        SavingsAccount savings = (SavingsAccount) account;
        assertEquals(1000, savings.getMinimumBalance(),
                "SavingsAccount должен иметь дефолтный минимальный остаток 1000");
    }

    @Test
    void createCreditAccount() {
        BankAccount account = factory.create(AccountType.CREDIT, number, "Charlie", 3000);

        assertNotNull(account);
        assertInstanceOf(CreditAccount.class, account);
        assertEquals(number, account.getId());
        assertEquals("Charlie", account.getOwner());
        assertEquals(3000, account.getBalance());

        CreditAccount credit = (CreditAccount) account;
        assertEquals(50_000, credit.getCreditLimit(),
                "CreditAccount должен иметь дефолтный кредитный лимит 50000");
    }

    @Test
    void createWithNullTypeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(null, number, "Alice", 5000));
    }

    @Test
    void createWithNullNumberThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(AccountType.DEBIT, null, "Alice", 5000));
    }

    @Test
    void createWithNullOwnerThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(AccountType.DEBIT, number, null, 5000));
    }

    @Test
    void createWithBlankOwnerThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(AccountType.DEBIT, number, "   ", 5000));
    }

    @Test
    void clientDoesNotNeedToKnowAboutConcreteClasses() {
        for (AccountType type : AccountType.values()) {
            BankAccount account = factory.create(type, number, "Test", 1000);
            assertNotNull(account.getId());
            assertNotNull(account.getOwner());
            assertTrue(account.getBalance() >= 0);
        }
    }

    @Test
    void factoryCanBeReplacedWithCustomImplementation() {
        AccountFactory customFactory = new AccountFactory() {
            @Override
            public BankAccount create(AccountType type, AccountNumber num, String owner, double balance) {
                return switch (type) {
                    case DEBIT -> new DebitAccount(num, owner, balance);
                    case SAVINGS -> new SavingsAccount(num, owner, balance, 5000);
                    case CREDIT -> new CreditAccount(num, owner, balance, 100_000);
                };
            }
        };

        BankAccount savings = customFactory.create(AccountType.SAVINGS, number, "Alice", 10000);
        BankAccount credit = customFactory.create(AccountType.CREDIT, number, "Bob", 5000);

        assertEquals(5000, ((SavingsAccount) savings).getMinimumBalance());
        assertEquals(100_000, ((CreditAccount) credit).getCreditLimit());
    }

    @Test
    void allAccountTypesAreHandled() {
        for (AccountType type : AccountType.values()) {
            BankAccount account = factory.create(type, number, "Test", 1000);
            assertNotNull(account, "Фабрика должна создать счёт для типа " + type);
        }
    }

    @Test
    void accountServiceCanUseFactory() {
        AccountRepository repository = new InMemoryAccountRepository();
        AccountService service = new AccountService(repository);

        BankAccount debit = factory.create(AccountType.DEBIT, new AccountNumber("1111111111"), "Alice", 5000);
        BankAccount savings = factory.create(AccountType.SAVINGS, new AccountNumber("2222222222"), "Bob", 10000);
        BankAccount credit = factory.create(AccountType.CREDIT, new AccountNumber("3333333333"), "Charlie", 3000);

        service.open(debit);
        service.open(savings);
        service.open(credit);

        assertEquals(3, service.count());
        assertTrue(service.find(new AccountNumber("1111111111")).isPresent());
        assertTrue(service.find(new AccountNumber("2222222222")).isPresent());
        assertTrue(service.find(new AccountNumber("3333333333")).isPresent());
    }
}