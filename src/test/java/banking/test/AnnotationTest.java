
package banking.test;

import banking.annotation.Audited;
import banking.annotation.RequiresRole;
import banking.application.AccountService;
import banking.application.TransferService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTest {

    @Test
    void transferServiceShouldHaveAuditedAnnotation() {

        assertTrue(
                TransferService.class.isAnnotationPresent(
                        Audited.class
                )
        );
    }

    @Test
    void requiresRoleShouldContainAdminValue()
            throws NoSuchMethodException {

        Method method = AccountService.class.getMethod(
                "close",
                banking.domain.AccountNumber.class
        );

        RequiresRole annotation =
                method.getAnnotation(RequiresRole.class);

        assertNotNull(annotation);
        assertEquals("ADMIN", annotation.value());
    }
}
