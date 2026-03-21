package de.tappert.publictransportsystem.config;

import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.strategy.HolidayTicketPricingStrategy;
import de.tappert.publictransportsystem.domain.strategy.TicketPricingStrategy;
import de.tappert.publictransportsystem.domain.strategy.WeekdayTicketPricingStrategy;
import de.tappert.publictransportsystem.domain.strategy.WeekendTicketPricingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TicketPricingStrategyConfig {

    @Bean
    public Map<DayType, TicketPricingStrategy> ticketPricingStrategies(
            WeekdayTicketPricingStrategy weekdayTicketPricingStrategy,
            WeekendTicketPricingStrategy weekendTicketPricingStrategy,
            HolidayTicketPricingStrategy holidayTicketPricingStrategy
    ) {
        return Map.of(
                DayType.WEEKDAY, weekdayTicketPricingStrategy,
                DayType.WEEKEND, weekendTicketPricingStrategy,
                DayType.HOLIDAY, holidayTicketPricingStrategy
        );
    }
}