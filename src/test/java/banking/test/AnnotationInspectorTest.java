
package banking.test;

import banking.application.AccountService;
import banking.application.TransferService;
import banking.reflection.AnnotationInspector;
import org.junit.jupiter.api.Test;

class AnnotationInspectorTest {

    @Test
    void shouldInspectTransferService() {
        AnnotationInspector inspector = new AnnotationInspector();

        inspector.inspect(TransferService.class);
    }

    @Test
    void shouldInspectAccountService() {
        AnnotationInspector inspector = new AnnotationInspector();

        inspector.inspect(AccountService.class);
    }
}
