package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> L = new BuggyAList<Integer>();
        AListNoResizing<Integer> contrast = new AListNoResizing<Integer>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        contrast.addLast(1);
        contrast.addLast(2);
        contrast.addLast(3);

        assertEquals(L.removeLast(), contrast.removeLast());
        assertEquals(L.removeLast(), contrast.removeLast());
        assertEquals(L.removeLast(), contrast.removeLast());

        assertEquals(0, L.size());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<Integer>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }

    @Test
    public void randomizedTest2() {
        AListNoResizing<Integer> L = new AListNoResizing<Integer>();
        BuggyAList<Integer> B = new BuggyAList<Integer>();
        int N = 500;
        for(int i = 0;i < N;++i) {
            int operation = StdRandom.uniform(0, 4);
            if(operation == 0) {
                int rand = StdRandom.uniform(0, 100);
                L.addLast(rand);
                B.addLast(rand);
                System.out.println("L: addLast(" + rand + ")");
                System.out.println("B: addLast(" + rand + ")");
            } else if(operation == 1) {
                //int size = L.size();
                assertEquals(L.size(), B.size());
            } else if(operation == 2 && L.size() > 0) {
                assertEquals(L.removeLast(), B.removeLast());
            } else if(operation == 3 && L.size() > 0) {
                assertEquals(L.getLast(), B.getLast());
            }
        }
    }


}
