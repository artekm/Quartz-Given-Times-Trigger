package pl.artur.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

@SpringBootApplication
public class QuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzApplication.class, args);
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(SimpleJob.class)
                         .storeDurably()
                         .withIdentity("JOB")
                         .withDescription("Sample job")
                         .build();
    }

    @Bean
    public Trigger trigger2(JobDetail jobDetail) {
        List<LocalTime> fireTimes = List.of(LocalTime.of(14, 02), LocalTime.of(14, 03), LocalTime.of(14, 04));
        EnumSet<DayOfWeek> fireDays = EnumSet.of(SATURDAY, SUNDAY);

        return TriggerBuilder.newTrigger().forJob(jobDetail)
                             .withIdentity("TRIGGER")
                             .withDescription("Sample trigger")
                             .withSchedule(GivenTimesScheduleBuilder.givenTimesSchedule()
                                                                    .withFireTimes(fireTimes)
                                                                    .withFireDays(fireDays))
                             .build();
    }
}