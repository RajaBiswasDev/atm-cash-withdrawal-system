package atm.domain;

public record Money(int value) implements Comparable<Money> {
    public Money {
        if (value <= 0 || value % 10 != 0) {
            throw new IllegalArgumentException("Invalid denomination: " + value);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Money(int otherValue))) return false;
        return value == otherValue;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public int compareTo(Money other) {
        // Descending order for greedy algorithms
        return Integer.compare(other.value, this.value);
    }
}
