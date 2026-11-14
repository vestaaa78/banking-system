package banking.export;

import banking.domain.Transaction;
import banking.domain.TransactionType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class TransactionFileExporter {

    public void export(Path path, List<Transaction> transactions)
            throws IOException {

        Objects.requireNonNull(path, "Path cannot be null");
        Objects.requireNonNull(transactions, "Transactions cannot be null");

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = transactions.stream()
                .map(this::toCsvLine)
                .toList();

        lines = new java.util.ArrayList<>(lines);
        lines.add(0, "id;from;to;amount;status");

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private String toCsvLine(Transaction tx) {

        String from = "";
        String to = "";

        switch (tx.type()) {

            case DEPOSIT -> {
                to = tx.account().value();
            }

            case WITHDRAWAL -> {
                from = tx.account().value();
            }

            case TRANSFER -> {
                if (tx.to() == null) {
                    throw new IllegalArgumentException(
                            "Recipient account is missing for transaction: " + tx.id()
                    );
                }

                from = tx.account().value();
                to = tx.to().value();
            }
        }

        return tx.id() + ";"
                + from + ";"
                + to + ";"
                + tx.amount() + ";"
                + tx.status().name();
    }
}