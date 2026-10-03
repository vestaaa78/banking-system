//package banking;
//
//public class BoxGenericDemo {
//
//    public static void main(String[] args) {
//        System.out.println("Демонстрация Generic-контейнера\n");
//
//        Box<String> text = new Box<>();
//        text.set("Java");
//
//        String s = text.get();
//
//        System.out.println("1. String значение: " + s);
//        System.out.println("Длина строки: " + s.length() + "\n");
//
//        Box<Integer> number = new Box<>();
//        number.set(42);
//
//        Integer n = number.get();
//
//        System.out.println("2. Integer значение: " + n);
//        System.out.println("Увеличенное значение: " + (n + 10) + "\n");
//
//        System.out.println("3. Попытка положить Integer в Box<String>: ");
//        Box<String> safeBox = new Box<>();
//
//        System.out.println("Компилятор заблокировал эту операцию (ошибка: incompatible types).");
//        System.out.println("ClassCastException больше невозможен!\n");
//    }
//}