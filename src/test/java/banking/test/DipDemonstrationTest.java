package banking.test;


import banking.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DipDemonstrationTest {

    private static final AccountNumber NUM_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUM_TO = new AccountNumber("2222222222");

    @Test
    void transferApplicationService_worksWithInMemoryRepository() {
        AccountRepository repository = new InMemoryAccountRepository();
        repository.save(new DebitAccount(NUM_FROM, "Alice", 10000));
        repository.save(new DebitAccount(NUM_TO, "Bob", 5000));

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        TransferApplicationService service = new TransferApplicationService(repository, transferService);

        service.transfer(NUM_FROM, NUM_TO, 3000);

        assertEquals(7000, repository.findById(NUM_FROM).get().getBalance());
        assertEquals(8000, repository.findById(NUM_TO).get().getBalance());
    }

    @Test
    void transferApplicationService_worksWithFakeRepository() {
        FakeAccountRepository fakeRepository = new FakeAccountRepository();
        fakeRepository.save(new DebitAccount(NUM_FROM, "Alice", 10000));
        fakeRepository.save(new DebitAccount(NUM_TO, "Bob", 5000));

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        TransferApplicationService service = new TransferApplicationService(fakeRepository, transferService);

        service.transfer(NUM_FROM, NUM_TO, 3000);

        assertEquals(7000, fakeRepository.findById(NUM_FROM).get().getBalance());
        assertEquals(8000, fakeRepository.findById(NUM_TO).get().getBalance());
    }

    @Test
    void transferApplicationService_handlesRepositoryFailure() {
        FakeAccountRepository fakeRepository = new FakeAccountRepository();
        fakeRepository.save(new DebitAccount(NUM_FROM, "Alice", 10000));
        fakeRepository.save(new DebitAccount(NUM_TO, "Bob", 5000));

        fakeRepository.enableFailureMode("Database connection lost");

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        TransferApplicationService service = new TransferApplicationService(fakeRepository, transferService);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.transfer(NUM_FROM, NUM_TO, 3000)
        );

        assertEquals("Database connection lost", exception.getMessage());
    }

    @Test
    void accountService_worksWithDifferentRepositories() {
        AccountRepository repo1 = new InMemoryAccountRepository();
        AccountService service1 = new AccountService(repo1);

        service1.open(new DebitAccount(NUM_FROM, "Alice", 5000));
        assertEquals(1, service1.count());

        FakeAccountRepository repo2 = new FakeAccountRepository();
        AccountService service2 = new AccountService(repo2);

        service2.open(new DebitAccount(NUM_FROM, "Bob", 3000));
        assertEquals(1, service2.count());
    }

    @Test
    void canCreateNewRepositoryImplementationWithoutChangingServices() {
        AccountRepository loggingRepository = new AccountRepository() {
            private final AccountRepository delegate = new InMemoryAccountRepository();

            @Override
            public void save(BankAccount account) {
                System.out.println("[LOG] Saving account: " + account.getId());
                delegate.save(account);
            }

            @Override
            public java.util.Optional<BankAccount> findById(AccountNumber number) {
                System.out.println("[LOG] Finding account: " + number);
                return delegate.findById(number);
            }

            @Override
            public boolean existsById(AccountNumber number) {
                return delegate.existsById(number);
            }

            @Override
            public boolean deleteById(AccountNumber number) {
                System.out.println("[LOG] Deleting account: " + number);
                return delegate.deleteById(number);
            }

            @Override
            public java.util.List<BankAccount> findAll() {
                return delegate.findAll();
            }

            @Override
            public int size() {
                return delegate.size();
            }
        };

        AccountService service = new AccountService(loggingRepository);
        service.open(new DebitAccount(NUM_FROM, "Alice", 5000));

        assertEquals(1, service.count());
    }
}