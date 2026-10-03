package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {

    private final List<T> values = new ArrayList<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot save null value");
        }

        ID id = value.getId();

        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getId(), id)) {
                values.set(i, value); // Заменяем элемент по индексу
                return;
            }
        }

        values.add(value);
    }

    @Override
    public T findById(ID id) {
        // Требование: пройти по values и сравнить ID
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean existsById(ID id) {
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }
}