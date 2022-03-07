package datastructures.maps;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpleConsistentHashMap<Key, Value> implements Map<Key, Value> {
    private static final int DEGREES_IN_CIRCLE = 360;
    private static final BigDecimal MINIMUM_ANGLE = BigDecimal.valueOf(0);
    private static final BigDecimal MAXIMUM_ANGLE = BigDecimal.valueOf(DEGREES_IN_CIRCLE);

    private int size;
    private final int nodeCount;
    private final BigDecimal[] inclusiveNodeStartAngles;
    private final Function<Key, Long> hashFunction;
    private final Map<Integer, Map<Key, Value>> entriesByAngleIndex;

    public SimpleConsistentHashMap(
            final int nodeCount,
            final Function<Key, Long> hashFunction
    ) {
        if (0 >= nodeCount) {
            throw new IllegalArgumentException("Node count must be greater than 0");
        }
        this.size = 0;
        this.nodeCount = nodeCount;
        this.hashFunction = hashFunction;

        inclusiveNodeStartAngles = IntStream.range(0, nodeCount)
                .boxed()
                .map(
                        v -> SimpleConsistentHashMap.calculateRandomAngle()
                )
                .sorted(
                        BigDecimal::compareTo
                )
                .toArray(BigDecimal[]::new);

        entriesByAngleIndex = new HashMap<>(nodeCount);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return 0 == size();
    }

    @Override
    public boolean containsKey(Object o) {
        if (null == o) {
            throw new NullPointerException("Cannot get value for null key");
        }
        final Key key = (Key) o;
        return Optional.ofNullable(entriesByAngleIndex.get(calculateKeyIndexForKey(key)))
                .map(v -> v.containsKey(key))
                .orElse(Boolean.FALSE);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value get(Object obj) {
        if (null == obj) {
            throw new NullPointerException("Cannot get value for null key");
        }
        final Key key = (Key) obj;

        final Map<Key, Value> nodeMapping = entriesByAngleIndex.get(calculateKeyIndexForKey(key));
        return Optional.ofNullable(nodeMapping)
                .flatMap(v -> Optional.ofNullable(v.get(key)))
                .orElse(null);
    }

    @Override
    public Value put(Key key, Value value) {
        if (null == key) {
            throw new NullPointerException("Key cannot be null");
        }

        final Value insertionValue = entriesByAngleIndex
                .computeIfAbsent(
                        calculateKeyIndexForKey(key),
                        (v) -> new HashMap<>()
                )
                .put(key, value);

        if (null == insertionValue) {
            size++;
        }

        return insertionValue;
    }

    @Override
    public Value remove(Object o) {
        if (null == o) {
            throw new NullPointerException("Key cannot be null");
        }

        final Key key = (Key) o;

        return Optional.ofNullable(
                        entriesByAngleIndex.get(calculateKeyIndexForKey(key))
                )
                .flatMap(entries -> Optional.ofNullable(entries.remove(key)))
                .orElse(null);
    }

    @Override
    public void putAll(Map<? extends Key, ? extends Value> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Key> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Value> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Key, Value>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private int calculateKeyIndexForKey(final Key key) {
        final BigDecimal angle = calculateAngleForKeyAndHashFunction(key, hashFunction);
        final int insertionIndex = Arrays.binarySearch(inclusiveNodeStartAngles, angle);
        return insertionIndex % nodeCount;
    }

    private static BigDecimal calculateRandomAngle() {
        return SimpleConsistentHashMap.MINIMUM_ANGLE
                .add(
                        BigDecimal.valueOf(Math.random())
                                .multiply(
                                        SimpleConsistentHashMap.MAXIMUM_ANGLE
                                                .subtract(SimpleConsistentHashMap.MINIMUM_ANGLE)
                                )
                );
    }

    private static <Key> BigDecimal calculateAngleForKeyAndHashFunction(
            final Key key,
            final Function<Key, Long> hashFunction
    ) {
        return BigDecimal.valueOf(hashFunction.apply(key) % DEGREES_IN_CIRCLE);
    }
}
