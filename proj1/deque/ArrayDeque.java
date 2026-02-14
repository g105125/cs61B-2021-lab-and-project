package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int first;
    private int rear;
    private int maxsize;
    private T[] items;

    public ArrayDeque() {
        this.items = (T[]) new Object[8];
        this.size = 0;
        this.first = 0;
        this.rear = 0;
        this.maxsize = 8;
    }

    @Override
    public void addLast(T item) {
        if (this.size == maxsize) {
            this.resize((int) (this.maxsize * 1.15));
        }
        this.items[this.rear] = item;
        this.rear = (this.rear + 1) % this.maxsize;
        this.size++;
    }

    private void resize(int newmaxsize) {
        if (newmaxsize < 8) {
            return;
        }
        @SuppressWarnings("unchecked") T[] newitems = (T[]) new Object[newmaxsize];
        int i = 0;
        while (i < this.size) {
            newitems[i] = this.get(i);
            i++;
        }
        this.items = newitems;
        this.maxsize = newmaxsize;
        this.first = 0;
        this.rear = this.size();
    }

    @Override
    public void addFirst(T item) {
        if (this.size == maxsize) {
            this.resize((int) (this.maxsize * 1.15));
        }
        this.first = (this.first - 1 + this.maxsize) % this.maxsize;
        this.items[this.first] = item;
        this.size++;
    }

    @Override
    public void printDeque() {
        for (int i = this.first; i < this.rear; i = (i + 1) % this.maxsize) {
            System.out.println(this.items[i]);
        }
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        if (this.size <= this.maxsize * 0.25) {
            this.resize((int) ((this.size - 1) * 1.15));
        }
        T ret = this.items[this.first];
        this.first = (this.first + 1) % this.maxsize;
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
        if (this.size <= this.maxsize * 0.25) {
            this.resize((int) ((this.size - 1) * 1.15));
        }
        this.rear = (this.rear - 1 + this.maxsize) % this.maxsize;
        this.size--;
        return this.items[this.rear];
    }

    public T get(int index) {
        if (index < 0 || index >= this.size()) {
            return null;
        }
        return this.items[(this.first + index) % this.maxsize];
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque)) {
            return false;
        }
        Deque<T> deque2 = (Deque<T>) o;
        if (this.size() != deque2.size()) {
            return false;
        }
        int i = 0;
        while (i < this.size()) {
            if (!(this.get(i).equals(deque2.get(i)))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeiterator();
    }

    private class ArrayDequeiterator implements Iterator<T> {
        int loca = 0;

        @Override
        public boolean hasNext() {
            return loca < size();
        }

        @Override
        public T next() {
            T ret = items[(this.loca + first) % maxsize];
            loca++;
            return ret;
        }
    }
}
