package datastructures.sets;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DisjointSetTest {
    @Test
    public void TestCreatingNonEmptySets() {
        IntStream.rangeClosed(0, 3)
                .boxed()
                .forEach(
                        v -> DisjointSet.create(
                                IntStream.rangeClosed(0, v)
                                        .boxed()
                                        .collect(Collectors.toSet())
                        )
                );
    }

    @Test
    public void TestUnableToFindRootForValue() {
        Assert.assertThrows(
                DisjointSet.UnableToIdentifyRootForValue.class,
                () -> {
                    final DisjointSet disjointSet = DisjointSet.create(
                            Set.of(1)
                    );

                    disjointSet.findRoot(-1);
                }
        );
    }

    @Test
    public void TestAddingValue() {
        final DisjointSet disjointSet = DisjointSet.create(Collections.emptySet());
        try {
            disjointSet.findRoot(1);
            Assert.fail();
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            // expected
        }

        IntStream
                .rangeClosed(1, 10)
                .boxed()
                .forEach(
                        v -> {
                            disjointSet.add(1);
                            try {
                                Assert.assertEquals(1, disjointSet.findRoot(1));
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );
    }

    @Test
    public void TestAbleToFindRootForValue() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        try {
            Assert.assertEquals(
                    1,
                    disjointSet.findRoot(1)
            );
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }
    }

    @Test
    public void TestRootValueDoesNotChangeAfterMultipleCalls() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        IntStream
                .rangeClosed(1, 10)
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        1,
                                        disjointSet.findRoot(1)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );

    }

    @Test
    public void TestUnionTwoValuesThatDoNotHaveRootsInSetThrowUnableToIdentifyRoot() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        Assert.assertThrows(
                DisjointSet.UnableToIdentifyRootForValue.class,
                () -> disjointSet.union(-1, -2)
        );
    }

    @Test
    public void TestUnionForFirstValueThatDoesNotHaveARootThrowsUnableToIdentifyRoot() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        Assert.assertThrows(
                DisjointSet.UnableToIdentifyRootForValue.class,
                () -> disjointSet.union(-1, 1)
        );
    }

    @Test
    public void TestUnionForSecondValueThatDoesNotHaveARootThrowsUnableToIdentifyRoot() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        Assert.assertThrows(
                DisjointSet.UnableToIdentifyRootForValue.class,
                () -> disjointSet.union(1, -1)
        );
    }

    @Test
    public void TestUnionForSameValues() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1)
        );

        try {
            disjointSet.union(1, 1);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }
    }

    @Test
    public void TestUnionForTwoDifferentRootValues() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1, 2)
        );

        try {
            disjointSet.union(1, 2);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        try {
            Assert.assertEquals(
                    1,
                    disjointSet.findRoot(2)
            );
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        try {
            Assert.assertEquals(
                    1,
                    disjointSet.findRoot(1)
            );
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }
    }

    @Test
    public void TestUnionForThreeDifferentRootValues() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1, 2, 3)
        );

        try {
            disjointSet.union(1, 2);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        IntStream
                .rangeClosed(1, 2)
                .boxed()
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        1,
                                        disjointSet.findRoot(v)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );

        try {
            disjointSet.union(2, 3);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        IntStream
                .rangeClosed(1, 3)
                .boxed()
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        1,
                                        disjointSet.findRoot(v)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );
    }

    @Test
    public void TestUnionForFourDifferentRootValues() {
        final DisjointSet disjointSet = DisjointSet.create(
                Set.of(1, 2, 3, 4)
        );

        try {
            disjointSet.union(1, 2);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        IntStream
                .rangeClosed(1, 2)
                .boxed()
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        1,
                                        disjointSet.findRoot(v)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );

        try {
            disjointSet.union(4, 3);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        IntStream
                .rangeClosed(3, 4)
                .boxed()
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        4,
                                        disjointSet.findRoot(v)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );

        try {
            disjointSet.union(4, 1);
        } catch (DisjointSet.UnableToIdentifyRootForValue e) {
            Assert.fail();
        }

        IntStream
                .rangeClosed(1, 4)
                .boxed()
                .forEach(
                        v -> {
                            try {
                                Assert.assertEquals(
                                        4,
                                        disjointSet.findRoot(v)
                                );
                            } catch (DisjointSet.UnableToIdentifyRootForValue e) {
                                Assert.fail();
                            }
                        }
                );
    }
}