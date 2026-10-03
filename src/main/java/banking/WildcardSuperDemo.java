package banking;

import java.util.ArrayList;
import java.util.List;

public class WildcardSuperDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация Wildcard ? super \n");

        List<DebitAccount> debitList = new ArrayList<>();
        BankAccountUtils.addDemoDebitAccounts(debitList);
        System.out.println("1. List<DebitAccount>: добавлено " + debitList.size() + " счетов");

        List<BankAccount> bankAccountList = new ArrayList<>();
        BankAccountUtils.addDemoDebitAccounts(bankAccountList);
        System.out.println("2. List<BankAccount>: добавлено " + bankAccountList.size() + " счетов");

        List<Object> objectList = new ArrayList<>();
        BankAccountUtils.addDemoDebitAccounts(objectList);
        System.out.println("3. List<Object>: добавлено " + objectList.size() + " счетов");

        System.out.println();

        System.out.println("Проблема чтения из List<? super DebitAccount> ");
        demonstrateReadingProblem(bankAccountList);
    }

    public static void demonstrateReadingProblem(List<? super DebitAccount> target) {
        Object value = target.get(0);
        System.out.println("Прочитанное значение (тип Object): " + value);

        System.out.println("Компилятор гарантирует только Object при чтении.");
        System.out.println("Реальный тип списка может быть List<Object>, поэтому безопаснее возвращать Object.");
    }
}