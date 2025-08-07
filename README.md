# Assumptions

- Only positive denominations that are multiples of 10 are valid (e.g., 10, 20, 50, 100, 500, 2000).
- All withdrawal amounts must be positive and a multiple of 10.
- The ATM cannot dispense more cash than it has in any denomination.
- The ATM always tries to minimize the number of notes dispensed (greedy algorithm).
- If the requested amount cannot be dispensed exactly with available denominations, the withdrawal fails entirely.
- Each ATM instance manages its own cash and is thread-safe (using locks).
- Multiple users (threads) may attempt to withdraw or load cash at the same time; the system must prevent race conditions.
- The application assumes that fairness in thread scheduling is important. When multiple threads are waiting to acquire the lock, the longest-waiting thread will acquire it next (first-come, first-served).
- All business rule violations (e.g., insufficient funds, invalid input) are reported via custom exceptions (`ATMException`).
- All public methods validate their inputs and throw exceptions for invalid data.
- The cash dispensing logic is pluggable (via `CashDispenserStrategy`), allowing for different algorithms.
- The system is designed to be easily unit tested, with clear separation of concerns.
