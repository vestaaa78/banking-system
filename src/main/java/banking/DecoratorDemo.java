package banking;

public class DecoratorDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация паттерна Decorator \n");

        System.out.println("1. Базовый ConsoleNotificationAdapter:");
        NotificationService base = new ConsoleNotificationAdapter();
        base.notify("Hello, World!");

        System.out.println("\n2. С логированием:");
        NotificationService withLogging = new LoggingNotificationDecorator(base);
        withLogging.notify("Transfer completed");

        System.out.println("\n3. С префиксом:");
        NotificationService withPrefix = new PrefixNotificationDecorator(base, "[BANK] ");
        withPrefix.notify("Your balance is low");

        System.out.println("\n4. Цепочка: Logging -> Prefix -> Base:");
        NotificationService chain = new LoggingNotificationDecorator(
                new PrefixNotificationDecorator(
                        new ConsoleNotificationAdapter(),
                        "[VIP] "
                )
        );
        chain.notify("Special offer for you");

        System.out.println("\n5. Подсчёт уведомлений:");
        CountingNotificationDecorator counting = new CountingNotificationDecorator(
                new ConsoleNotificationAdapter()
        );
        counting.notify("First");
        counting.notify("Second");
        counting.notify("Third");
        System.out.println("   Всего отправлено: " + counting.getCount());

        System.out.println("\n6. Retry-декоратор (имитация сбоя):");
        FailingNotificationService failing = new FailingNotificationService(2, message -> {
            System.out.println("   ✓ Сообщение доставлено: " + message);
        });
        NotificationService withRetry = new RetryNotificationDecorator(failing, 3);
        withRetry.notify("Important notification");

        System.out.println("\n7. Преимущества Decorator перед наследованием:");
        System.out.println("   - Можно комбинировать поведения динамически");
        System.out.println("   - Не нужно создавать класс для каждой комбинации");
        System.out.println("   - Поведения можно добавлять/убирать в runtime");
        System.out.println("   - Соответствует принципу Open/Closed");
    }
}