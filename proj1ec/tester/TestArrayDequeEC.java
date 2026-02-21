package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomtest() {
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sad2 = new ArrayDequeSolution<>();
        String message = "";
        for (int i = 0; i < 1000; i += 1) {
            int n = StdRandom.uniform(0, 4);
            if (n == 0) {
                sad1.addLast(i);
                sad2.addLast(i);
                message += "addLast(" + i + ")\n";
            } else if (n == 1) {
                sad1.addFirst(i);
                sad2.addFirst(i);
                message += "addFirst(" + i + ")\n";
            } else if (n == 2) {
                Integer e1 = null, e2 = null;
                if (!sad1.isEmpty())
                    e1 = sad1.removeFirst();
                if (!sad2.isEmpty())
                    e2 = sad2.removeFirst();
                assertEquals(message += "removeFirst()\n", e2, e1);
            } else if (n == 3) {
                Integer e1 = null, e2 = null;
                if (!sad1.isEmpty())
                    e1 = sad1.removeLast();
                if (!sad2.isEmpty())
                    e2 = sad2.removeLast();
                assertEquals(message += "removeLast()\n", e2, e1);
            }
        }
    }
}
