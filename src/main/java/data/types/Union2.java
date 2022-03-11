package data.types;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Union2<T1, T2> {
    private final Optional<T1> _1;
    private final Optional<T2> _2;

    Union2(final T1 _1, final T2 _2) {
        this._1 = Optional.ofNullable(_1);
        this._2 = Optional.ofNullable(_2);

        if (this._1.isEmpty() && this._2.isEmpty()) {
            throw new IllegalArgumentException("Union must contain a non-null value");
        }

        if (this._1.isPresent() && this._2.isPresent()) {
            throw new IllegalArgumentException("Union contains two non-null values");
        }
    }

    public static <T1, T2> Union2<T1, T2> _1(final T1 _1) {
        return new Union2<>(_1, null);
    }

    public static <T1, T2> Union2<T1, T2> _2(final T2 _2) {
        return new Union2<>(null, _2);
    }

    public <R> Union2<R, T2> map1(
            final Function<T1, R> mapper1
    ) {
        return map(
                mapper1,
                Function.identity()
        );
    }

    public <R> Union2<T1, R> map2(
            final Function<T2, R> mapper2
    ) {
        return map(
                Function.identity(),
                mapper2
        );
    }

    public <R1, R2> Union2<R1, R2> map(
            final Function<T1, R1> mapper1,
            final Function<T2, R2> mapper2
    ) {
        return fold(
                (_1) -> Union2._1(mapper1.apply(_1)),
                (_2) -> Union2._2(mapper2.apply(_2))
        );
    }

    public <R> R fold(
            final Function<T1, R> mapper1,
            final Function<T2, R> mapper2
    ) {
        return _1
                .map(mapper1)
                .orElseGet(
                        () -> _2
                                .map(mapper2)
                                .orElseThrow(() -> new RuntimeException("All values are null"))
                );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Union2<?, ?> union = (Union2<?, ?>) o;
        return Objects.equals(_1, union._1) && Objects.equals(_2, union._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "Union{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                '}';
    }
}
