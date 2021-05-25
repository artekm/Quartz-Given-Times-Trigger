package pl.artur.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.matchers.GroupMatcher;

@Slf4j
public class ResumeJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            context.getScheduler().resumeJobs(GroupMatcher.groupEquals("WORK"));
            log.info("jobs are resumed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
