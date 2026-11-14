package banking.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ArchitectureTest {

    private static final String MAIN_DIR = "src/main/java/banking";

    @Test
    void domainLayerDoesNotDependOnOuterLayers() throws Exception {
        List<String> violations = new ArrayList<>();

        List<Path> files = getJavaFiles(Paths.get(MAIN_DIR, "domain"));
        for (Path file : files) {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.startsWith("import banking.ports") ||
                        line.startsWith("import banking.application") ||
                        line.startsWith("import banking.adapters")) {
                    violations.add(file.getFileName() + ": " + line.trim());
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Domain layer не должен зависеть от внешних слоёв. Нарушения:\n" +
                        String.join("\n", violations));
    }

    @Test
    void portsLayerDoesNotDependOnApplicationAndAdapters() throws Exception {
        List<String> violations = new ArrayList<>();

        List<Path> files = getJavaFiles(Paths.get(MAIN_DIR, "ports"));
        for (Path file : files) {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.startsWith("import banking.application") ||
                        line.startsWith("import banking.adapters")) {
                    violations.add(file.getFileName() + ": " + line.trim());
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Ports layer не должен зависеть от application и adapters. Нарушения:\n" +
                        String.join("\n", violations));
    }

    @Test
    void applicationLayerDoesNotDependOnAdapters() throws Exception {
        List<String> violations = new ArrayList<>();

        List<Path> files = getJavaFiles(Paths.get(MAIN_DIR, "application"));
        for (Path file : files) {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.startsWith("import banking.adapters")) {
                    violations.add(file.getFileName() + ": " + line.trim());
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Application layer не должен зависеть от adapters. Нарушения:\n" +
                        String.join("\n", violations));
    }

    @Test
    void allLayersExist() {
        assertTrue(new File(MAIN_DIR + "/domain").isDirectory(), "Пакет domain должен существовать");
        assertTrue(new File(MAIN_DIR + "/ports").isDirectory(), "Пакет ports должен существовать");
        assertTrue(new File(MAIN_DIR + "/application").isDirectory(), "Пакет application должен существовать");
        assertTrue(new File(MAIN_DIR + "/adapters").isDirectory(), "Пакет adapters должен существовать");
    }

    @Test
    void domainContainsCoreEntities() {
        assertTrue(classExists("banking.domain.BankAccount"));
        assertTrue(classExists("banking.domain.AccountNumber"));
        assertTrue(classExists("banking.domain.Transaction"));
        assertTrue(classExists("banking.domain.CommissionPolicy"));
    }

    @Test
    void portsContainInterfaces() {
        assertTrue(classExists("banking.ports.AccountRepository"));
        assertTrue(classExists("banking.ports.NotificationService"));
    }

    @Test
    void applicationContainsUseCases() {
        assertTrue(classExists("banking.application.AccountService"));
        assertTrue(classExists("banking.application.TransferApplicationService"));
    }

    @Test
    void adaptersContainImplementations() {
        assertTrue(classExists("banking.adapters.InMemoryAccountRepository"));
        assertTrue(classExists("banking.adapters.ConsoleNotificationAdapter"));
    }

    private List<Path> getJavaFiles(Path directory) throws Exception {
        List<Path> result = new ArrayList<>();
        if (!Files.exists(directory)) {
            return result;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(result::add);
        }
        return result;
    }

    private boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}