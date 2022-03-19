package data.structures.lists.impl;

import data.structures.lists.interfaces.HashRing;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class SimpleHashRingTest extends TestCase {

    public void test() {
        try {
            new SimpleHashRing<Integer, Integer>(Collections.emptySet(), SimpleHashRing.Position::new);
            throw new RuntimeException("unexpected");
        } catch (HashRing.AtLeastOneNodeMustExist atLeastOneNodeMustExist) {
            // expected
        }

        final HashRing<Integer, Integer> hashRing;

        try {
            hashRing = new SimpleHashRing<>(Set.of(new SimpleHashRing.Position(0)), SimpleHashRing.Position::new);
        } catch (HashRing.AtLeastOneNodeMustExist e) {
            throw new RuntimeException("unexpected", e);
        }

        hashRing.addNode(new SimpleHashRing.Position(0));
        hashRing.addNode(new SimpleHashRing.Position(1));
        hashRing.addNode(new SimpleHashRing.Position(Integer.MAX_VALUE));
        hashRing.addEntry(Integer.MAX_VALUE, Integer.MAX_VALUE);
        hashRing.addEntry(0, 0);

        Assert.assertEquals(Optional.of(0), hashRing.getValue(0));
        Assert.assertEquals(Optional.of(Integer.MAX_VALUE), hashRing.getValue(Integer.MAX_VALUE));
        Assert.assertEquals(Optional.empty(), hashRing.getValue(1));

    }
}