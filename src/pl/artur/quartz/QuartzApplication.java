package pl.artur.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

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
    public static ConversionService conversionService() {
        // Ten konwerter jest potrzebny, bo domyslny springowy nie umie czytać LocalTime z sekundami
        FormattingConversionService reg = new DefaultFormattingConversionService();
        reg.addConverter(new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return LocalTime.parse(source);
            }
        }); // z jakiegoś tajemniczego powodu przestaje działać po zmianie na lambdę, więc zostaje jako klasa anonimowa
        return reg;
    }

    @Bean("workJob")
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(SimpleJob.class)
                         .storeDurably()
                         .withIdentity("JOB", "WORK")
                         .withDescription("Sample job")
                         .build();
    }

    @Bean("resumerJob")
    public JobDetail jobResumerDetail() {
        return JobBuilder.newJob().ofType(ResumeJob.class)
                         .storeDurably()
                         .withIdentity("RESUMER", "WORK")
                         .withDescription("Sample job resumer")
                         .build();
    }

    @Bean
    public Trigger trigger(@Qualifier("workJob") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                             .withIdentity("TRIGGER", "WORK")
                             .withDescription("Sample trigger")
                             .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")
                                                              .withMisfireHandlingInstructionDoNothing())
                             .build();
    }

    @Bean
    public Trigger resumeTrigger(@Qualifier("resumerJob") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                             .withIdentity("RESUMER", "WORK")
                             .withDescription("Resumes paused job every evening")
                             .withSchedule(GivenTimesScheduleBuilder.givenTimesSchedule()
                                                                    .withFireTimes("20:10","20:30"))
                             .build();
    }
}