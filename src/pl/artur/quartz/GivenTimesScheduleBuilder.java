package pl.artur.quartz;

import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

import static org.quartz.Trigger.MISFIRE_INSTRUCTION_SMART_POLICY;

public class GivenTimesScheduleBuilder extends ScheduleBuilder<GivenTimesTrigger> {
    private List<LocalTime> fireTimes;
    private EnumSet<DayOfWeek> fireDays;
    private int misfireInstruction = MISFIRE_INSTRUCTION_SMART_POLICY;

    protected GivenTimesScheduleBuilder() {
        super();
    }

    public static GivenTimesScheduleBuilder givenTimesSchedule() {
        return new GivenTimesScheduleBuilder();
    }

    public GivenTimesScheduleBuilder withFireTimes(List<LocalTime> fireTimes) {
        if ((fireTimes == null) || (fireTimes.isEmpty())) {
            throw new RuntimeException("Fire times list must not be null nor empty");
        }
        this.fireTimes = fireTimes;
        return this;
    }

    public GivenTimesScheduleBuilder withFireDays(EnumSet<DayOfWeek> fireDays) {
        if ((fireDays == null) || (fireDays.isEmpty())) {
            throw new RuntimeException("Fire days list must not be null nor empty");
        }
        this.fireDays = fireDays;
        return this;
    }

    public GivenTimesScheduleBuilder withMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
        return this;
    }

    @Override
    protected MutableTrigger build() {
        GivenTimesTriggerImpl trigger = new GivenTimesTriggerImpl();
        trigger.setFireTimes(this.fireTimes);
        trigger.setFireDays(this.fireDays);
        trigger.setMisfireInstruction(this.misfireInstruction);
        return trigger;
    }
}
