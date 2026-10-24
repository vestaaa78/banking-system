//package banking;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
//public class ComparatorDemo {
//
//    public static void main(String[] args) {
//        List<BankAccount> accounts = new ArrayList<>();
//        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000.0));
//        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 1000.0));
//        accounts.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 10000.0));
//
//        System.out.println("Исходный список:");
//        printAccounts(accounts);
//
//        Comparator<BankAccount> byBalanceAnonymous = new Comparator<>() {
//            @Override
//            public int compare(BankAccount a, BankAccount b) {
//                return Double.compare(a.getBalance(), b.getBalance());
//            }
//        };
//
//        List<BankAccount> list1 = new ArrayList<>(accounts);
//        list1.sort(byBalanceAnonymous);
//        System.out.println("\n1. Сортировка через анонимный класс:");
//        printAccounts(list1);
//
//        Comparator<BankAccount> byBalanceLambda =
//                (a, b) -> Double.compare(a.getBalance(), b.getBalance());
//
//        List<BankAccount> list2 = new ArrayList<>(accounts);
//        list2.sort(byBalanceLambda);
//        System.out.println("\n2. Сортировка через Lambda-выражение:");
//        printAccounts(list2);
//
//        Comparator<BankAccount> byBalanceMethodRef =
//                Comparator.comparingDouble(BankAccount::getBalance);
//
//        List<BankAccount> list3 = new ArrayList<>(accounts);
//        list3.sort(byBalanceMethodRef);
//        System.out.println("\n3. Сортировка через Comparator.comparingDouble и ссылку на метод:");
//        printAccounts(list3);
//    }
//
//    private static void printAccounts(List<BankAccount> accounts) {
//        for (BankAccount acc : accounts) {
//            System.out.printf("  %-10s : %.2f%n", acc.getOwner(), acc.getBalance());
//        }
//    }
//}