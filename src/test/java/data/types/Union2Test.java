package data.types;

import junit.framework.TestCase;

public class Union2Test extends TestCase {

    public void test() {
        try {
            new Union2<>(null, null);
            throw new RuntimeException("unexpected");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            new Union2<>("foo", "bar");
            throw new RuntimeException("unexpected");
        } catch (IllegalArgumentException e) {
            // expected
        }


        assert Union2._1("foo")
                .map(
                        (v) -> "bar",
                        (v) -> {
                            throw new RuntimeException("unexpected");
                        }
                )
                .equals(Union2._1("bar"));

        assert Union2._2("foo")
                .map(
                        (v) -> {
                            throw new RuntimeException("unexpected");
                        },
                        (v) -> "bar"
                )
                .equals(Union2._2("bar"));

        assert Union2._1("foo")
                .fold(
                        (v) -> "bar",
                        (v) -> {
                            throw new RuntimeException("unexpected");
                        }
                )
                .equals("bar");

        assert Union2._2("foo")
                .fold(
                        (v) -> {
                            throw new RuntimeException("unexpected");
                        },
                        (v) -> "bar"
                )
                .equals("bar");

        assert Union2._1("foo")
                .map1((v) -> "bar")
                .equals(Union2._1("bar"));

        assert Union2._1("foo")
                .map2((v) -> {
                    throw new RuntimeException("unexpected");
                })
                .equals(Union2._1("foo"));

        assert Union2._2("foo")
                .map2((v) -> "bar")
                .equals(Union2._2("bar"));

        assert Union2._2("foo")
                .map1((v) -> {
                    throw new RuntimeException("unexpected");
                })
                .equals(Union2._2("foo"));
    }
}