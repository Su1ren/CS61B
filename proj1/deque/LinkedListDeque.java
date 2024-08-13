package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * Inside the Linked-list,
     * each Item is wrapped into a Node.
     */
    private class Node {
        private T item;
        private Node next, prev;

        Node(T item, Node prev, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * The circular linklist deque
     */
    private Node _sentinel;
    private int _size;

    /**
     * Default constructor
     * Create an empty linklist apart from a sentinel
     */
    public LinkedListDeque() {
        _size = 0;
        _sentinel = new Node(null, null, null);
        _sentinel.next = _sentinel;
        _sentinel.prev = _sentinel;
    }

    /**
     * Get the num of elements in the deque
     * @return the num of elements
     */
    @Override
    public int size() {
        return _size;
    }

    /**
     * Check if the deque is empty
     * @return true if the deque is empty
     */
//    @Override
//    public boolean isEmpty() {
//        return _size == 0;
//    }

    /**
     * Get the nth node in deque
     * Using iteration.
     * @return null if no such item
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= _size) {
            return null;
        }
        Node p = _sentinel.next;
        int i = 0;
        while (i != index) {
            p = p.next;
            ++i;
        }
        return p.item;
    }

    /**
     * Adding an item at the rear of deque.
     * @param item element to be added
     */
    @Override
    public void addLast(T item) {
        Node last = _sentinel.prev;
        Node newNode = new Node(item, last, _sentinel);
        last.next = newNode;
        _sentinel.prev = newNode;
        ++_size;
    }

    /**
     * Adding an item at the front of deque.
     * @param item element to be added
     */
    @Override
    public void addFirst(T item) {
        Node first = _sentinel.next;
        Node newNode = new Node(item, _sentinel, first);
        first.prev = newNode;
        _sentinel.next = newNode;
        ++_size;
    }

    /**
     * Remove an item from the Last of deque return it.
     * Gc will recycle the removed item since it has no reference to it anymore.
     * @return null if the deque is empty
     */
    @Override
    public T removeLast() {
        if (_size == 0) {
            return null;
        }
        Node last = _sentinel.prev;
        T re = last.item;

        last.prev.next = _sentinel;
        _sentinel.prev = last.prev;
        --_size;
        return re;
    }

    /**
     * Remove the first item of deque and return it.
     * Gc will recycle the removed item since it has no reference to it anymore.
     * @return null if the deque is empty
     */
    @Override
    public T removeFirst() {
        if (_size == 0) {
            return null;
        }
        Node first = _sentinel.next;
        T re = first.item;

        first.next.prev = _sentinel;
        _sentinel.next = first.next;
        --_size;
        return re;
    }

    /**
     * Print the deque in order for debugging.
     * Note that loop ends at _sentinel
     */
    @Override
    public void printDeque() {
        if (_size == 0) {
            System.out.println("Empty");
            return;
        }
        Node p = _sentinel.next;
        while (p != _sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    /**
     * Recursively get the nth item in the deque
     * @param index the index of elem to access, start from 0
     * @return null if no such item
     */
    public T getRecursive(int index) {
        if (index < 0 || index >= _size) {
            return null;
        }
        return getRecursiveHelper(_sentinel.next, index);
    }

    /**
     * Private interface of recursion
     * @param p the current node
     * @param index the index of elem to access, start from 0
     * @return the item at specific index
     */
    private T getRecursiveHelper(Node p, int index) {
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(p.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node _p = _sentinel;

        LinkedListDequeIterator() {
            assert _p != null;
            _p = _sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return _p != _sentinel;
        }

        @Override
        public T next() {
            T re = _p.item;
            _p = _p.next;
            return re;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        Deque<?> other = (Deque<?>) o;
        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < _size; ++i) {
//            if (this.get(i) != other.get(i)) { // basic equality check
//                return false;
//            }
            if (!(this.get(i).equals(other.get(i)))) { // deep equality check
                return false;
            }
        }
        return true;
    }
}
