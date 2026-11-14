
package banking.test;

import banking.dto.TransactionDto;
import banking.export.TransactionFileImporter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFileImporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadTransactionsFromFile() throws IOException {

        Path file = tempDir.resolve("transactions.txt");

        List<String> lines = List.of(
                "id;from;to;amount;status",
                "1;1111111111;2222222222;1000.0;SUCCESS",
                "2;;3333333333;500.0;SUCCESS",
                "3;4444444444;;200.0;SUCCESS"
        );

        Files.write(file, lines, StandardCharsets.UTF_8);

        TransactionFileImporter importer =
                new TransactionFileImporter();

        List<TransactionDto> result = importer.load(file);

        assertEquals(3, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("1111111111", result.get(0).from());
        assertEquals("2222222222", result.get(0).to());
        assertEquals(1000.0, result.get(0).amount());
        assertEquals("SUCCESS", result.get(0).status());

        assertEquals(2L, result.get(1).id());
        assertEquals("", result.get(1).from());
        assertEquals("3333333333", result.get(1).to());

        assertEquals(3L, result.get(2).id());
        assertEquals("4444444444", result.get(2).from());
        assertEquals("", result.get(2).to());
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() throws IOException {

        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        TransactionFileImporter importer =
                new TransactionFileImporter();

        List<TransactionDto> result = importer.load(file);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() throws IOException {

        Path file = tempDir.resolve("invalid.txt");

        Files.write(
                file,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;abc;SUCCESS"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.load(file)
        );

        assertTrue(exception.getMessage().contains("line 2"));
    }

    @Test
    void shouldCountSuccessfulTransactionsWithoutLoadingAllLines()
            throws IOException {

        Path file = tempDir.resolve("statistics.txt");

        List<String> lines = List.of(
                "id;from;to;amount;status",
                "1;1111111111;2222222222;1000.0;SUCCESS",
                "2;3333333333;4444444444;500.0;FAILED",
                "3;5555555555;6666666666;200.0;SUCCESS",
                "4;7777777777;8888888888;300.0;REJECTED",
                "5;9999999999;0000000000;400.0;SUCCESS"
        );

        Files.write(file, lines, StandardCharsets.UTF_8);

        TransactionFileImporter importer =
                new TransactionFileImporter();

        long count = importer.countSuccessful(file);

        assertEquals(3L, count);
    }
}
