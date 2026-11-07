package banking;

public final class ProgressiveCommissionPolicy implements CommissionPolicy {

    private final double firstThreshold;
    private final double secondThreshold;
    private final double lowRate;
    private final double middleRate;
    private final double highRate;

    public ProgressiveCommissionPolicy(
            double firstThreshold,
            double secondThreshold,
            double lowRate,
            double middleRate,
            double highRate
    ) {
        if (firstThreshold < 0 || secondThreshold < firstThreshold) {
            throw new IllegalArgumentException("Invalid thresholds");
        }
        if (lowRate < 0 || middleRate < 0 || highRate < 0) {
            throw new IllegalArgumentException("Rates cannot be negative");
        }
        this.firstThreshold = firstThreshold;
        this.secondThreshold = secondThreshold;
        this.lowRate = lowRate;
        this.middleRate = middleRate;
        this.highRate = highRate;
    }

    @Override
    public double calculate(double amount) {
        if (amount <= firstThreshold) {
            return amount * lowRate;
        } else if (amount <= secondThreshold) {
            return amount * middleRate;
        } else {
            return amount * highRate;
        }
    }
}