package banking.adapters;

import banking.domain.CommissionPolicy;

public class NoCommission implements CommissionPolicy {

    @Override
    public double calculate(double amount) {
        return 0;
    }
}