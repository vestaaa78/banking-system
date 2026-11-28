
package banking.concurrency;

import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.DebitAccount;

public class DeadlockDemo {

    private static volatile boolean thread1LockedA = false;
    private static volatile boolean thread2LockedB = false;

    public static void main(String[] args) throws InterruptedException {

        BankAccount accountA = new DebitAccount(
                new AccountNumber("1000000001"),
                "Client A",
                100_000
        );

        BankAccount accountB = new DebitAccount(
                new AccountNumber("1000000002"),
                "Client B",
                100_000
        );

        Thread thread1 = new Thread(() -> {

            synchronized (accountA) {
                System.out.println("Thread 1 locked A");

                thread1LockedA = true;

                while (!thread2LockedB) {
                    Thread.yield();
                }

                System.out.println("Thread 1 waits for B");

                synchronized (accountB) {
                    System.out.println("Thread 1 locked B");
                }
            }

        }, "Thread-1");

        Thread thread2 = new Thread(() -> {

            synchronized (accountB) {
                System.out.println("Thread 2 locked B");

                thread2LockedB = true;

                while (!thread1LockedA) {
                    Thread.yield();
                }

                System.out.println("Thread 2 waits for A");

                synchronized (accountA) {
                    System.out.println("Thread 2 locked A");
                }
            }

        }, "Thread-2");

        thread1.setDaemon(true);
        thread2.setDaemon(true);

        thread1.start();
        thread2.start();

        thread1.join(3000);
        thread2.join(3000);

        System.out.println("\n========== DEADLOCK CHECK ==========");

        System.out.println("Thread 1 alive: " + thread1.isAlive());
        System.out.println("Thread 2 alive: " + thread2.isAlive());

        if (thread1.isAlive() && thread2.isAlive()) {
            System.out.println("DEADLOCK DETECTED");
        } else {
            System.out.println("Deadlock was not reproduced");
        }
    }
}
