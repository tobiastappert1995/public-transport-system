package de.tappert.publictransportsystem.domain.strategy;

import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class HolidayTicketPricingStrategy implements TicketPricingStrategy {

    @Override
    public Money calculatePrice(TripExecution tripExecution) {
        Money basePrice = tripExecution.getTrip().getFare().getPrice();

        return Money.of(
                basePrice.getAmount()
                        .multiply(new BigDecimal("1.10"))
                        .setScale(2, RoundingMode.HALF_UP),
                basePrice.getCurrency()
        );
    }
}
