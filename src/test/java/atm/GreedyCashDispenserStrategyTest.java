package atm;

import org.junit.jupiter.api.Test;

import atm.domain.Money;
import atm.strategy.GreedyCashDispenserStrategy;
import atm.exception.ATMException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreedyCashDispenserStrategyTest {

    private final GreedyCashDispenserStrategy strategy = new GreedyCashDispenserStrategy();

    @Test
    void givenSufficientSingleDenomination_whenDispenseExactAmount_thenReturnCorrectNotes() throws ATMException {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(100), 10);

        // Act
        Map<Money, Integer> result = strategy.dispense(300, available);

        // Assert
        assertEquals(Map.of(new Money(100), 3), result);
    }

    @Test
    void givenMultipleDenominations_whenDispenseAmount_thenUseMinimumNotes() throws ATMException {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(2000), 1, new Money(500), 2, new Money(100), 5);

        // Act
        Map<Money, Integer> result = strategy.dispense(2700, available);

        // Assert
        assertEquals(Map.of(new Money(2000), 1, new Money(500), 1, new Money(100), 2), result);
    }

    @Test
    void givenInsufficientFunds_whenDispenseAmount_thenThrowATMException() {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(100), 2);

        // Act & Assert
        assertThrows(ATMException.class, () -> strategy.dispense(500, available));
    }

    @Test
    void givenImpossibleDenominationCombination_whenDispenseAmount_thenThrowATMException() {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(100), 2);

        // Act & Assert
        assertThrows(ATMException.class, () -> strategy.dispense(150, available));
    }

    @Test
    void givenInvalidAmount_whenDispense_thenThrowATMException() {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(100), 10);

        // Act & Assert
        assertThrows(ATMException.class, () -> strategy.dispense(0, available));
        assertThrows(ATMException.class, () -> strategy.dispense(-100, available));
        assertThrows(ATMException.class, () -> strategy.dispense(125, available));
    }

    @Test
    void givenInvalidDenominationsInMap_whenDispense_thenIgnoreInvalidDenominations() throws ATMException {
        // Arrange
        Map<Money, Integer> available = Map.of(new Money(100), 5);

        // Act
        Map<Money, Integer> result = strategy.dispense(200, available);

        // Assert
        assertEquals(Map.of(new Money(100), 2), result);
    }
}
