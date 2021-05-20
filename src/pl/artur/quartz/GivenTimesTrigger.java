package pl.artur.quartz;

import org.quartz.Calendar;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CoreTrigger;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;

public class GivenTimesTrigger extends AbstractTrigger<GivenTimesTrigger> implements Trigger, CoreTrigger {
    private static final long serialVersionUID = 1234567890L;
    public static final int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;
    public static final int MISFIRE_INSTRUCTION_DO_NOTHING = 2;

    private Date startTime;
    private Date nextFireTime;
    private Date previousFireTime;

    private List<LocalTime> fireTimes;
    private List<DayOfWeek> fireDays;

    private boolean continueToday = true;

    public GivenTimesTrigger() {
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
        LocalTime lastTimeDaily = fireTimes.get(fireTimes.size() - 1);

        //continueToday flag is cleared
        //or
        //time is after last time a day
        if (!continueToday || !time.isBefore(lastTimeDaily)) {
            do {
                date = date.plusDays(1);
            } while (!fireDays.contains(date.getDayOfWeek()));
            return toDate(LocalDateTime.of(date, firstTimeDaily));
        }

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
        return null;
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

    public void setContinueToday(boolean continueToday) {
        this.continueToday = continueToday;
    }

    public boolean continueToday() {
        return continueToday;
    }

    public List<LocalTime> getFireTimes() {
        return fireTimes;
    }

    public void setFireTimes(List<LocalTime> fireTimes) {
        if (fireTimes != null) {
            this.fireTimes = fireTimes;
        }
    }

    public List<DayOfWeek> getFireDays() {
        return fireDays;
    }

    public void setFireDays(List<DayOfWeek> fireDays) {
        if (fireDays != null) {
            this.fireDays = fireDays;
        }
    }

    private LocalDateTime toLocalDateTime(Date dateToConvert) {
        return LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
    }

    private Date toDate(LocalDateTime dateToConvert) {
        return Timestamp.valueOf(dateToConvert);
    }
}
