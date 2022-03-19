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
    public void addNode(Position location) {
        final Map<Key, Value> entries = entriesByLocation.putIfAbsent(location, new HashMap<>());
        if (null == entries) {
            int indexOfFirstElementGreaterThanLocation = Math.abs(Collections.binarySearch(nodeLocations, location)) - 1;
            int insertionIndex = Math.max(indexOfFirstElementGreaterThanLocation - 1, 0);
            nodeLocations.add(insertionIndex, location);
            final Map<Key, Value> nextIndexEntries = entriesByLocation.get(nodeLocations.get(indexOfFirstElementGreaterThanLocation));
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
        return getEntriesForKey(key).map(entries -> entries.put(key, value));
    }

    @Override
    public Optional<Value> removeEntry(final Key key) {
        return getEntriesForKey(key).map(entries -> entries.remove(key));
    }

    @Override
    public Optional<Value> getValue(final Key key) {
        return getEntriesForKey(key).map(entries -> entries.get(key));
    }

    private Optional<Map<Key, Value>> getEntriesForKey(final Key key) {
        return Optional.ofNullable(
                entriesByLocation.get(
                        nodeLocations.get(calculateNodeLocationForKey(key))
                )
        );
    }

    private int calculateNodeLocationForKey(final Key key) {
        final int nodeLocationIndex;
        {
            final int searchedIndex = Collections.binarySearch(nodeLocations, hashFunction.apply(key));
            if (0 > searchedIndex) {
                nodeLocationIndex = Math.abs(searchedIndex) - 1;
            } else {
                nodeLocationIndex = searchedIndex;
            }
        }

        return nodeLocationIndex % nodeLocations.size();
    }
}
