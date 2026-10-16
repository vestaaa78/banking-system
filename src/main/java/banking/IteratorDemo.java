package banking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация Iterator и ConcurrentModificationException \n");

        // Создаём тестовые транзакции
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, new AccountNumber("1111111111"), 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, new AccountNumber("2222222222"), 500, TransactionStatus.REJECTED);
        Transaction tx3 = new Transaction(3L, TransactionType.TRANSFER, new AccountNumber("3333333333"), 750, TransactionStatus.SUCCESS);
        Transaction tx4 = new Transaction(4L, TransactionType.DEPOSIT, new AccountNumber("4444444444"), 2000, TransactionStatus.REJECTED);

        System.out.println(" Демонстрация 1: Ошибочный for-each с remove() ");
        List<Transaction> wrongList = new ArrayList<>();
        wrongList.add(tx1);
        wrongList.add(tx2);
        wrongList.add(tx3);
        wrongList.add(tx4);

        try {
            for (Transaction tx : wrongList) {
                if (tx.status() == TransactionStatus.REJECTED) {
                    wrongList.remove(tx);
                }
            }
            System.out.println(" Удаление прошло успешно (маловероятно)");
        } catch (Exception e) {
            System.out.println(" Ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("  Причина: for-each использует внутренний Iterator, который обнаруживает");
            System.out.println("  модификацию коллекции и выбрасывает ConcurrentModificationException.\n");
        }

        System.out.println(" Демонстрация 2: Правильный Iterator ");
        List<Transaction> correctList = new ArrayList<>();
        correctList.add(tx1);
        correctList.add(tx2);
        correctList.add(tx3);
        correctList.add(tx4);

        Iterator<Transaction> iterator = correctList.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                iterator.remove();
            }
        }

        System.out.println(" Удаление прошло успешно!");
        System.out.println("   Осталось транзакций: " + correctList.size());
        System.out.println("   Все оставшиеся транзакции имеют статус SUCCESS: " +
                correctList.stream().allMatch(tx -> tx.status() == TransactionStatus.SUCCESS));
    }
}