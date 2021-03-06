package pl.artur.quartz;

import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.Trigger.MISFIRE_INSTRUCTION_SMART_POLICY;
import static pl.artur.quartz.GivenTimesTrigger.ANY_DAY;

public class GivenTimesScheduleBuilder extends ScheduleBuilder<GivenTimesTrigger> {
    private List<LocalTime> fireTimes;
    private List<DayOfWeek> fireDays = ANY_DAY;
    private int misfireInstruction = MISFIRE_INSTRUCTION_SMART_POLICY;

    protected GivenTimesScheduleBuilder() {
        super();
    }

    public static GivenTimesScheduleBuilder givenTimesSchedule() {
        return new GivenTimesScheduleBuilder();
    }

    public GivenTimesScheduleBuilder withFireTimes(List<LocalTime> fireTimes) {
        this.fireTimes = fireTimes;
        return this;
    }

    public GivenTimesScheduleBuilder withFireTimes(String... fireTimes) {
        this.fireTimes = Arrays.stream(fireTimes).map(LocalTime::parse).collect(Collectors.toList());
        return this;
    }

    public GivenTimesScheduleBuilder withFireDays(List<DayOfWeek> fireDays) {
        this.fireDays = fireDays;
        return this;
    }

    public GivenTimesScheduleBuilder withMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
        return this;
    }

    @Override
    protected MutableTrigger build() {
        if ((fireTimes == null) || (fireTimes.isEmpty())) {
            throw new RuntimeException("Fire times list must not be null nor empty");
        }
        if ((fireDays == null) || (fireDays.isEmpty())) {
            throw new RuntimeException("Fire days list must not be null nor empty");
        }
        GivenTimesTrigger trigger = new GivenTimesTrigger();
        trigger.setFireTimes(this.fireTimes);
        trigger.setFireDays(this.fireDays);
        trigger.setMisfireInstruction(this.misfireInstruction);
        return trigger;
    }
}
