package deque;

public class LinkedListDeque<Item> {
    /**
     * Inside the Linked-list,
     * each Item is wrapped into a Node.
     */
    private class Node {
        public Item item;
        public Node next, prev;

        public Node(Item item, Node prev, Node next) {
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
        _sentinel = new Node(null,null, null);
        _sentinel.next = _sentinel;
        _sentinel.prev = _sentinel;
    }

    /**
     * Get the num of elements in the deque
     * @return the num of elements
     */
    public int size() {
        return _size;
    }

    /**
     * Check if the deque is empty
     * @return true if the deque is empty
     */
    public boolean isEmpty() {
        return _size == 0;
    }

    /**
     * Get the nth node in deque
     * Using iteration.
     * @return null if no such item
     */
    public Item get(int index) {
        if (index < 0 || index >= _size)
            return null;
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
    public void addLast(Item item) {
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
    public void addFirst(Item item) {
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
    public Item removeLast() {
        if (_size == 0)
            return null;
        Node last = _sentinel.prev;
        Item re = last.item;

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
    public Item removeFirst() {
        if (_size == 0)
            return null;
        Node first = _sentinel.next;
        Item re = first.item;

        first.next.prev = _sentinel;
        _sentinel.next = first.next;
        --_size;
        return re;
    }

    /**
     * Print the deque in order for debugging.
     * Note that loop ends at _sentinel
     */
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
    public Item getRecursive(int index) {
        if(index < 0 || index >= _size)
            return null;
        return _getRecursive(_sentinel.next, index);
    }

    /**
     * Private interface of recursion
     * @param p the current node
     * @param index the index of elem to access, start from 0
     * @return the item at specific index
     */
    private Item _getRecursive(Node p, int index) {
        if (index == 0)
            return p.item;
        return _getRecursive(p.next, index - 1);
    }
}
