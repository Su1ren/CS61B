package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    /**
     *            N     time (s)        # ops  microsec/op
     * ------------------------------------------------------------
     *         1000         0.01        10000         1.30
     *         2000         0.03        10000         3.10
     *         4000         0.05        10000         5.10
     *         8000         0.12        10000        11.90
     *        16000         0.27        10000        27.30
     *        32000         0.47        10000        47.10
     *        64000         1.31        10000       130.80
     *       128000         2.47        10000       246.60
     */
    public static void timeGetLast() {
        AList<Integer> size = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();

        for(int start = 1000; start <= 128000; start *= 2) {
            size.addLast(start);
            opCounts.addLast(10000);
        }

        for(int i = 0;i < size.size();++i) {
            int N = size.get(i);
            SLList<Integer> sll = new SLList<Integer>();
            for(int j = 0;j < N;++j) {
                sll.addFirst(j);
            }
            Stopwatch sw = new Stopwatch();
            for(int j = 0;j < opCounts.get(i);++j)
                sll.getLast();
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(size, times, opCounts);
    }

}
