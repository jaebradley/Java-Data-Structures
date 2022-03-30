package data.structures.streams.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Partitioner {
    static class PartitionSpliterator<T> extends Spliterators.AbstractSpliterator<List<T>> {
        private final Spliterator<T> values;
        private final int partitionSize;
        private List<T> currentPartition;

        public PartitionSpliterator(final Spliterator<T> values, final int partitionSize) {
            super(
                    Double.valueOf(Math.ceil((double) (values.estimateSize() / partitionSize)))
                            .intValue(),
                    ORDERED | IMMUTABLE | SIZED | SUBSIZED
            );

            if (1 > partitionSize) {
                throw new IllegalArgumentException("Size must be a positive integer");
            }

            this.values = values;
            this.partitionSize = partitionSize;
            this.currentPartition = null;
        }

        @Override
        public boolean tryAdvance(Consumer<? super List<T>> action) {
            if (null == currentPartition) {
                currentPartition = new ArrayList<>();
            }

            while (partitionSize > currentPartition.size() && values.tryAdvance(currentPartition::add)) {
            }

            if (currentPartition.isEmpty()) {
                return false;
            }

            action.accept(currentPartition);
            currentPartition = null;
            return true;
        }
    }

    public static <Value> Stream<List<Value>> partition(final Stream<Value> values, final int size) {
        if (1 > size) {
            throw new IllegalArgumentException("Size must be a positive integer");
        }

        return StreamSupport.stream(
                new PartitionSpliterator<>(values.spliterator(), size),
                values.isParallel()
        );
    }
}
