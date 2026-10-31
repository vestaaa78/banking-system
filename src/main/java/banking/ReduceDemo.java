package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReduceDemo {

    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));
        accounts.add(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 15000, 1000));

        System.out.println("Демонстрация reduce (свёртки)\n");

        System.out.println("1. Ручной цикл (императивный стиль):");
        double totalLoop = 0;
        for (BankAccount a : accounts) {
            totalLoop += a.getBalance();
        }
        System.out.println("   Сумма: " + totalLoop);

        System.out.println("\n2. reduce с identity (0.0):");
        double totalReduce = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);
        System.out.println("   Сумма: " + totalReduce);

        System.out.println("\n3. reduce без identity (возвращает Optional):");
        Optional<Double> totalOptional = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::sum);
        totalOptional.ifPresent(val -> System.out.println("   Сумма: " + val));

        System.out.println("\n4. reduce с lambda:");
        double totalLambda = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, (a, b) -> a + b);
        System.out.println("   Сумма: " + totalLambda);

        System.out.println("\n5. reduce для поиска максимума:");
        Optional<Double> maxBalance = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::max);
        maxBalance.ifPresent(val -> System.out.println("   Максимальный баланс: " + val));

        System.out.println("\n6. reduce для поиска минимума:");
        Optional<Double> minBalance = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::min);
        minBalance.ifPresent(val -> System.out.println("   Минимальный баланс: " + val));

        System.out.println("\n7. reduce для конкатенации строк:");
        String allOwners = accounts.stream()
                .map(BankAccount::getOwner)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        System.out.println("   Владельцы: " + allOwners);

        System.out.println("\n8. reduce для подсчёта количества:");
        int count = accounts.stream()
                .reduce(0, (n, account) -> n + 1, Integer::sum);
        System.out.println("   Количество счетов: " + count);

        System.out.println("\n9. reduce для накопления статистики:");
        AccountStatistics stats = accounts.stream()
                .reduce(
                        new AccountStatistics(0, 0, 0),
                        (acc, account) -> new AccountStatistics(
                                acc.count + 1,
                                acc.totalBalance + account.getBalance(),
                                Math.max(acc.maxBalance, account.getBalance())
                        ),
                        (acc1, acc2) -> new AccountStatistics(
                                acc1.count + acc2.count,
                                acc1.totalBalance + acc2.totalBalance,
                                Math.max(acc1.maxBalance, acc2.maxBalance)
                        )
                );
        System.out.println("   Статистика: " + stats);

        System.out.println("\n10. Поведение с пустым списком:");
        List<BankAccount> emptyList = new ArrayList<>();

        double sumWithIdentity = emptyList.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);
        System.out.println("   reduce с identity: " + sumWithIdentity);

        Optional<Double> sumWithoutIdentity = emptyList.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::sum);
        System.out.println("   reduce без identity: " + sumWithoutIdentity);
    }

    record AccountStatistics(int count, double totalBalance, double maxBalance) {
        @Override
        public String toString() {
            return String.format("count=%d, total=%.0f, max=%.0f",
                    count, totalBalance, maxBalance);
        }
    }
}