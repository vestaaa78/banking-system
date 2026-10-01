package banking;

public class BankingSystemDemo {

    public static void main(String[] args) {
        System.out.println("Банковская система\n");

        AccountNumber account1Number = new AccountNumber("1234567890");
        AccountNumber account2Number = new AccountNumber("0987654321");

        BankAccount alice = new DebitAccount(account1Number, "Alice", 10000);
        BankAccount bob = new DebitAccount(account2Number, "Bob", 5000);

        System.out.println("Начальные балансы:");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println();

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        System.out.println("Сценарий 1: Успешный перевод");
        try {
            transferService.transfer(alice, bob, 3000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Балансы после перевода:");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println();

        System.out.println("Сценарий 2: Недостаточно средств");
        try {
            transferService.transfer(alice, bob, 50000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Сценарий 3: Отрицательная сумма");
        try {
            transferService.transfer(alice, bob, -1000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Сценарий 4: Превышение лимита перевода");
        try {
            transferService.transfer(alice, bob, 60000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Сценарий 5: Перевод самому себе");
        try {
            transferService.transfer(alice, alice, 1000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Сценарий 6: Пополнение счёта");
        try {
            alice.deposit(5000);
            System.out.println("Deposit completed. New balance: " + alice.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Сценарий 7: Снятие со счёта");
        try {
            alice.withdraw(2000);
            System.out.println("Withdrawal completed. New balance: " + alice.getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Финальные балансы:");
        System.out.println(alice);
        System.out.println(bob);
    }
}