package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);
    T removeFirst();
    T removeLast();
    default boolean isEmpty() {
        return size() == 0;
    }
    int size();
    void printDeque();
    T get(int index);
}
