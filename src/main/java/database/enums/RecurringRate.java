package database.enums;

import java.time.Period;

public enum RecurringRate {
    WEEKLY, BI_WEEKLY{
        @Override
        public String toString() {
            return "bi-weekly";
        }
    }, MONTHLY, QUARTERLY, YEARLY;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public Period getInterval(){
        return switch (this){
            case WEEKLY -> Period.ofWeeks(1);
            case BI_WEEKLY -> Period.ofWeeks(2);
            case MONTHLY -> Period.ofMonths(1);
            case QUARTERLY -> Period.ofMonths(3);
            case YEARLY -> Period.ofYears(1);
        };
    }
}
