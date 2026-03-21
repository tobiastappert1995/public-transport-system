package de.tappert.publictransportsystem.domain.strategy;

import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.valueobject.Money;

/**
 * Strategy interface for calculating the price of a ticket
 * for a given trip execution.
 */
public interface TicketPricingStrategy {
    
    /**
     * Calculates the ticket price for the given trip execution.
     */
    Money calculatePrice(TripExecution tripExecution);
}
