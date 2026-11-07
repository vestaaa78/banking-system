package banking.test;

import banking.CommissionPolicy;
import banking.FixedCommissionPolicy;
import banking.ProgressiveCommissionPolicy;
import banking.WeekendCommissionPolicy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class CommissionPoliciesTest {
    @Test
    void fixedCommissionAlwaysReturnsSameAmount() {
        CommissionPolicy policy = new FixedCommissionPolicy(50.0);

        assertEquals(50.0, policy.calculate(100));
        assertEquals(50.0, policy.calculate(10000));
        assertEquals(50.0, policy.calculate(1000000));
    }

    @Test
    void fixedCommissionZeroIsAllowed() {
        CommissionPolicy policy = new FixedCommissionPolicy(0);
        assertEquals(0.0, policy.calculate(5000));
    }

    @Test
    void fixedCommissionNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new FixedCommissionPolicy(-10));
    }

    @Test
    void progressiveCommissionAppliesLowRateForSmallAmount() {
        CommissionPolicy policy = new ProgressiveCommissionPolicy(
                10_000, 50_000,
                0.0, 0.01, 0.02
        );

        assertEquals(0.0, policy.calculate(5000), "Малая сумма — 0%");
        assertEquals(0.0, policy.calculate(10_000), "Граница — 0%");
    }

    @Test
    void progressiveCommissionAppliesMiddleRateForMediumAmount() {
        CommissionPolicy policy = new ProgressiveCommissionPolicy(
                10_000, 50_000,
                0.0, 0.01, 0.02
        );

        assertEquals(300.0, policy.calculate(30_000), "Средняя сумма — 1%");
        assertEquals(500.0, policy.calculate(50_000), "Граница — 1%");
    }

    @Test
    void progressiveCommissionAppliesHighRateForLargeAmount() {
        CommissionPolicy policy = new ProgressiveCommissionPolicy(
                10_000, 50_000,
                0.0, 0.01, 0.02
        );

        assertEquals(2000.0, policy.calculate(100_000), "Большая сумма — 2%");
    }

    @Test
    void progressiveCommissionInvalidThresholdsThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProgressiveCommissionPolicy(50_000, 10_000, 0.0, 0.01, 0.02));
    }

    @Test
    void weekendCommissionAppliesWeekendPolicyOnSaturday() {
        Clock saturdayClock = Clock.fixed(
                Instant.parse("2026-11-07T12:00:00Z"),
                ZoneId.of("UTC")
        );

        CommissionPolicy weekday = amount -> amount * 0.01;
        CommissionPolicy weekend = amount -> amount * 0.03;

        CommissionPolicy policy = new WeekendCommissionPolicy(weekday, weekend, saturdayClock);

        assertEquals(300.0, policy.calculate(10_000), "В субботу — 3%");
    }

    @Test
    void weekendCommissionAppliesWeekendPolicyOnSunday() {
        Clock sundayClock = Clock.fixed(
                Instant.parse("2026-11-08T12:00:00Z"),
                ZoneId.of("UTC")
        );

        CommissionPolicy weekday = amount -> amount * 0.01;
        CommissionPolicy weekend = amount -> amount * 0.03;

        CommissionPolicy policy = new WeekendCommissionPolicy(weekday, weekend, sundayClock);

        assertEquals(300.0, policy.calculate(10_000), "В воскресенье — 3%");
    }

    @Test
    void weekendCommissionAppliesWeekdayPolicyOnMonday() {
        Clock mondayClock = Clock.fixed(
                Instant.parse("2026-11-09T12:00:00Z"),
                ZoneId.of("UTC")
        );

        CommissionPolicy weekday = amount -> amount * 0.01;
        CommissionPolicy weekend = amount -> amount * 0.03;

        CommissionPolicy policy = new WeekendCommissionPolicy(weekday, weekend, mondayClock);

        assertEquals(100.0, policy.calculate(10_000), "В понедельник — 1%");
    }

    @Test
    void weekendCommissionWithSystemClock() {
        CommissionPolicy policy = new WeekendCommissionPolicy(
                amount -> amount * 0.01,
                amount -> amount * 0.03,
                Clock.systemDefaultZone()
        );

        double commission = policy.calculate(10_000);
        assertTrue(commission >= 0);
    }

    @Test
    void weekendCommissionNullArgumentsThrowException() {
        CommissionPolicy weekday = amount -> 0.0;
        CommissionPolicy weekend = amount -> 0.0;
        Clock clock = Clock.systemDefaultZone();

        assertThrows(IllegalArgumentException.class,
                () -> new WeekendCommissionPolicy(null, weekend, clock));
        assertThrows(IllegalArgumentException.class,
                () -> new WeekendCommissionPolicy(weekday, null, clock));
        assertThrows(IllegalArgumentException.class,
                () -> new WeekendCommissionPolicy(weekday, weekend, null));
    }
}