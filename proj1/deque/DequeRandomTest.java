package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

public class DequeRandomTest {
    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L1.addLast(randVal);

                L2.addLast(randVal);

            } else if (operationNumber == 1) {
                // size
                int size1 = L1.size();

                int size2 = L2.size();

            } else if (operationNumber == 2) {
                int randVal = StdRandom.uniform(0, 100);
                Integer last1 = L1.get(randVal);

                Integer last2 = L2.get(randVal);
            } else if (operationNumber == 3) {
                Integer last1 = L1.removeLast();
                Integer last2 = L2.removeLast();
            } else if (operationNumber == 5) {
                Integer last1 = L1.removeFirst();
                Integer last2 = L2.removeFirst();
            } else if (operationNumber == 4) {
                int randVal = StdRandom.uniform(0, 100);
                L1.addFirst(randVal);

                L2.addFirst(randVal);

            } else if (operationNumber == 6) {
                L1.printDeque();
                L2.printDeque();
            }
        }
    }
}
