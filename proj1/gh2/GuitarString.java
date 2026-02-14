package gh2;

import deque.*;

// import deque.Deque;
//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /**
     * Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday.
     */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        this.buffer = new LinkedListDeque<Double>();
        int n = (int) Math.round((this.SR / frequency));
        for (int i = 0; i < n; i++) {
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        for (int i = 0; i < this.buffer.size(); i++) {
            double r = Math.random() - 0.5;
            this.buffer.removeFirst();
            this.buffer.addLast(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double a = this.buffer.removeFirst();
        this.buffer.addLast((a + this.buffer.get(0)) / 2 * this.DECAY);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return this.buffer.get(0);
    }
}
