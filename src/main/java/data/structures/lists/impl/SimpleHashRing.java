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

    private final List<Position> nodePositions;
    private final Map<Position, Map<Key, Value>> entriesByPosition;
    private final Function<Key, Position> hashFunction;

    public SimpleHashRing(final Set<Position> nodePositions, final Function<Key, Position> hashFunction) throws AtLeastOneNodeMustExist {
        if (nodePositions.isEmpty()) {
            throw new AtLeastOneNodeMustExist();
        }

        this.nodePositions = nodePositions
                .stream()
                .sorted(Comparator.comparingInt(v -> v.value))
                .collect(Collectors.toList());
        this.entriesByPosition = nodePositions
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
    public void addNode(final Position position) {
        final Map<Key, Value> positionEntries = entriesByPosition.putIfAbsent(position, new HashMap<>());
        if (null == positionEntries) {
            final int followingNodeLocation = Math.abs(Collections.binarySearch(nodePositions, position) + 1);
            final int currentNodeIndex = Math.max(followingNodeLocation, 0);
            nodePositions.add(currentNodeIndex, position);

            final Map<Key, Value> followingNodeEntries = entriesByPosition.get(nodePositions.get(followingNodeLocation));
            final Map<Key, Value> currentNodeEntries = followingNodeEntries
                    .entrySet()
                    .stream()
                    .filter(
                            e -> 0 > this.hashFunction.apply(e.getKey()).compareTo(position)
                    )
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue
                            )
                    );
            entriesByPosition.put(position, currentNodeEntries);
            followingNodeEntries.keySet().removeAll(currentNodeEntries.keySet());
        }
    }

    @Override
    public void removeNode(final Position position) throws AtLeastOneNodeMustExist {
        final Map<Key, Value> entries = entriesByPosition.get(position);
        if (null != entries) {
            if (1 < entriesByPosition.size()) {
                final int currentNodeIndex = Collections.binarySearch(nodePositions, position);
                final int followingNodeIndex = (currentNodeIndex + 1) % nodePositions.size();
                this.entriesByPosition.get(this.nodePositions.get(followingNodeIndex)).putAll(entries);
                entriesByPosition.remove(position);
                nodePositions.remove(currentNodeIndex);
                return;
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
                entriesByPosition.get(
                        nodePositions.get(calculateNodeLocationForKey(key))
                )
        );
    }

    private int calculateNodeLocationForKey(final Key key) {
        final int nodeLocationIndex;
        {
            final int searchedIndex = Collections.binarySearch(nodePositions, hashFunction.apply(key));
            if (0 > searchedIndex) {
                nodeLocationIndex = Math.abs(searchedIndex) - 1;
            } else {
                nodeLocationIndex = searchedIndex;
            }
        }

        return nodeLocationIndex % nodePositions.size();
    }
}
