package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private ListNode first;
    private int size;

    public LinkedListDeque() {
        this.first = new ListNode();
        this.first.prior = this.first;
        this.first.next = this.first;
        this.size = 0;
    }

    @Override
    public void addLast(T item) {
        ListNode t = this.first.prior;
        t.next = new ListNode(item, t, this.first);
        this.first.prior = t.next;
        this.size++;
    }

    @Override
    public void addFirst(T item) {
        this.first.next = new ListNode(item, this.first, this.first.next);
        this.first.next.next.prior = this.first.next;
        this.size++;
    }

    @Override
    public void printDeque() {
        ListNode t = this.first.next;
        while (t != this.first) {
            System.out.println(t.value);
            t = t.next;
        }

    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        T ret = this.first.next.value;
        this.first.next.next.prior = this.first;
        this.first.next = this.first.next.next;
        this.size--;
        return ret;
    }

    public int size() {
        return this.size;
    }

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        T ret = this.first.prior.value;
        this.first.prior.prior.next = this.first;
        this.first.prior = this.first.prior.prior;
        this.size--;
        return ret;
    }

    public T get(int index) {
        int i = 0;
        ListNode t = this.first.next;
        while (i < this.size() && i < index) {
            t = t.next;
            i++;
        }
        if (i == index) {
            return t.value;
        } else {
            return null;
        }
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque))
            return false;
        Deque<T> l2 = (Deque<T>) o;
        if (this.size() != l2.size()) {
            return false;
        }
        int i = 0;
        while (i < this.size()) {
            if (this.get(i) != l2.get(i)) {
                return false;
            }
            i++;
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeiterator();
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= this.size()) {
            return null;
        }
        return getRecursivet(index, this.first.next);
    }

    private T getRecursivet(int index, ListNode l) {
        if (index == 0) {
            return l.value;
        } else {
            return getRecursivet(index - 1, l.next);
        }
    }

    private class ListNode {
        private T value;
        private ListNode next;
        private ListNode prior;

        private ListNode() {
            this(null, null, null);
        }

        private ListNode(T value) {
            this(value, null, null);
        }

        private ListNode(T value, ListNode prior, ListNode next) {
            this.value = value;
            this.next = next;
            this.prior = prior;
        }
    }

    private class LinkedListDequeiterator implements Iterator<T> {
        ListNode loca = first.next;

        @Override
        public boolean hasNext() {
            return !(loca == first);
        }

        @Override
        public T next() {
            T ret = loca.value;
            loca = loca.next;
            return ret;
        }
    }
}
