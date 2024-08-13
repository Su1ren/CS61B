package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
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
     * Empty: rear == front
     */
    private T[] _items;
    private int _size;
    private static final int DEFAULT_CAPACITY = 8;
    private int _front, _rear;

    /**
     * Default constructor
     * The array grows from the center to both edges.
     */
    public ArrayDeque() {
        _items = (T[]) new Object[DEFAULT_CAPACITY];
        _size = 0;
        _front = DEFAULT_CAPACITY / 2;
        _rear = _front;
    }

    /**
     * Return the size of deque.
     * @return the size of deque
     */
    @Override
    public int size() {
        return _size;
    }

    /**
     * Check if the deque is empty.
     * @return true if the deque is empty
     */
//    @Override
//    public boolean isEmpty() {
//        return _size == 0;
//    }

    /**
     * Get the item at specifc index from 0
     * @param index the index of accessing item, count from 0
     * @return null if index is invalid
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= _size) {
            return null;
        }
        return _items[_front + index];
    }

    /**
     * Resize of ArrayDeque has different situations on capacity.
     * 1. Extend the capacity.
     * 2. Shrink the capacity.
     * @param capacity the new capacity of deque
     */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];

        System.arraycopy(_items, _front, a, (capacity - _size) / 2, _size);
        _front = (capacity - _size) / 2;
        _rear = _front + _size;

        _items = a;
    }

    /**
     * Adding a new item from the front.
     * @param item item to be added
     */
    @Override
    public void addFirst(T item) {
        if (_front == 0) {
            resize(2 * _size);
        }

        _items[--_front] = item;
        ++_size;
    }

    /**
     * Adding a new item from the rear.
     * @param item item to be added
     */
    @Override
    public void addLast(T item) {
        if (_rear == _items.length) {
            resize(2 * _size);
        }

        _items[_rear++] = item;
        ++_size;
    }

    /**
     * Remove an item from the front, then check if resize is necessary.
     * @return null if the deque is empty
     */
    @Override
    public T removeFirst() {
        if (_size == 0) {
            return null;
        }
        T re = _items[_front];
        _items[_front++] = null;
        --_size;

        if (_size >= 4 && _size < _items.length / 4) {
            resize(_size * 2);
        } else if (_size == 0) {
            resize(DEFAULT_CAPACITY);
        }
        return re;
    }

    /**
     * Remove an item from the rear, then check if resize is necessary.
     * @return null if the deque is empty
     */
    @Override
    public T removeLast() {
        if (_size == 0) {
            return null;
        }
        T re = _items[--_rear];
        _items[_rear] = null;
        --_size;

        if (_size >= 4 && _size < _items.length / 4) {
            resize(_size * 2);
        } else if (_size == 0) {
            resize(DEFAULT_CAPACITY);
        }
        return re;
    }

    /**
     * Print the items in deque in order.
     * Print empty if the deque is empty.
     */
    @Override
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int _index;

        ArrayDequeIterator() {
            _index = 0;
        }

        @Override
        public boolean hasNext() {
            return _index < size();
        }

        @Override
        public T next() {
            return get(_index++);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        ArrayDeque<?> other = (ArrayDeque<?>) o;
        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < _size; ++i) {
            if (this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }
}
