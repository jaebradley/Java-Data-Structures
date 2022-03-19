package data.structures.lists.impl;

import data.structures.lists.interfaces.HashRing;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleHashRing<Key, Value> implements HashRing<Key, Value> {
    public static class Position implements Comparable<Position> {
        private final int value;

        public Position(int value) {
            if (0 > value) {
                throw new IllegalArgumentException("value must be positive");
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

    static class Node<Key, Value> {
        private final Position location;
        private final Map<Key, Value> entries;

        public Node(final Position location, final Map<Key, Value> entries) {
            this.location = location;
            this.entries = entries;
        }

        public Position getLocation() {
            return location;
        }

        public Map<Key, Value> getEntries() {
            return entries;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(location, node.location) && Objects.equals(entries, node.entries);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, entries);
        }
    }

    private final List<Position> nodeLocations;
    private final Map<Position, Map<Key, Value>> entriesByLocation;
    private final Function<Key, Position> hashFunction;

    public SimpleHashRing(final Set<Position> nodeLocations, final Function<Key, Position> hashFunction) throws AtLeastOneNodeMustExist {
        if (nodeLocations.isEmpty()) {
            throw new AtLeastOneNodeMustExist();
        }

        this.nodeLocations = nodeLocations
                .stream()
                .sorted(Comparator.comparingInt(v -> v.value))
                .collect(Collectors.toList());
        this.entriesByLocation = nodeLocations
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                (v) -> new HashMap<>()
                        )
                );
        this.hashFunction = hashFunction;
    }

    @Override
    public void addNode(Position location) throws NodeAlreadyExists {
        final Map<Key, Value> entries = entriesByLocation.putIfAbsent(location, new HashMap<>());
        if (null == entries) {
            int indexOfFirstElementGreaterThanLocation = Collections.binarySearch(nodeLocations, location);
            int insertionIndex = Math.min(indexOfFirstElementGreaterThanLocation - 1, 0);
            nodeLocations.add(insertionIndex, location);
            final Map<Key, Value> nextIndexEntries = entriesByLocation.get(new Position(indexOfFirstElementGreaterThanLocation));
            final Map<Key, Value> newNodeEntries = nextIndexEntries
                    .entrySet()
                    .stream()
                    .filter(
                            e -> 0 > this.hashFunction.apply(e.getKey()).compareTo(location)
                    )
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue
                            )
                    );
            entriesByLocation.put(location, newNodeEntries);
            nextIndexEntries.keySet().removeAll(newNodeEntries.keySet());
        }
        throw new NodeAlreadyExists();


    }

    @Override
    public void removeNode(Position location) throws AtLeastOneNodeMustExist {
        final Map<Key, Value> entries = entriesByLocation.get(location);
        if (null != entries) {
            if (1 < entriesByLocation.size()) {
                final int index = Collections.binarySearch(nodeLocations, location);
                final int nextNodeIndex = (index + 1) % nodeLocations.size();
                this.entriesByLocation.get(this.nodeLocations.get(nextNodeIndex)).putAll(entries);
                entriesByLocation.remove(location);
                nodeLocations.remove(index);
            }

            throw new AtLeastOneNodeMustExist();
        }
    }


    @Override
    public Optional<Value> addEntry(final Key key, final Value value) {
        return Optional.ofNullable(
                        entriesByLocation
                                .get(
                                        nodeLocations.get(
                                                Collections.binarySearch(nodeLocations, hashFunction.apply(key)) % nodeLocations.size()
                                        )
                                )
                )
                .map(entries -> entries.put(key, value));
    }

    @Override
    public Value removeEntry(final Key key) throws EntryDoesNotExist {
        final Map<Key, Value> entries = entriesByLocation
                .get(
                        nodeLocations.get(
                                Collections.binarySearch(nodeLocations, hashFunction.apply(key)) % nodeLocations.size()
                        )
                );
        if (null == entries) {
            throw new EntryDoesNotExist();
        }

        final Value value = entries.remove(key);

        if (null == value) {
            throw new EntryDoesNotExist();
        }

        return value;
    }

    @Override
    public Optional<Value> getValue(final Key key) {
        return Optional.ofNullable(
                entriesByLocation
                        .get(
                                nodeLocations.get(
                                        Collections.binarySearch(nodeLocations, hashFunction.apply(key)) % nodeLocations.size()
                                )
                        )
                        .get(key)
        );
    }
}
