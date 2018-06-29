package m;

public interface GPMAP<K,V> {
    interface Entry<K,V> {
        K getKey();
        V getValue();
        V setValue(V value);
        boolean equals(Object o);
        int hashCode();
    }

    V put(K key, V value);
}
