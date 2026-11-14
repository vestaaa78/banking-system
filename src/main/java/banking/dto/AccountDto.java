
package banking.dto;

import banking.domain.BankAccount;

public record AccountDto(
        String number,
        String owner,
        double balance,
        String type
) {

    public static AccountDto fromDomain(BankAccount account) {
        return new AccountDto(
                account.getNumber().value(),
                account.getOwner(),
                account.getBalance(),
                account.getClass().getSimpleName()
        );
    }
}
