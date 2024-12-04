package database.enums;

import java.time.Period;

/**
 * Represents the recurrence rate for recurring transactions.
 * Provides methods to retrieve the interval duration and custom string representations.
 */
public enum RecurringRate {
    WEEKLY,
    BI_WEEKLY {
        @Override
        public String toString() {
            return "bi-weekly";
        }
    },
    MONTHLY,
    QUARTERLY,
    YEARLY;

    /**
     * Returns a lowercase string representation of the recurrence rate.
     * For BI_WEEKLY, a custom string "bi-weekly" is returned.
     *
     * @return the string representation of the recurrence rate.
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Retrieves the interval duration for each recurrence rate as a {@link Period}.
     *
     * @return the interval as a {@link Period}.
     */
    public Period getInterval() {
        return switch (this) {
            case WEEKLY -> Period.ofWeeks(1);
            case BI_WEEKLY -> Period.ofWeeks(2);
            case MONTHLY -> Period.ofMonths(1);
            case QUARTERLY -> Period.ofMonths(3);
            case YEARLY -> Period.ofYears(1);
        };
    }
}
