package pl.artur.quartz;

import org.quartz.ScheduleBuilder;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerPersistenceDelegateSupport;
import org.quartz.impl.jdbcjobstore.SimplePropertiesTriggerProperties;
import org.quartz.spi.OperableTrigger;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GivenTimesPersistenceDelegate extends SimplePropertiesTriggerPersistenceDelegateSupport {

    // You must order Quartz to load this class by adding to your application.yaml:
    // spring.quartz.properties.org.quartz.jobStore.driverDelegateInitString: triggerPersistenceDelegateClasses=GivenTimesPersistenceDelegate

    @Override
    protected SimplePropertiesTriggerProperties getTriggerProperties(OperableTrigger trigger) {

        GivenTimesTrigger calTrig = (GivenTimesTrigger) trigger;

        SimplePropertiesTriggerProperties props = new SimplePropertiesTriggerProperties();

        props.setString1(calTrig.getFireTimes().stream().map(LocalTime::toString).collect(Collectors.joining(",")));
        props.setString2(calTrig.getFireDays().stream().map(Enum::toString).collect(Collectors.joining(",")));

        props.setBoolean1(calTrig.continueToday());
        return props;
    }

    @Override
    protected TriggerPropertyBundle getTriggerPropertyBundle(SimplePropertiesTriggerProperties properties) {
        List<LocalTime> fireTimes = Stream.of(properties.getString1().split(","))
                                          .map(LocalTime::parse)
                                          .collect(Collectors.toList());
        List<DayOfWeek> fireDays = Stream.of(properties.getString2().split(","))
                                            .map(DayOfWeek::valueOf)
                                            .collect(Collectors.toList());

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
        return ((trigger instanceof GivenTimesTrigger) && !((GivenTimesTrigger) trigger)
                .hasAdditionalProperties());
    }

    @Override
    public String getHandledTriggerTypeDiscriminator() {
        return "GIVENTIME";
    }
}
