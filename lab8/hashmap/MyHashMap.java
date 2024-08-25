package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Suiren
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private int size;
    private double loadFactor;

    /** Constructors */
    public MyHashMap() {
//        for (int i = 0; i < INITIAL_SIZE; ++i) {
//            buckets[i] = new LinkedList<>();
//        }
//        size = 0;
//        loadFactor = DEFAULT_LOAD_FACTOR;
        this(INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
//        for (int i = 0; i < initialSize; ++i) {
//            buckets[i] = new LinkedList<>();
//        }
//        size = 0;
//        loadFactor = DEFAULT_LOAD_FACTOR;
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
        size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] re = new Collection[tableSize];
        for (int i = 0; i < tableSize; ++i) {
            re[i] = createBucket();
        }
        return re;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        buckets = createTable(INITIAL_SIZE);
    }

    /**
     * Return the ket set of HashMap
     * Since Map61B extends Map61B<K, V>, we can simply for-each loop.
     * @return the key set
     */
    @Override
    public Set<K> keySet() {
        Set<K> re = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                re.add(n.key);
            }
        }
        return re;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        Node dst = getNode(key);
        return dst == null ? null : dst.value;
    }

    private Node getNode(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[index]) {
            if (key.equals(n.key)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        Node dst = getNode(key);
        if (dst == null) {
            if (shouldResize()) {
                resize(buckets.length * 2);
            }

            int index = Math.floorMod(key.hashCode(), buckets.length);
            buckets[index].add(createNode(key, value));
            ++size;
            return ;
        }
        dst.value = value;
    }

    @Override
    public V remove(K key) {
        Node dst = getNode(key);
        if (dst == null) {
            return null;
        }

        int index = Math.floorMod(key.hashCode(), buckets.length);
        buckets[index].remove(dst);
        --size;
        return dst.value;
    }

    @Override
    public V remove(K key, V value) {
        Node dst = getNode(key);
        if (dst == null || dst.value != value) {
            return null;
        }

        int index = Math.floorMod(key.hashCode(), buckets.length);
        buckets[index].remove(dst);
        --size;
        return dst.value;
    }

    /**
     * Remember Java's GC.
     * You don't need to deallocate the nodes like in C++.
     * @param capacity the new capacity
     */
    private void resize(int capacity) {
        Collection<Node>[] newBuckets = createTable(capacity);
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                int index = Math.floorMod(n.key.hashCode(), newBuckets.length);
                newBuckets[index].add(n);
            }
        }
        buckets = newBuckets;
    }

    private boolean shouldResize() {
        return size >= buckets.length * loadFactor;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
