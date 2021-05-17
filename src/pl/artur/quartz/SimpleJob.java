package pl.artur.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class SimpleJob implements Job {
    public void execute(JobExecutionContext context) {
        System.out.println("Job is executing!!!!!");
        System.out.println("Next fire time: " + context.getTrigger().getNextFireTime());
    }
}
