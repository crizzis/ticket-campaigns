package org.example.ticketcampaign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    @Bean
    public Clock defaultClock() {
        return Clock.systemDefaultZone();
    }
}
