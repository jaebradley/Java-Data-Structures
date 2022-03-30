package data.structures.streams.utils;

import junit.framework.TestCase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PartitionerTest extends TestCase {

    public void test() {
        assertEquals(
                Stream.of(
                        List.of(1, 2),
                        List.of(3, 4),
                        List.of(5, 6)
                ).collect(Collectors.toList()),
                Partitioner.partition(
                        IntStream.rangeClosed(1, 6).boxed(),
                        2
                ).collect(Collectors.toList())
        );

        assertEquals(
                Stream.of(
                        List.of(1, 2, 3)
                ).collect(Collectors.toList()),
                Partitioner.partition(
                        IntStream.rangeClosed(1, 3).boxed(),
                        3
                ).collect(Collectors.toList())
        );

        assertEquals(
                Stream.of(
                        List.of(1, 2, 3)
                ).collect(Collectors.toList()),
                Partitioner.partition(
                        IntStream.rangeClosed(1, 3).boxed(),
                        4
                ).collect(Collectors.toList())
        );

        assertEquals(
                Stream.of(
                        List.of(1, 2),
                        List.of(3)
                ).collect(Collectors.toList()),
                Partitioner.partition(
                        IntStream.rangeClosed(1, 3).boxed(),
                        2
                ).collect(Collectors.toList())
        );
    }
}