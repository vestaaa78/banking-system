package banking.demo;

import banking.application.BankAccountUtils;
import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.DebitAccount;

import java.util.ArrayList;
import java.util.List;

public class PecsCopyDemo {

    public static void main(String[] args) {
        List<DebitAccount> debits = new ArrayList<>();
        debits.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 1000));

        List<BankAccount> accounts = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        BankAccountUtils.copy(debits, accounts);
        BankAccountUtils.copy(debits, objects);
        BankAccountUtils.copy(accounts, accounts);

        System.out.println("Все разрешённые операции копирования выполнены успешно!");

    }
}