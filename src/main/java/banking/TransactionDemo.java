package banking;

public class TransactionDemo {

    public static void main(String[] args) {
        Transaction deposit = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS
        );
        System.out.println(deposit);

        Transaction withdrawal = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                2000,
                TransactionStatus.SUCCESS
        );
        System.out.println(withdrawal);

        AccountNumber senderNumber = new AccountNumber("1111111111");
        Transaction rejectedTransfer = new Transaction(
                TransactionType.TRANSFER,
                senderNumber,
                100000,
                TransactionStatus.REJECTED
        );
        System.out.println(rejectedTransfer);

        Transaction successfulTransfer = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("2222222222"),
                3000,
                TransactionStatus.SUCCESS
        );
        System.out.println(successfulTransfer);
    }
}
