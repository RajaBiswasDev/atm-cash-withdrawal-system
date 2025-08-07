package atm.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atm.domain.Money;
import atm.exception.ATMException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Greedy implementation of CashDispenserStrategy.
 * Dispenses the minimum number of notes for a given amount.
 */
public final class GreedyCashDispenserStrategy implements CashDispenserStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GreedyCashDispenserStrategy.class);

    @Override
    public Map<Money, Integer> dispense(int amount, Map<Money, Integer> availableDenominations) throws ATMException {
        if (amount <= 0 || amount % 10 != 0) {
            logger.error("Invalid amount requested: {}", amount);
            throw new ATMException("Amount must be a positive multiple of 10.");
        }
        // Sort denominations in descending order (Money.compareTo is descending)
        List<Money> denominations = new ArrayList<>(availableDenominations.keySet());
        denominations.sort(Comparator.naturalOrder());

        Map<Money, Integer> result = new LinkedHashMap<>();
        int remaining = amount;

        for (Money denomination : denominations) {
            int denominationValue = denomination.value();
            int available = availableDenominations.getOrDefault(denomination, 0);
            if (denominationValue <= 0 || denominationValue % 10 != 0) continue; // skip invalid denominations

            int count = Math.min(remaining / denominationValue, available);
            if (count > 0) {
                result.put(denomination, count);
                remaining -= denominationValue * count;
            }
        }

        if (remaining != 0) {
            logger.error("Cannot dispense requested amount {} with available denominations: {}", amount, availableDenominations);
            throw new ATMException("Cannot dispense the requested amount with available denominations.");
        }

        logger.info("Dispensed amount {} with notes: {}", amount, result);
        return result;
    }
}
