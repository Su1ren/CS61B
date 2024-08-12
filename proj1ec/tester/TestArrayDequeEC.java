package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    /**
     * The test randomly calls methods StudentArrayDeque and ArrayDequeSolution
     * Until they disagree on an output.
     */
    @Test
    public void randomTest() {
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
        int N = 10000;
        StringBuilder msg = new StringBuilder();

        for (int i = 0; i < N; ++i) {
            int ops = StdRandom.uniform(0,5);
            switch (ops) {
                case 0:
                    int rand = StdRandom.uniform(0, 100);
                    sad.addFirst(rand);
                    ads.addFirst(rand);
                    msg.append("addFirst(").append(rand).append(")\n");
                    //assertEquals(msg.toString(), ads.get(0), sad.get(0));
                    break;
                case 1:
                    int rand2 = StdRandom.uniform(0, 100);
                    sad.addLast(rand2);
                    ads.addLast(rand2);
                    msg.append("addLast(").append(rand2).append(")\n");
                    //assertEquals(msg.toString(), ads.get(ads.size() - 1), sad.get(sad.size() - 1));
                    break;
                case 2:
                    if (!sad.isEmpty()) {
                        Integer actual = sad.removeFirst();
                        Integer expected = ads.removeFirst();
                        msg.append("removeFirst()\n");
                        assertEquals(msg.toString(), expected, actual);
                    }
                    break;
                case 3:
                    if (!sad.isEmpty()) {
                        Integer actual = sad.removeLast();
                        Integer expected = ads.removeLast();
                        msg.append("removeLast()\n");
                        assertEquals(msg.toString(), expected, actual);
                    }
                default:
                    msg.append("size()\n");
                    assertEquals(msg.toString(), ads.size(), sad.size());
            }
        }
    }
}
