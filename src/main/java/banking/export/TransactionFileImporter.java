
package banking.export;

import banking.domain.TransactionStatus;
import banking.dto.TransactionDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TransactionFileImporter {

    public List<TransactionDto> load(Path path) throws IOException {

        Objects.requireNonNull(path, "Path cannot be null");

        List<String> lines = Files.readAllLines(
                path,
                StandardCharsets.UTF_8
        );

        List<TransactionDto> transactions = new ArrayList<>();

        if (lines.isEmpty()) {
            return transactions;
        }

        for (int i = 1; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            String[] fields = line.split(";", -1);

            if (fields.length != 5) {
                throw new IllegalArgumentException(
                        "Invalid transaction at line " + (i + 1)
                                + ": expected 5 fields, but found "
                                + fields.length
                );
            }

            try {
                long id = Long.parseLong(fields[0].trim());

                String from = fields[1].trim();
                String to = fields[2].trim();

                double amount = Double.parseDouble(fields[3].trim());

                TransactionStatus status =
                        TransactionStatus.valueOf(fields[4].trim());

                TransactionDto dto = new TransactionDto(
                        id,
                        from,
                        to,
                        amount,
                        status.name()
                );

                transactions.add(dto);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid number at line " + (i + 1)
                                + ": " + line,
                        e
                );

            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException(
                        "Invalid transaction data at line "
                                + (i + 1) + ": " + line,
                        e
                );
            }
        }

        return transactions;
    }

    public long countSuccessful(Path path) throws IOException {

        Objects.requireNonNull(path, "Path cannot be null");

        try (Stream<String> lines = Files.lines(path)) {

            return lines
                    .filter(line -> line.contains("SUCCESS"))
                    .count();
        }
    }
}
