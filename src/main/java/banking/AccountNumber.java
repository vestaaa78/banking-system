package banking;

public record AccountNumber(String value) {

    public AccountNumber(String value) {
        if (value == null || !value.matches("\\d{10}")) {
            throw new IllegalArgumentException("Account number must contain exactly 10 digits");
        }
        this.value = value;
    }
}