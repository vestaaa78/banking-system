package banking.domain;

public interface CommissionPolicy {
    double calculate(double amount);
}