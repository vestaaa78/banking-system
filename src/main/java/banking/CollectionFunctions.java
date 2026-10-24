package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CollectionFunctions {

    private CollectionFunctions() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static <T> List<T> filter(List<T> source, Predicate<? super T> condition) {
        if (source == null) {
            throw new IllegalArgumentException("Source list cannot be null");
        }
        if (condition == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }

        List<T> result = new ArrayList<>();
        for (T value : source) {
            if (condition.test(value)) {
                result.add(value);
            }
        }
        return result;
    }
    public static <T, R> List<R> map(List<T> source, Function<? super T, ? extends R> mapper) {
        if (source == null) {
            throw new IllegalArgumentException("Source list cannot be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function cannot be null");
        }

        List<R> result = new ArrayList<>();
        for (T value : source) {
            result.add(mapper.apply(value));
        }
        return result;
    }
    public static <T> void forEach(List<T> source, Consumer<? super T> action) {
        if (source == null) {
            throw new IllegalArgumentException("Source list cannot be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }

        for (T value : source) {
            action.accept(value);
        }
    }
}