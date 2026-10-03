package banking;

public class TransactionDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация Transaction с ID\n");

        Transaction deposit = new Transaction(
                1L,
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000.0,
                TransactionStatus.SUCCESS
        );
        System.out.println("1. " + deposit);

        Transaction withdrawal = new Transaction(
                2L,
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                2000.0,
                TransactionStatus.SUCCESS
        );
        System.out.println("2. " + withdrawal);

        AccountNumber senderNumber = new AccountNumber("1111111111");
        Transaction rejectedTransfer = new Transaction(
                3L,
                TransactionType.TRANSFER,
                senderNumber,
                100000.0,
                TransactionStatus.REJECTED
        );
        System.out.println("3. " + rejectedTransfer);

        Transaction successfulTransfer = new Transaction(
                4L,
                TransactionType.TRANSFER,
                new AccountNumber("2222222222"),
                3000.0,
                TransactionStatus.SUCCESS
        );
        System.out.println("4. " + successfulTransfer);

        System.out.println("\nДемонстрация завершена успешно!");
    }
}