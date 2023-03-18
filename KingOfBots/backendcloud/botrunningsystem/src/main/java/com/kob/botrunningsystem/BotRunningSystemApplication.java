package com.kob.botrunningsystem;


import com.kob.botrunningsystem.service.Impl.BotRunningServiceImpl;
import com.kob.botrunningsystem.service.Impl.utils.BotPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceImpl.botpool.start();
        SpringApplication.run(BotRunningSystemApplication.class,args);
    }
}