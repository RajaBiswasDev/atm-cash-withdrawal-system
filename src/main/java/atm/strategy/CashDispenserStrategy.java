package atm.strategy;

import atm.domain.Money;
import atm.exception.ATMException;

import java.util.Map;

/**
 * Strategy interface for dispensing cash.
 */
public sealed interface CashDispenserStrategy permits GreedyCashDispenserStrategy {
    /**
     * Dispense the specified amount using available denominations.
     * @param amount The amount to dispense.
     * @param availableDenominations Map of denomination (Money) to available count.
     * @return Map of denomination (Money) to count dispensed.
     * @throws ATMException if the amount cannot be dispensed.
     */
    Map<Money, Integer> dispense(int amount, Map<Money, Integer> availableDenominations) throws ATMException;
}
