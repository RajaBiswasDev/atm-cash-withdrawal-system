package atm.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atm.strategy.CashDispenserStrategy;
import atm.exception.ATMException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class ATM {
    private static final Logger logger = LoggerFactory.getLogger(ATM.class);
    private final Map<Money, Integer> denominations;
    private final CashDispenserStrategy dispenserStrategy;
    private final ReentrantLock lock = new ReentrantLock(true);

    public ATM(Map<Money, Integer> initialDenominations, CashDispenserStrategy dispenserStrategy) {
        this.denominations = new TreeMap<>(Comparator.naturalOrder());
        if (initialDenominations != null) {
            for (var entry : initialDenominations.entrySet()) {
                Money denominationValue = entry.getKey();
                int count = entry.getValue();
                if (denominationValue != null && count >= 0) {
                    this.denominations.put(denominationValue, count);
                }
            }
        }
        this.dispenserStrategy = dispenserStrategy;
    }

    /**
     * Withdraws the specified amount using the minimum number of notes.
     * @param amount The amount to withdraw.
     * @return Map of denomination to count dispensed.
     * @throws ATMException if the amount cannot be dispensed.
     */
    public Map<Money, Integer> withdraw(int amount) throws ATMException {
        lock.lock();
        try {
            Map<Money, Integer> availableCopy = new HashMap<>(denominations);
            Map<Money, Integer> toDispense = dispenserStrategy.dispense(amount, availableCopy);

            // Check and update denominations
            for (var entry : toDispense.entrySet()) {
                Money denominationValue = entry.getKey();
                int count = entry.getValue();
                int availableCount = denominations.getOrDefault(denominationValue, 0);
                if (count > availableCount) {
                    logger.error("Insufficient notes for denomination: {}", denominationValue);
                    throw new ATMException("Insufficient notes for denomination: " + denominationValue);
                }
            }
            // Update ATM state
            for (var entry : toDispense.entrySet()) {
                Money denominationValue = entry.getKey();
                int count = entry.getValue();
                denominations.put(denominationValue, denominations.get(denominationValue) - count);
            }
            logger.info("Dispensed amount {} with notes: {}", amount, toDispense);
            return toDispense;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Loads notes into the ATM for a given denomination.
     * @param denomination The denomination to load.
     * @param count The number of notes to add.
     */
    public void loadDenomination(Money denomination, int count) {
        if (denomination == null || count <= 0) {
            logger.error("Invalid denomination or count: denomination={}, count={}", denomination, count);
            throw new IllegalArgumentException("Invalid denomination or count.");
        }
        lock.lock();
        try {
            denominations.merge(denomination, count, Integer::sum);
            logger.info("Loaded denomination {} with count {}", denomination, count);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a copy of the available denominations and their counts.
     * @return Map of denomination to available count.
     */
    public Map<Money, Integer> getAvailableDenominations() {
        lock.lock();
        try {
            return new TreeMap<>(denominations);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets the ATM to the specified denominations (for testing).
     * @param newDenominations The new denominations to set.
     */
    public void reset(Map<Money, Integer> newDenominations) {
        lock.lock();
        try {
            denominations.clear();
            if (newDenominations != null) {
                for (var entry : newDenominations.entrySet()) {
                    Money denominationValue = entry.getKey();
                    int count = entry.getValue();
                    if (denominationValue != null && count >= 0) {
                        denominations.put(denominationValue, count);
                    }
                }
            }
            logger.info("ATM reset with new denominations: {}", newDenominations);
        } finally {
            lock.unlock();
        }
    }
}
