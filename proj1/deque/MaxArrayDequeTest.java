package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    private static class IntegerComparator implements Comparator<Integer> {

        /**
         * Judge if lhs is greater than rhs
         * @param lhs the first Integer to be compared.
         * @param rhs the second Integer to be compared.
         * @return positive, zero, negative if greater than, equal to, less than
         */
        @Override
        public int compare(Integer lhs, Integer rhs) {
            return lhs - rhs;
        }
    }

    private static class StringComparator implements Comparator<String> {
        /**
         * Judge if lhs if greater than rhs
         * @param lhs the first String to be compared
         * @param rhs the second String to be compared
         * @return positive, zero, negative if greater than, equal to, less than
         */
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs); // kind of short-cut
        }
    }

    private static class StringLengthComparator implements Comparator<String> {
        /**
         * Judge if lhs if greater than rhs
         * @param lhs the first String to be compared
         * @param rhs the second String to be compared
         * @return positive, zero, negative if greater than, equal to, less than
         */
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.length() - rhs.length();
        }
    }

    @Test
    public void maxTestWithDefaultComparator() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntegerComparator());
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals((Integer)3, deque.max());
    }

    @Test
    public void maxTestWithGivenComparator() {
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(new StringComparator());
        deque.addLast("Ego");
        deque.addLast("hena");
        deque.addLast("von");
        deque.addLast("merci");

        assertEquals("von", deque.max());
        assertEquals("merci", deque.max(new StringLengthComparator()));
    }
}
