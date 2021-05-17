package pl.artur.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class QuartzApplication {

    @Value("${fire-days}")
    private List<DayOfWeek> fireDays;

    @Value("${fire-times}")
    private List<LocalTime> fireTimes;

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
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                             .withIdentity("TRIGGER")
                             .withDescription("Sample trigger")
                             .withSchedule(GivenTimesScheduleBuilder.givenTimesSchedule()
                                                                    .withFireTimes(fireTimes)
                                                                    .withFireDays(fireDays))
                             .build();
    }
}