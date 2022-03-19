package data.structures.sets;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DisjointSet {
    public static class UnableToIdentifyRootForValue extends Exception {
    }

    public static DisjointSet create(final Set<Integer> values) {
        return new DisjointSet(
                values
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        (v) -> 1
                                )
                        ),
                values
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        Function.identity()
                                )
                        )

        );
    }

    private final Map<Integer, Integer> sizesByRoot;
    private final Map<Integer, Integer> parentsByChild;

    private DisjointSet(final Map<Integer, Integer> sizesByRoot, final Map<Integer, Integer> parentsByChild) {
        this.sizesByRoot = sizesByRoot;
        this.parentsByChild = parentsByChild;
    }

    public void add(final int value) {
        if (!parentsByChild.containsKey(value)) {
            parentsByChild.put(value, value);
            sizesByRoot.put(value, 1);
        }
    }

    public int findRoot(final int value) throws UnableToIdentifyRootForValue {
        final Integer parent = parentsByChild.get(value);
        if (null == parent) {
            throw new UnableToIdentifyRootForValue();
        }

        if (parent.equals(value)) {
            return value;
        }

        final int root = findRoot(parent);
        parentsByChild.put(value, root);

        return root;
    }

    public void union(final int value1, final int value2) throws UnableToIdentifyRootForValue {
        final int root1 = findRoot(value1);
        final int root2 = findRoot(value2);

        if (root1 != root2) {
            final Integer root1Size = sizesByRoot.get(root1);
            if (null == root1Size) {
                throw new RuntimeException(createUnableToIdentifyRootSizeForValueErrorMessage(root1, value1));
            }
            final Integer root2Size = sizesByRoot.get(root2);
            if (null == root2Size) {
                throw new RuntimeException(createUnableToIdentifyRootSizeForValueErrorMessage(root2, value2));
            }
            final int parentRoot;
            final int childRoot;
            {
                if (root1Size < root2Size) {
                    parentRoot = root2;
                    childRoot = root1;
                } else {
                    parentRoot = root1;
                    childRoot = root2;
                }
            }

            parentsByChild.put(childRoot, parentRoot);
            sizesByRoot.put(parentRoot, root1Size + root2Size);
        }
    }

    private static String createUnableToIdentifyRootSizeForValueErrorMessage(final int root, final int value) {
        return "Unable to identify size for root " + root + " and value " + value;
    }
}
