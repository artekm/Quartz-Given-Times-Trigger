package pl.artur.quartz;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.DailyTimeIntervalTriggerImpl;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;

@Component
@Slf4j
public class SimpleJob implements Job {
    @SneakyThrows
    public void execute(JobExecutionContext context) {
        System.out.println("Job is executing!!!!!");
        context.getScheduler().pauseJob(context.getJobDetail().getKey());
        System.out.println("Next fire time: " + context.getTrigger().getNextFireTime());
//        trigger = rescheduleForTommorow(context);
//        System.out.println("New next fire time: " + trigger.getNextFireTime());
    }

    private Trigger rescheduleForTommorow(JobExecutionContext context) {
        GivenTimesTrigger trigger = (GivenTimesTrigger) context.getTrigger();
        TriggerBuilder<GivenTimesTrigger> triggerBuilder = trigger.getTriggerBuilder();
        GivenTimesTrigger newTrigger = triggerBuilder.build();
        newTrigger.setStartTime(Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay()));
        try {
            context.getScheduler().rescheduleJob(trigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            log.warn("Rescheduling failed");
        }
        return newTrigger;
    }
}
