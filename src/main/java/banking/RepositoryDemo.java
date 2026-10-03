package banking;

public class RepositoryDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация Generic Repository\n");

        Repository<AccountNumber, BankAccount> accountRepo = new InMemoryRepository<>();

        BankAccount acc1 = new DebitAccount(new AccountNumber("1111111111"), "Alice", 10000);
        BankAccount acc2 = new SavingsAccount(new AccountNumber("2222222222"), "Bob", 5000, 1000);

        accountRepo.save(acc1);
        accountRepo.save(acc2);

        System.out.println("1. BankAccount Repository:");
        System.out.println("Размер: " + accountRepo.size());
        System.out.println("Существует 1111111111? " + accountRepo.existsById(new AccountNumber("1111111111")));
        System.out.println("Найден владелец: " + accountRepo.findById(new AccountNumber("1111111111")).getOwner());
        System.out.println();

        Repository<Long, Transaction> transactionRepo = new InMemoryRepository<>();

        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, new AccountNumber("1111111111"), 10000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, new AccountNumber("2222222222"), 500, TransactionStatus.SUCCESS);

        transactionRepo.save(tx1);
        transactionRepo.save(tx2);

        System.out.println("2. Transaction Repository:");
        System.out.println("Размер: " + transactionRepo.size());
        System.out.println("Существует ID=2? " + transactionRepo.existsById(2L));
        System.out.println("Найден тип операции: " + transactionRepo.findById(2L).type());
        System.out.println();

        System.out.println("Один интерфейс Repository успешно работает с двумя разными типами сущностей и ID!");
    }
}