package de.tappert.publictransportsystem.domain.strategy;

import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class WeekdayTicketPricingStrategy implements TicketPricingStrategy {

    @Override
    public Money calculatePrice(TripExecution tripExecution) {
        return tripExecution.getTrip().getFare().getPrice();
    }
}
