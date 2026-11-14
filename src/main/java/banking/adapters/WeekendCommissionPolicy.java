package banking.adapters;

import banking.domain.CommissionPolicy;

import java.time.Clock;
import java.time.DayOfWeek;

public final class WeekendCommissionPolicy implements CommissionPolicy {

    private final CommissionPolicy weekdayPolicy;
    private final CommissionPolicy weekendPolicy;
    private final Clock clock;

    public WeekendCommissionPolicy(
            CommissionPolicy weekdayPolicy,
            CommissionPolicy weekendPolicy,
            Clock clock
    ) {
        if (weekdayPolicy == null || weekendPolicy == null || clock == null) {
            throw new IllegalArgumentException("Policies and clock cannot be null");
        }
        this.weekdayPolicy = weekdayPolicy;
        this.weekendPolicy = weekendPolicy;
        this.clock = clock;
    }

    @Override
    public double calculate(double amount) {
        DayOfWeek dayOfWeek = clock.instant().atZone(clock.getZone()).getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return weekendPolicy.calculate(amount);
        } else {
            return weekdayPolicy.calculate(amount);
        }
    }
}