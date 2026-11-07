package banking;

public final class FixedCommissionPolicy implements CommissionPolicy {

    private final double fixedAmount;

    public FixedCommissionPolicy(double fixedAmount) {
        if (fixedAmount < 0) {
            throw new IllegalArgumentException("Fixed commission cannot be negative");
        }
        this.fixedAmount = fixedAmount;
    }

    @Override
    public double calculate(double amount) {
        return fixedAmount;
    }
}