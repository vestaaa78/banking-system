package banking;

public class PercentCommission implements CommissionPolicy {

    private final double percent;

    public PercentCommission(double percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("Процент комиссии не может быть отрицательным");
        }
        this.percent = percent;
    }

    @Override
    public double calculate(double amount) {
        return amount * percent / 100.0;
    }
}