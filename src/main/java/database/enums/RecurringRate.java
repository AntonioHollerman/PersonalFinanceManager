package database.enums;

import java.time.Period;

public enum RecurringRate {
    WEEKLY, BI_WEEKLY, MONTHLY, QUARTERLY, YEARLY;
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
