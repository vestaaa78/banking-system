package banking.concurrency;

import java.util.List;

public class StatisticsMonitor implements Runnable {

    private final List<BankClient> clients;
    private final long intervalMillis;

    private volatile boolean running = true;

    private int previousAttempts = 0;
    private long previousTime;

    public StatisticsMonitor(
            List<BankClient> clients,
            long intervalMillis
    ) {
        this.clients = clients;
        this.intervalMillis = intervalMillis;
        this.previousTime = System.nanoTime();
    }

    @Override
    public void run() {

        System.out.println("[STATISTICS] Monitor started");

        while (running && !Thread.currentThread().isInterrupted()) {

            int successful = 0;
            int failed = 0;
            int attempts = 0;

            for (BankClient client : clients) {
                successful += client.getSuccessfulTransfers();
                failed += client.getFailedTransfers();
                attempts += client.getTotalAttempts();
            }

            long currentTime = System.nanoTime();

            double elapsedSeconds =
                    (currentTime - previousTime) / 1_000_000_000.0;

            int newAttempts = attempts - previousAttempts;

            double operationsPerSecond = 0;

            if (elapsedSeconds > 0) {
                operationsPerSecond = newAttempts / elapsedSeconds;
            }

            System.out.println();
            System.out.println("[STATISTICS]");
            System.out.println("success = " + successful);
            System.out.println("failed = " + failed);
            System.out.printf(
                    "operations/sec ~= %.2f%n",
                    operationsPerSecond
            );

            previousAttempts = attempts;
            previousTime = currentTime;

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[STATISTICS] Monitor stopped");
    }

    public void stop() {
        running = false;
    }
}