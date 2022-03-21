package data.structures.lists.interfaces;

import java.util.Objects;
import java.util.Optional;

public interface HashRing<Key, Value> {
    class AtLeastOneNodeMustExist extends Exception {
    }

    class Position implements Comparable<Position> {
        private final int value;

        public Position(int value) {
            if (0 > value) {
                throw new IllegalArgumentException("value must be non-negative");
            }
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return value == position.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public int compareTo(Position o) {
            return Integer.compare(this.value, o.value);
        }
    }

    void addNode(Position position);

    void removeNode(Position position) throws AtLeastOneNodeMustExist;

    Optional<Value> addEntry(Key key, Value value);

    Optional<Value> removeEntry(Key key);

    Optional<Value> getValue(Key key);
}
