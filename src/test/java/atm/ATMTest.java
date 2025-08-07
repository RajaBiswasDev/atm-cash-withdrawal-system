package atm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import atm.domain.ATM;
import atm.domain.Money;
import atm.strategy.GreedyCashDispenserStrategy;
import atm.exception.ATMException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ATMTest {

    private ATM atm;

    @BeforeEach
    void setUp() {
        Map<Money, Integer> initial = Map.of(
                new Money(2000), 10,
                new Money(500), 20,
                new Money(100), 100
        );
        atm = new ATM(initial, new GreedyCashDispenserStrategy());
    }

    @Test
    void givenSufficientSingleDenomination_whenWithdrawExactAmount_thenDispenseCorrectNotes() throws ATMException {
        // Act
        Map<Money, Integer> result = atm.withdraw(2000);

        // Assert
        assertEquals(Map.of(new Money(2000), 1), result);
    }

    @Test
    void givenSufficientMultipleDenominations_whenWithdrawAmount_thenDispenseCorrectCombination() throws ATMException {
        // Act
        Map<Money, Integer> result = atm.withdraw(2600);

        // Assert
        assertEquals(Map.of(new Money(2000), 1, new Money(500), 1, new Money(100), 1), result);
    }

    @Test
    void givenInsufficientFunds_whenWithdrawLargeAmount_thenThrowATMException() {
        // Act & Assert
        assertThrows(ATMException.class, () -> atm.withdraw(100000));
    }

    @Test
    void givenImpossibleDenomination_whenWithdraw_thenThrowATMException() {
        // Arrange
        atm.reset(Map.of(new Money(100), 2));

        // Act & Assert
        assertThrows(ATMException.class, () -> atm.withdraw(150));
    }

    @Test
    void givenAmountNotMultipleOf10_whenWithdraw_thenThrowATMException() {
        // Act & Assert
        assertThrows(ATMException.class, () -> atm.withdraw(125));
    }

    @Test
    void givenNegativeOrZeroAmount_whenWithdraw_thenThrowATMException() {
        // Act & Assert
        assertThrows(ATMException.class, () -> atm.withdraw(0));
        assertThrows(ATMException.class, () -> atm.withdraw(-100));
    }

    @Test
    void givenATMWithLoadedDenomination_whenWithdraw_thenDispenseWithNewDenomination() throws ATMException {
        // Arrange
        atm.loadDenomination(new Money(50), 10);

        // Act
        Map<Money, Integer> result = atm.withdraw(150);

        // Assert
        assertEquals(Map.of(new Money(100), 1, new Money(50), 1), result);
    }

    @Test
    void givenATMWithFullBalance_whenWithdrawAll_thenATMEmptied() throws ATMException {
        // Arrange
        int total = 2000 * 10 + 500 * 20 + 100 * 100;

        // Act
        Map<Money, Integer> result = atm.withdraw(total);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(0, atm.getAvailableDenominations().values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void givenEmptyATM_whenWithdraw_thenThrowATMException() {
        // Arrange
        atm.reset(Map.of());

        // Act & Assert
        assertThrows(ATMException.class, () -> atm.withdraw(100));
    }

    @Test
    void givenMultipleThreads_whenParallelWithdrawals_thenAllSucceed() throws InterruptedException {
        // Arrange
        atm.reset(Map.of(new Money(100), 100));
        int threads = 10;
        int withdrawAmount = 500;
        List<Future<Map<Money, Integer>>> futures = new ArrayList<>();

        // Act
        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        return atm.withdraw(withdrawAmount);
                    } catch (ATMException e) {
                        return null;
                    }
                }));
            }
            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            assertTrue(terminated, "Executor did not terminate in the expected time");
        }

        // Assert
        long successful = futures.stream().filter(f -> {
            try {
                return f.get() != null;
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertEquals(threads, successful); // 10 threads, so 10 successful withdrawals expected
    }
}
