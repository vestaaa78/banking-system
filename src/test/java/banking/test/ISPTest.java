package banking.test;

import banking.ports.AccountRepository;
import banking.ports.AccountReader;
import banking.ports.AccountWriter;
import banking.ports.AccountDeleter;
import banking.ports.AccountCatalog;
import banking.ports.NotificationService;
import banking.domain.CommissionPolicy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ISPTest {

    @Test
    void accountRepositoryIsCompositionOfSmallInterfaces() {
        assertTrue(AccountReader.class.isAssignableFrom(AccountRepository.class));
        assertTrue(AccountWriter.class.isAssignableFrom(AccountRepository.class));
        assertTrue(AccountDeleter.class.isAssignableFrom(AccountRepository.class));
        assertTrue(AccountCatalog.class.isAssignableFrom(AccountRepository.class));
    }

    @Test
    void accountReaderHasOnlyReadMethods() {
        Method[] methods = AccountReader.class.getDeclaredMethods();
        assertEquals(2, methods.length, "AccountReader должен иметь только 2 метода: findById и existsById");

        boolean hasFindById = false;
        boolean hasExistsById = false;
        for (Method m : methods) {
            if (m.getName().equals("findById")) hasFindById = true;
            if (m.getName().equals("existsById")) hasExistsById = true;
        }
        assertTrue(hasFindById && hasExistsById);
    }

    @Test
    void accountWriterHasOnlySaveMethod() {
        Method[] methods = AccountWriter.class.getDeclaredMethods();
        assertEquals(1, methods.length, "AccountWriter должен иметь только 1 метод: save");
        assertEquals("save", methods[0].getName());
    }

    @Test
    void accountDeleterHasOnlyDeleteMethod() {
        Method[] methods = AccountDeleter.class.getDeclaredMethods();
        assertEquals(1, methods.length, "AccountDeleter должен иметь только 1 метод: deleteById");
        assertEquals("deleteById", methods[0].getName());
    }

    @Test
    void accountCatalogHasOnlyListMethods() {
        Method[] methods = AccountCatalog.class.getDeclaredMethods();
        assertEquals(2, methods.length, "AccountCatalog должен иметь только 2 метода: findAll и size");
    }

    @Test
    void notificationServiceIsSmallInterface() {
        Method[] methods = NotificationService.class.getDeclaredMethods();
        int abstractMethods = 0;
        for (Method m : methods) {
            if (Modifier.isAbstract(m.getModifiers())) {
                abstractMethods++;
            }
        }
        assertEquals(1, abstractMethods, "NotificationService должен быть маленьким интерфейсом с 1 методом");
    }

    @Test
    void commissionPolicyIsSmallInterface() {
        Method[] methods = CommissionPolicy.class.getDeclaredMethods();
        int abstractMethods = 0;
        for (Method m : methods) {
            if (Modifier.isAbstract(m.getModifiers())) {
                abstractMethods++;
            }
        }
        assertEquals(1, abstractMethods, "CommissionPolicy должен быть маленьким интерфейсом с 1 методом");
    }

    @Test
    void accountRepositoryDoesNotContainExportOrEmailMethods() {
        Method[] methods = AccountRepository.class.getMethods();
        for (Method m : methods) {
            String name = m.getName().toLowerCase();
            assertFalse(name.contains("export"), "AccountRepository не должен иметь методов экспорта");
            assertFalse(name.contains("email"), "AccountRepository не должен иметь методов email");
            assertFalse(name.equals("print"), "AccountRepository не должен иметь метода print");
        }
    }
}