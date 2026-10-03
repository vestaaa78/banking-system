package banking;

public class PairDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация универсальной пары Pair<K, V>\n");

        Pair<String, Integer> age = new Pair<>("Ivan", 20);

        System.out.println("1. Pair<String, Integer>:");
        System.out.println("Ключ (тип String): " + age.key());
        System.out.println("Значение (тип Integer): " + age.value());
        System.out.println("Автоматический toString: " + age + "\n");

        AccountNumber accountNumber = new AccountNumber("1234567890");
        Pair<AccountNumber, String> owner = new Pair<>(accountNumber, "Ivan");

        System.out.println("2. Pair<AccountNumber, String>:");
        System.out.println("Ключ (тип AccountNumber): " + owner.key().value());
        System.out.println("Значение (тип String): " + owner.value());
        System.out.println("Автоматический toString: " + owner + "\n");

        Pair<Double, Boolean> transactionInfo = new Pair<>(1500.50, true);

        System.out.println("3. Pair<Double, Boolean>:");
        System.out.println("Ключ: " + transactionInfo.key());
        System.out.println("Значение: " + transactionInfo.value());
        System.out.println("Автоматический toString: " + transactionInfo + "\n");

        System.out.println("Все комбинации типов работают корректно без приведения типов (casting)!");
    }
}