package banking.test;

import banking.domain.AccountNumber;
import banking.domain.Transaction;
import banking.domain.TransactionStatus;
import banking.domain.TransactionType;

import banking.export.TransactionFileExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFileExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateFileAndExportTransactions() throws IOException {

        AccountNumber from = new AccountNumber("1111111111");
        AccountNumber to = new AccountNumber("2222222222");

        Transaction transaction = new Transaction(
                1L,
                TransactionType.TRANSFER,
                from,
                to,
                1000.0,
                TransactionStatus.SUCCESS
        );

        TransactionFileExporter exporter = new TransactionFileExporter();

        Path file = tempDir
                .resolve("exports")
                .resolve("transactions.txt");

        exporter.export(file, List.of(transaction));

        assertTrue(Files.exists(file),
                "Файл должен быть создан");

        assertTrue(Files.size(file) > 0,
                "Файл не должен быть пустым");

        List<String> lines = Files.readAllLines(file);

        assertEquals(2, lines.size());

        assertEquals(
                "id;from;to;amount;status",
                lines.get(0)
        );

        assertEquals(
                "1;1111111111;2222222222;1000.0;SUCCESS",
                lines.get(1)
        );
    }
}