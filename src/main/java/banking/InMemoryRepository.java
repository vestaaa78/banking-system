package banking;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {

    private final Map<ID, T> storage = new HashMap<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot save null value");
        }
        storage.put(value.getId(), value);
    }

    @Override
    public T findById(ID id) {
        return storage.get(id);
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @Override
    public int size() {
        return storage.size();
    }
}