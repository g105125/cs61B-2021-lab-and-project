package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T ret = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            if (c.compare(this.get(i), ret) > 0) {
                ret = this.get(i);
            }
        }
        return ret;
    }
}
