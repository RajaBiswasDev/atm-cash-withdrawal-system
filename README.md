# ATM Cash Withdrawal System

This repository contains a Java-based ATM simulation. The system is designed to handle cash withdrawals using a greedy algorithm, ensuring the minimum number of notes are dispensed for any valid request. The code is thread-safe and supports concurrent access, making it suitable for multi-user scenarios.

## What’s Included

- **ATM core logic**: Handles loading, dispensing, and tracking of cash denominations.
- **Greedy cash dispenser**: Implements a strategy to always use the largest denominations first.
- **Custom exception handling**: Provides clear error messages for invalid operations.
- **Thread safety**: Uses a fair `ReentrantLock` to prevent race conditions and ensure fairness among threads.
- **Unit tests**: Comprehensive JUnit tests for all major features.

## Assumptions

- Only positive denominations that are multiples of 10 are valid (e.g., 10, 20, 50, 100, 500, 2000).
- All withdrawal amounts must be positive and a multiple of 10.
- The ATM cannot dispense more cash than it has in any denomination.
- The ATM always tries to minimize the number of notes dispensed (greedy algorithm).
- If the requested amount cannot be dispensed exactly with available denominations, the withdrawal fails entirely.
- Each ATM instance manages its own cash and is thread-safe (using locks).
- Multiple users (threads) may attempt to withdraw or load cash at the same time; the system must prevent race conditions.
- The application assumes that fairness in thread scheduling is important. When multiple threads are waiting to acquire the lock, the longest-waiting thread will acquire it next (first-come, first-served).
- All business rule violations (e.g., insufficient funds, invalid input) are reported via custom exceptions (ATMException).
- All public methods validate their inputs and throw exceptions for invalid data.
- The cash dispensing logic is pluggable (via CashDispenserStrategy), allowing for different algorithms.
- The system is designed to be easily unit tested, with clear separation of concerns.

## How to Build and Test

1. Clone this repository.
2. Build the project with Maven:
   ```
   mvn clean install
   ```
3. Run all tests:
   ```
   mvn test
   ```

## Project Structure

- `src/main/java/atm/domain/` — Main classes like `ATM` and `Money`.
- `src/main/java/atm/strategy/` — Cash dispensing strategies.
- `src/main/java/atm/exception/` — Custom exception classes.
- `src/test/java/atm/` — JUnit test cases.

## Logging

- Logging is set up using SLF4J with Logback. Configuration can be found in `src/main/resources/logback.xml`.
