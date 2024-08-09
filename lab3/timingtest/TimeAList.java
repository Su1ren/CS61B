package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    /**
     * Under bad Resizing strategy
     *            N     time (s)        # ops  microsec/op
     * ------------------------------------------------------------
     *         1000         0.00         1000         1.00
     *         2000         0.00         2000         1.50
     *         4000         0.01         4000         2.00
     *         8000         0.04         8000         4.50
     *        16000         0.10        16000         6.50
     *        32000         0.29        32000         9.19
     *        64000         0.84        64000        13.17
     *       128000         2.94       128000        22.95
     */
    public static void timeAListConstruction() {
        AList<Integer> size = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();

        for(int start = 1000; start <= 128000; start *= 2) {
            size.addLast(start);
            opCounts.addLast(start);
        }

        for(int s = 0; s < size.size(); s++) {
            int N = size.get(s);
            AList<Integer> L = new AList<Integer>();
            Stopwatch sw = new Stopwatch();
            for(int i = 0; i < N; i++) {
                L.addLast(i);
            }
            times.addLast(sw.elapsedTime());
        }

        printTimingTable(size, times, opCounts);
    }
}
