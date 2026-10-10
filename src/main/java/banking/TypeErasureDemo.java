package banking;

import java.util.ArrayList;
import java.util.List;

public class TypeErasureDemo {

    public static void main(String[] args) {
        System.out.println("Эксперимент: Type Erasure\n");

        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        System.out.println("Class of strings:  " + strings.getClass());
        System.out.println("Class of numbers:  " + numbers.getClass());

        boolean isSameClass = strings.getClass() == numbers.getClass();
        System.out.println("Are they the same class at runtime? " + isSameClass);

        System.out.println("\nВывод");
        if (isSameClass) {
            System.out.println("Подтверждено: на этапе выполнения (runtime) это один и тот же класс ArrayList.");
            System.out.println("Информация о <String> и <Integer> была стёрта компилятором.");
        }

        System.out.println("\nПрактическое ограничение");
        System.out.println("Попытка проверить: if (strings instanceof List<String>)");
        System.out.println("Эта строка не скомпилируется!");
        System.out.println("Причина: из-за стирания типов JVM не может проверить, является ли список списком строк.");

    }
}