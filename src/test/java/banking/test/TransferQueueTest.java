package banking.test;

import banking.AccountNumber;
import banking.TransferQueue;
import banking.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferQueueTest {

    private TransferQueue queue;
    private AccountNumber from;
    private AccountNumber to;

    @BeforeEach
    void setUp() {
        queue = new TransferQueue();
        from = new AccountNumber("1111111111");
        to = new AccountNumber("2222222222");
    }

    @Test
    void elementsAreReturnedInFifoOrder() {
        TransferRequest a = new TransferRequest(from, to, 100);
        TransferRequest b = new TransferRequest(from, to, 200);
        TransferRequest c = new TransferRequest(from, to, 300);

        queue.add(a);
        queue.add(b);
        queue.add(c);

        assertSame(a, queue.next(), "Первый должен быть A");
        assertSame(b, queue.next(), "Второй должен быть B");
        assertSame(c, queue.next(), "Третий должен быть C");
    }

    @Test
    void peekDoesNotRemoveFirstElement() {
        TransferRequest request = new TransferRequest(from, to, 500);
        queue.add(request);

        TransferRequest peeked = queue.peek();
        TransferRequest peekedAgain = queue.peek();

        assertSame(request, peeked);
        assertSame(request, peekedAgain, "Второй peek() должен вернуть тот же элемент");
        assertEquals(1, queue.size(), "Размер очереди не должен измениться после peek()");
    }

    @Test
    void nextOnEmptyQueueReturnsNull() {
        TransferRequest result = queue.next();

        assertNull(result, "next() на пустой очереди должен вернуть null");
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekOnEmptyQueueReturnsNull() {
        TransferRequest result = queue.peek();

        assertNull(result, "peek() на пустой очереди должен вернуть null");
    }

    @Test
    void sizeDecreasesAfterExtraction() {
        queue.add(new TransferRequest(from, to, 100));
        queue.add(new TransferRequest(from, to, 200));
        assertEquals(2, queue.size());

        queue.next();

        assertEquals(1, queue.size(), "После извлечения размер должен уменьшиться на 1");

        queue.next();
        assertEquals(0, queue.size(), "После второго извлечения очередь должна стать пустой");
        assertTrue(queue.isEmpty());
    }

    @Test
    void shouldThrowExceptionOnNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> queue.add(null));
    }
}