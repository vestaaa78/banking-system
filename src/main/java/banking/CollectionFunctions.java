package banking;

import java.util.ArrayList;
import java.util.List;
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
}