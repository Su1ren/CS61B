package deque;

public class ArrayDeque<Item> {
    /**
     * Different from LinkedListDeque,
     * ArrayDeque store items in an array.
     *
     * We use FRONT and REAR flags to signify the beginning and end of deque
     * So that we don't have to adjust the array every time modify from first.
     * The array grows from the center to both edges.
     *
     * front: the index of the first item
     * rear: the index after the last item
     *
     * Full: rear - front = size
     * Empty: rear == front
     */
    private Item[] _items;
    private int _size;
    private static final int DEFAULT_CAPACITY = 8;
    private int _front, _rear;

    /**
     * Default constructor
     * The array grows from the center to both edges.
     */
    public ArrayDeque() {
        _items = (Item[]) new Object[DEFAULT_CAPACITY];
        _size = 0;
        _front = DEFAULT_CAPACITY / 2;
        _rear = _front;
    }

    /**
     * Return the size of deque.
     * @return the size of deque
     */
    public int size() {
        return _size;
    }

    /**
     * Check if the deque is empty.
     * @return true if the deque is empty
     */
    public boolean isEmpty() {
        return _size == 0;
    }

    /**
     * Get the item at specifc index from 0
     * @param index the index of accessing item, count from 0
     * @return null if index is invalid
     */
    public Item get(int index) {
        if (index < 0 || index >= _size)
            return null;
        return _items[_front + index];
    }

    /**
     * Resize of ArrayDeque has different situations on capacity.
     * 1. Extend the capacity.
     * 2. Shrink the capacity.
     * @param capacity the new capacity of deque
     */
    public void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];

        System.arraycopy(_items, _front, a, (capacity - _size) / 2, _size);
        _front = (capacity - _size) / 2;
        _rear = _front + _size;

        _items = a;
    }

    /**
     * Adding a new item from the front.
     * @param item item to be added
     */
    public void addFirst(Item item) {
        if (_front == 0)
            resize(2 * _size);
        _items[--_front] = item;
        ++_size;
    }

    /**
     * Adding a new item from the rear.
     * @param item item to be added
     */
    public void addLast(Item item) {
        if (_rear == _items.length)
            resize(2 * _size);
        _items[_rear++] = item;
        ++_size;
    }

    /**
     * Remove an item from the front, then check if resize is necessary.
     * @return null if the deque is empty
     */
    public Item removeFirst() {
        if (_size == 0)
            return null;
        Item re = _items[_front];
        _items[_front++] = null;
        --_size;

        if (_size > 0 && _size < _items.length / 4)
            resize(_size * 2);
        else if (_size == 0)
            resize(DEFAULT_CAPACITY);
        
        return re;
    }

    /**
     * Remove an item from the rear, then check if resize is necessary.
     * @return null if the deque is empty
     */
    public Item removeLast() {
        if (_size == 0)
            return null;
        Item re = _items[--_rear];
        _items[_rear] = null;
        --_size;

        if (_size > 0 && _size < _items.length / 4)
            resize(_size * 2);
        else if (_size == 0)
            resize(DEFAULT_CAPACITY);

        return re;
    }

    /**
     * Print the items in deque in order.
     * Print empty if the deque is empty.
     */
    public void printDeque() {
        if (_size == 0) {
            System.out.println("Empty");
            return;
        }
        for (int i = _front; i != _rear; ++i) {
            System.out.print(_items[i] + " ");
        }
        System.out.println();
    }
}
