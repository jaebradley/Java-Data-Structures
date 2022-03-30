package data.structures.trees.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleMerkleTree {
    public static class Node {
        private final Node left;
        private final Node right;
        private final byte[] value;

        public Node(final Node left, final Node right, final byte[] value) {
            this.left = left;
            this.right = right;
            this.value = value;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        public byte[] getValue() {
            return Arrays.copyOf(value, value.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(left, node.left) && Objects.equals(right, node.right) && Arrays.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(left, right);
            result = 31 * result + Arrays.hashCode(value);
            return result;
        }
    }

    private final Node root;

    public SimpleMerkleTree(final Function<byte[], byte[]> hashFunction, final byte[][] values) {
        if (0 == values.length) {
            throw new IllegalArgumentException("Must specify at least one value");
        }

        List<Node> currentLevel = Stream.of(values)
                .map(hashFunction)
                .map(hash -> new Node(null, null, hash))
                .collect(Collectors.toList());

        while (1 < currentLevel.size()) {
            final List<Node> nextLevel = new ArrayList<>();

            for (int nodeIndex = 0; nodeIndex < currentLevel.size(); nodeIndex += 2) {
                final Node firstNode = currentLevel.get(nodeIndex);

                if (currentLevel.size() - 1 == nodeIndex) {
                    nextLevel.add(new Node(firstNode, null, firstNode.value));
                } else {
                    final Node secondNode = currentLevel.get(1 + nodeIndex);

                    nextLevel.add(
                            new Node(
                                    firstNode,
                                    secondNode,
                                    hashFunction.apply(concatenate(firstNode.value, secondNode.value))
                            )
                    );
                }
            }

            currentLevel = nextLevel;
        }

        this.root = currentLevel.get(0);
    }

    public Node getRoot() {
        return root;
    }

    private static byte[] concatenate(final byte[] first, final byte[] second) {
        final int firstLength = first.length;
        final int secondLength = second.length;
        final byte[] concatenated = new byte[firstLength + secondLength];
        System.arraycopy(first, 0, concatenated, 0, firstLength);
        System.arraycopy(second, 0, concatenated, firstLength, secondLength);

        return concatenated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMerkleTree that = (SimpleMerkleTree) o;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }
}
