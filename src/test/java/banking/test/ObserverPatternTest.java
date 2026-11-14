package banking.test;

import banking.adapters.*;
import banking.application.*;
import banking.domain.*;
import banking.ports.AccountRepository;
import banking.ports.TransferListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObserverPatternTest {

    private static final AccountNumber NUM_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUM_TO = new AccountNumber("2222222222");

    private AccountRepository repository;
    private TransferService transferService;
    private TransferEventPublisher publisher;
    private TransferApplicationService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationAdapter(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );
        publisher = new TransferEventPublisher();
        service = new TransferApplicationService(repository, transferService, publisher);

        repository.save(new DebitAccount(NUM_FROM, "Alice", 10000));
        repository.save(new DebitAccount(NUM_TO, "Bob", 5000));
    }

    @Test
    void singleListenerReceivesEvent() {
        LoggingTransferListener listener = new LoggingTransferListener();
        publisher.subscribe(listener);

        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(1, listener.size());
        assertTrue(listener.getLog().get(0).contains("Transfer"));
    }

    @Test
    void multipleListenersAllReceiveEvent() {
        LoggingTransferListener logging = new LoggingTransferListener();
        StatisticsTransferListener statistics = new StatisticsTransferListener();
        AlertTransferListener alert = new AlertTransferListener(5000);

        publisher.subscribe(logging);
        publisher.subscribe(statistics);
        publisher.subscribe(alert);

        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(1, logging.size());
        assertEquals(1, statistics.getTotalTransfers());
        assertEquals(0, alert.getAlertCount());
    }

    @Test
    void listenerReceivesMultipleEvents() {
        StatisticsTransferListener statistics = new StatisticsTransferListener();
        publisher.subscribe(statistics);

        service.transfer(NUM_FROM, NUM_TO, 1000);
        service.transfer(NUM_FROM, NUM_TO, 2000);
        service.transfer(NUM_FROM, NUM_TO, 3000);

        assertEquals(3, statistics.getTotalTransfers());
        assertEquals(6000, statistics.getTotalAmount());
        assertEquals(3000, statistics.getMaxAmount());
        assertEquals(2000, statistics.getAverageAmount());
    }

    @Test
    void unsubscribeStopsNotifications() {
        LoggingTransferListener listener = new LoggingTransferListener();
        publisher.subscribe(listener);

        service.transfer(NUM_FROM, NUM_TO, 1000);
        assertEquals(1, listener.size());

        publisher.unsubscribe(listener);
        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(1, listener.size());
    }

    @Test
    void duplicateSubscriptionIgnored() {
        LoggingTransferListener listener = new LoggingTransferListener();

        publisher.subscribe(listener);
        publisher.subscribe(listener);

        assertEquals(1, publisher.getSubscriberCount());

        service.transfer(NUM_FROM, NUM_TO, 1000);
        assertEquals(1, listener.size());
    }

    @Test
    void nullListenerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> publisher.subscribe(null));
    }

    @Test
    void differentListenersReactDifferently() {
        LoggingTransferListener logging = new LoggingTransferListener();
        StatisticsTransferListener statistics = new StatisticsTransferListener();
        AlertTransferListener alert = new AlertTransferListener(2000);

        publisher.subscribe(logging);
        publisher.subscribe(statistics);
        publisher.subscribe(alert);

        service.transfer(NUM_FROM, NUM_TO, 1000);
        service.transfer(NUM_FROM, NUM_TO, 3000);

        assertEquals(2, logging.size());
        assertEquals(2, statistics.getTotalTransfers());
        assertEquals(1, alert.getAlertCount());
    }

    @Test
    void observerNotifiesAll_subscribersWhileStrategySelectsOne() {
        LoggingTransferListener logging = new LoggingTransferListener();
        StatisticsTransferListener statistics = new StatisticsTransferListener();
        AlertTransferListener alert = new AlertTransferListener(500);

        publisher.subscribe(logging);
        publisher.subscribe(statistics);
        publisher.subscribe(alert);

        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(1, logging.size());
        assertEquals(1, statistics.getTotalTransfers());
        assertEquals(1, alert.getAlertCount());
    }

    @Test
    void listenersCanBeAddedDynamically() {
        assertEquals(0, publisher.getSubscriberCount());

        service.transfer(NUM_FROM, NUM_TO, 500);

        LoggingTransferListener listener = new LoggingTransferListener();
        publisher.subscribe(listener);
        assertEquals(1, publisher.getSubscriberCount());

        service.transfer(NUM_FROM, NUM_TO, 500);

        assertEquals(1, listener.size());
    }

    @Test
    void publisherCanWorkIndependently() {
        TransferEventPublisher standalonePublisher = new TransferEventPublisher();
        LoggingTransferListener listener = new LoggingTransferListener();
        standalonePublisher.subscribe(listener);

        Transaction tx = new Transaction(
                1L,
                TransactionType.TRANSFER,
                NUM_FROM,
                5000,
                TransactionStatus.SUCCESS
        );
        standalonePublisher.publish(tx);

        assertEquals(1, listener.size());
    }

    @Test
    void transferServiceWithoutPublisherStillWorks() {
        TransferApplicationService serviceWithoutPublisher = new TransferApplicationService(
                repository,
                transferService
        );

        assertDoesNotThrow(() -> serviceWithoutPublisher.transfer(NUM_FROM, NUM_TO, 1000));
        assertEquals(9000, repository.findById(NUM_FROM).get().getBalance());
    }

    @Test
    void lambdaAsObserver() {
        int[] counter = {0};
        TransferListener lambdaListener = tx -> counter[0]++;

        publisher.subscribe(lambdaListener);
        service.transfer(NUM_FROM, NUM_TO, 1000);
        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(2, counter[0]);
    }

    @Test
    void listenerCanUnsubscribeDuringIteration() {
        int[] callCount = {0};

        TransferListener selfUnsubscribing = new TransferListener() {
            @Override
            public void onTransferCompleted(Transaction transaction) {
                callCount[0]++;
                publisher.unsubscribe(this);
            }
        };
        publisher.subscribe(selfUnsubscribing);

        service.transfer(NUM_FROM, NUM_TO, 1000);
        service.transfer(NUM_FROM, NUM_TO, 1000);

        assertEquals(1, callCount[0],
                "Listener должен получить только одно событие, так как отписался после первого");

        assertEquals(0, publisher.getSubscriberCount(),
                "После отписки список подписчиков должен быть пуст");
    }
}