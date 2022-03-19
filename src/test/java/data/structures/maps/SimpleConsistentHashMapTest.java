package data.structures.maps;

import junit.framework.TestCase;
import org.junit.Assert;

public class SimpleConsistentHashMapTest extends TestCase {

    public void test() {
        final SimpleConsistentHashMap<Integer, String> map = new SimpleConsistentHashMap<>(
                10,
                Integer::longValue
        );

        map.put(1, "one");
        Assert.assertEquals("one", map.get(1));

        map.put(2, "two");
        Assert.assertEquals("two", map.get(2));

        map.put(3, "three");
        Assert.assertEquals("three", map.get(3));
    }
}