package pl.artur.quartz;

import org.quartz.Calendar;
import org.quartz.ScheduleBuilder;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CoreTrigger;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GivenTimesTriggerImpl extends AbstractTrigger<GivenTimesTrigger> implements GivenTimesTrigger, CoreTrigger {
    private static final long serialVersionUID = 1234567890L;

    private Date startTime;
    private Date nextFireTime;
    private Date previousFireTime;

    private List<LocalTime> fireTimes;
    private List<DayOfWeek> fireDays;

    private boolean continueToday = true;

    public GivenTimesTriggerImpl() {
        super();
        startTime = new Date();
    }

    @Override
    public void triggered(Calendar calendar) {
        previousFireTime = nextFireTime;
        nextFireTime = getFireTimeAfter(nextFireTime, calendar);
    }

    @Override
    public Date computeFirstFireTime(Calendar calendar) {
        nextFireTime = getFireTimeAfter(startTime, calendar);
        return nextFireTime;
    }

    @Override
    public boolean mayFireAgain() {
        return (nextFireTime != null);
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Date startTime) {
        this.startTime = toDate(toLocalDateTime(startTime).withNano(0));
    }

    @Override
    public void setEndTime(Date endTime) {
        // do nothing, runs forever
    }

    @Override
    public Date getEndTime() {
        return null; // no such field - runs forever
    }

    @Override
    public Date getNextFireTime() {
        return this.nextFireTime;
    }

    @Override
    public Date getPreviousFireTime() {
        return this.previousFireTime;
    }

    public Date getFireTimeAfter(Date fireTime, Calendar calendar) {
        Date hardStop = toDate(LocalDateTime.now().plusYears(10));
        do {
            fireTime = getFireTimeAfter(fireTime);
        } while (fireTime != null && fireTime.before(hardStop) &&
                calendar != null && !calendar.isTimeIncluded(fireTime.getTime()));
        return fireTime;
    }

    @Override
    public Date getFireTimeAfter(Date afterTime) {
        if (afterTime == null) {
            return null;
        }
        if (afterTime.before(startTime)) {
            afterTime = startTime;
        }
        LocalDateTime dateTime = toLocalDateTime(afterTime).withNano(0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        LocalTime firstTimeDaily = fireTimes.get(0);

        //time is before first time a day
        if (time.isBefore(firstTimeDaily)) {
            return toDate(LocalDateTime.of(date, firstTimeDaily));
        }

        //time is between first and last time a day
        for (LocalTime fireTime : fireTimes) {
            if (time.isBefore(fireTime)) {
                return toDate(LocalDateTime.of(date, fireTime));
            }
        }

        //time is after last time a day
        do {
            date = date.plusDays(1);
        } while (!fireDays.contains(date.getDayOfWeek()));
        return toDate(LocalDateTime.of(date, firstTimeDaily));
    }

    @Override
    public Date getFinalFireTime() {
        return null; // runs forever
    }

    @Override
    protected boolean validateMisfireInstruction(int misfireInstruction) {
        return misfireInstruction >= MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY && misfireInstruction <= MISFIRE_INSTRUCTION_DO_NOTHING;
    }

    @Override
    public void updateAfterMisfire(Calendar cal) {
        switch (getMisfireInstruction()) {
            case MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                return;
            case MISFIRE_INSTRUCTION_SMART_POLICY:
            case MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                nextFireTime = new Date();
                break;
            case MISFIRE_INSTRUCTION_DO_NOTHING:
                nextFireTime = getFireTimeAfter(new Date(), cal);
        }
    }

    @Override
    public void updateWithNewCalendar(Calendar calendar, long misfireThreshold) {
        nextFireTime = getFireTimeAfter(previousFireTime, calendar);
    }

    @Override
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    @Override
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    @Override
    public ScheduleBuilder<GivenTimesTrigger> getScheduleBuilder() {
        return GivenTimesScheduleBuilder.givenTimesSchedule()
                                        .withFireTimes(getFireTimes())
                                        .withFireDays(getFireDays())
                                        .withMisfireInstruction(getMisfireInstruction());
    }

    @Override
    public boolean hasAdditionalProperties() {
        return false;
    }

    @Override
    public void setContinueToday(boolean continueToday) {
        this.continueToday = continueToday;
    }

    @Override
    public boolean continueToday() {
        return continueToday;
    }

    @Override
    public List<LocalTime> getFireTimes() {
        return fireTimes.stream()
                        .map(LocalTime::from)
                        .collect(Collectors.toList());
    }

    @Override
    public void setFireTimes(List<LocalTime> fireTimes) {
        if (fireTimes != null) {
            this.fireTimes = fireTimes.stream()
                                      .map(LocalTime::from)
                                      .collect(Collectors.toList());
            this.fireTimes.sort(LocalTime::compareTo);
        }
    }

    @Override
    public List<DayOfWeek> getFireDays() {
        return fireDays.stream().map(DayOfWeek::from).collect(Collectors.toList());

    }

    @Override
    public void setFireDays(List<DayOfWeek> fireDays) {
        if (fireDays != null) {
            this.fireDays = fireDays.stream().map(DayOfWeek::from).collect(Collectors.toList());
        }
    }

    private LocalDateTime toLocalDateTime(Date dateToConvert) {
        return LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
    }

    private Date toDate(LocalDateTime dateToConvert) {
        return Timestamp.valueOf(dateToConvert);
    }
}
