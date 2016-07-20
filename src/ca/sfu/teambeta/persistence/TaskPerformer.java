package ca.sfu.teambeta.persistence;

import org.quartz.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by constantin on 19/07/16.
 */
public class TaskPerformer {
    private static final String JOB_NAME = "timeSlotDistribution";
    private static final String GROUP_NAME = "group";
    private static final String TRIGGER_NAME = "trigger";
    // every Thursday at 6:00 pm
    private static final String DEFAULT_SCHEDULE =  "0 0 18 ? * THU";
    // every Minute
    private static final String TEST_SCHEDULE =  "0 0/1 * * * ?";

    private DBManager dbManager;
    private String schedule;
    private Trigger trigger;
    private JobDetail job;


    public TaskPerformer(DBManager dbManager) {
        this.dbManager = dbManager;
        schedule = DEFAULT_SCHEDULE;
        job = newJob(Task.class)
                .withIdentity(JOB_NAME, GROUP_NAME)
                .build();
        perform();
    }

//    public void setFrequency(String schedule){
//        //TODO make it more friendly
//        this.schedule = schedule;
//        perform();
//    }

    private void perform() {
        trigger = newTrigger()
                .withIdentity(TRIGGER_NAME, GROUP_NAME)
                .withSchedule(cronSchedule(schedule))
                .forJob(job)
                .build();
    }

    private class Task implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            dbManager.performTimeDistribution();
        }
    }
}