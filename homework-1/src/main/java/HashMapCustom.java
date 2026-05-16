import java.util.Objects;

public class HashMapCustom<K, V> implements MapCustom<K, V> {

    static final int DEFAULT_INIT_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static class Node<K, V> implements Entry<K, V> {

        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public final boolean equals(Object o) {

            if (o == this)
                return true;

            return o instanceof MapCustom.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    static int hash(Object key) {
        return Objects.hashCode(key);
    }

    static int tableSizeFor(int cap) {

        int n = 1;

        while (n < cap) {
            n <<= 1;
        }

        return n;
    }

    private Node<K, V>[] table;
    private int size;
    private final float loadFactor;
    private int threshold;

    public HashMapCustom() {
        this(DEFAULT_INIT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMapCustom(int capacity, float loadFactor) {

        this.threshold = tableSizeFor(capacity);
        this.loadFactor = loadFactor;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public V put(K key, V value) {

        return putVal(hash(key), key, value);
    }

    private V putVal(int hash, K key, V value) {

        if (table == null || table.length == 0) {
            resize();
        }

        int index = (table.length - 1) & hash;

        Node<K, V> first = table[index];

        if (first == null) {

            table[index] = new Node<>(hash, key, value);

        } else {

            Node<K, V> current = first;

            while (true) {

                if (current.hash == hash &&
                        Objects.equals(current.key, key)) {

                    V old = current.value;
                    current.value = value;

                    return old;
                }

                if (current.next == null) {
                    current.next = new Node<>(hash, key, value);
                    break;
                }

                current = current.next;
            }
        }

        size++;

        if (size > threshold) {
            resize();
        }

        return null;
    }

    @Override
    public V get(K key) {

        Node<K, V> node = getNode(hash(key), key);

        return node == null ? null : node.value;
    }

    private Node<K, V> getNode(int hash, K key) {

        Node<K, V>[] tab = table;

        if (tab == null || tab.length == 0)
            return null;

        int index = (tab.length - 1) & hash;

        Node<K, V> current = tab[index];

        while (current != null) {

            if (current.hash == hash &&
                    Objects.equals(current.key, key)) {

                return current;
            }

            current = current.next;
        }

        return null;
    }

    @Override
    public V remove(K key) {

        Node<K, V>[] tab = table;

        if (tab == null || tab.length == 0)
            return null;

        int hash = hash(key);

        int index = (tab.length - 1) & hash;

        Node<K, V> current = tab[index];
        Node<K, V> prev = null;

        while (current != null) {

            if (current.hash == hash &&
                    Objects.equals(current.key, key)) {

                if (prev == null) {
                    tab[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                size--;

                return current.value;
            }

            prev = current;
            current = current.next;
        }

        return null;
    }

    private void resize() {

        Node<K, V>[] oldTable = table;

        int oldCap = (oldTable == null) ? 0 : oldTable.length;

        int newCap;

        if (oldCap == 0) {
            newCap = threshold;
        } else {
            newCap = oldCap << 1;
        }

        threshold = (int) (newCap * loadFactor);

        @SuppressWarnings("unchecked")
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCap];

        if (oldTable != null) {

            for (Node<K, V> node : oldTable) {

                while (node != null) {

                    Node<K, V> next = node.next;

                    int index = (newCap - 1) & node.hash;

                    node.next = newTable[index];
                    newTable[index] = node;

                    node = next;
                }
            }
        }

        table = newTable;
    }
}