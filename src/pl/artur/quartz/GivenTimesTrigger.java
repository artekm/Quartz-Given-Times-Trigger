package pl.artur.quartz;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

public interface GivenTimesTrigger extends Trigger {
    public static final long serialVersionUID = 123456L;
    public static final int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;
    public static final int MISFIRE_INSTRUCTION_DO_NOTHING = 2;

    void setContinueToday(boolean continueToday);
    boolean continueToday();
    List<LocalTime> getFireTimes();
    void setFireTimes(List<LocalTime> fireTimes);
    EnumSet<DayOfWeek> getFireDays();
    void setFireDays(EnumSet<DayOfWeek> fireDays);
    TriggerBuilder<GivenTimesTrigger> getTriggerBuilder();
}
