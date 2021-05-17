package pl.artur.quartz;

import org.quartz.ScheduleBuilder;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerPersistenceDelegateSupport;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerProperties;
import org.quartz.spi.OperableTrigger;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class GivenTimesPersistenceDelegate extends SimplePropertiesTriggerPersistenceDelegateSupport {

    @Override
    protected SimplePropertiesTriggerProperties getTriggerProperties(OperableTrigger trigger) {

        GivenTimesTriggerImpl calTrig = (GivenTimesTriggerImpl) trigger;

        SimplePropertiesTriggerProperties props = new SimplePropertiesTriggerProperties();

        props.setString1(calTrig.getFireTimes().stream().map(LocalTime::toString).collect(Collectors.joining(",")));
        props.setString2(calTrig.getFireDays().stream().map(Enum::toString).collect(Collectors.joining(",")));

        props.setBoolean1(calTrig.continueToday());
        return props;
    }

    @Override
    protected TriggerPropertyBundle getTriggerPropertyBundle(SimplePropertiesTriggerProperties properties) {
        List<LocalTime> fireTimes = Arrays.stream(properties.getString1().split(","))
                                          .map(LocalTime::parse)
                                          .collect(Collectors.toList());
        EnumSet<DayOfWeek> fireDays = Arrays.stream(properties.getString2().split(","))
                                            .map(DayOfWeek::valueOf)
                                            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));

        ScheduleBuilder<?> sb = GivenTimesScheduleBuilder.givenTimesSchedule()
                                                         .withFireDays(fireDays)
                                                         .withFireTimes(fireTimes);

        boolean continueToday = properties.isBoolean1();

        String[] statePropertyNames = {"continueToday"};
        Object[] statePropertyValues = {continueToday};
        return new TriggerPropertyBundle(sb, statePropertyNames, statePropertyValues);
    }

    @Override
    public boolean canHandleTriggerType(OperableTrigger trigger) {
        return ((trigger instanceof GivenTimesTriggerImpl) && !((GivenTimesTriggerImpl) trigger)
                .hasAdditionalProperties());
    }

    @Override
    public String getHandledTriggerTypeDiscriminator() {
        return "GIVEN_TIME";
    }
}
