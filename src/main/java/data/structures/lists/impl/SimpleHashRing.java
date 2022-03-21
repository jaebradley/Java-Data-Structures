package data.structures.lists.impl;

import data.structures.lists.interfaces.HashRing;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleHashRing<Key, Value> implements HashRing<Key, Value> {
    private final List<Position> nodePositions;
    private final Map<Position, Map<Key, Value>> entriesByPosition;
    private final Function<Key, Position> keyToPositionCalculator;

    public SimpleHashRing(final Set<Position> nodePositions, final Function<Key, Position> keyToPositionCalculator) throws AtLeastOneNodeMustExist {
        if (nodePositions.isEmpty()) {
            throw new AtLeastOneNodeMustExist();
        }

        this.nodePositions = nodePositions
                .stream()
                .sorted(Comparator.comparingInt(Position::getValue))
                .collect(Collectors.toList());
        this.entriesByPosition = nodePositions
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                (v) -> new HashMap<>()
                        )
                );
        this.keyToPositionCalculator = keyToPositionCalculator;
    }

    @Override
    public void addNode(final Position position) {
        final Map<Key, Value> positionEntries = entriesByPosition.putIfAbsent(position, new HashMap<>());
        if (null == positionEntries) {
            final int followingNodePosition = Math.abs(Collections.binarySearch(nodePositions, position) + 1);
            final int currentNodeIndex = Math.max(followingNodePosition, 0);
            nodePositions.add(currentNodeIndex, position);

            final Map<Key, Value> followingNodeEntries = entriesByPosition.get(nodePositions.get(followingNodePosition));
            final Map<Key, Value> currentNodeEntries = followingNodeEntries
                    .entrySet()
                    .stream()
                    .filter(
                            e -> 0 > this.keyToPositionCalculator.apply(e.getKey()).compareTo(position)
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
                        nodePositions.get(calculateNodePositionForKey(key))
                )
        );
    }

    private int calculateNodePositionForKey(final Key key) {
        final int nodePositionIndex;
        {
            final int searchedIndex = Collections.binarySearch(nodePositions, keyToPositionCalculator.apply(key));
            if (0 > searchedIndex) {
                nodePositionIndex = Math.abs(searchedIndex) - 1;
            } else {
                nodePositionIndex = searchedIndex;
            }
        }

        return nodePositionIndex % nodePositions.size();
    }
}
