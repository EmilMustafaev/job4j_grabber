package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = AlertRabbit.class.
                getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static Connection getConnection(Properties properties) throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        try {
            Properties properties = loadProperties();
            int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
            Connection connection = getConnection(properties);

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);

            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.
                    getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.
                    prepareStatement("INSERT INTO rabbit(created_date) VALUES(?)")) {
                statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}