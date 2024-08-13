package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    /**
     * Note that we don't need to set MaxArrayDeque as Comparable.
     * Since the compared object is T Object rather than the deque.
     */


    /* The comparator initialized for the deque */
    private Comparator<T> _comparator;

    /**
     * Create a new MaxArrayDeque with given Comparator
     * @param c comparator for the deque
     */
    public MaxArrayDeque(Comparator<T> c) {
        _comparator = c;
    }

    /**
     * Return the max item in the deque
     * Under the criteria of the previously given comparator
     * @return the max item, null if the deque is empty
     */
    public T max() {
        return max(_comparator);
    }

    /**
     * Return the max item in the deque according to the given comparator c
     * @param c The provided comparator
     * @return the max item under the criteria of c, null if the deque is empty
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        int idx = 0;
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), get(idx)) > 0) {
                idx = i;
            }
        }
        return get(idx);
    }
}
