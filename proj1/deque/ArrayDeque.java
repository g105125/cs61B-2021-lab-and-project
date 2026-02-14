package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T> {
    int size;
    int maxsize;
    T[] items;
    public ArrayDeque(){
        this.items=(T[])new Object[8];
        this.size=0;
        this.maxsize=8;
    }
    @Override
    public void addLast(T item){
        if(this.size==maxsize){
            this.resize((int)(this.maxsize*1.15));
        }
        this.items[this.size++]=item;
    }
    private void resize(int newmaxsize){
        if(newmaxsize<8){
            return;
        }
        @SuppressWarnings("unchecked") T[]newitems=(T[])new Object[newmaxsize];
        int i=0;
        while(i<this.size){
            newitems[i]=this.items[i];
            i++;
        }
        this.items=newitems;
        this.maxsize=newmaxsize;
    }
    @Override
    public void addFirst(T item){
        if(this.size==maxsize){
            this.resize((int)(this.maxsize*1.15));
        }
        for(int i=this.size;i>0;i--) {
            this.items[i] = this.items[i - 1];
        }
        this.items[0]=item;
        this.size++;
    }

    @Override
    public void printDeque(){
        for(int i=0;i<this.size;i++){
            System.out.println(this.items[i]);
        }
    }
    public T removeFirst(){
        if(this.isEmpty()){
            return null;
        }
        if(this.size<=this.maxsize*0.25){
            this.resize((int) ((this.size-1)*1.15));
        }
       T ret=this.items[0];
        for(int i=1;i<this.size;i++){
            this.items[i-1]=this.items[i];
        }
        this.size--;
        return ret;
    }
    public int size(){
        return this.size;
    }
    public T removeLast(){
        if(this.isEmpty()){
            return null;
        }
        if(this.size<=this.maxsize*0.25){
            this.resize((int)((this.size-1)*1.15));
        }
        return this.items[(this.size--)-1];
    }
    public T get(int index){
        if(index<0||index>=this.size()){
            return null;
        }
        return this.items[index];
    }
    public boolean equals(Object o){
        if(o==null||!(o instanceof ArrayDeque)){
            return false;
        }
        ArrayDeque<T>deque2=(ArrayDeque<T>)o;
        if(this.size()!=deque2.size()){
            return false;
        }
        int i=0;
        while(i<this.size()){
            if(!(this.items[i]==deque2.items[i])){
                return false;
            }
            i++;
        }
        return true;
    }
    public Iterator<T> iterator(){
        return new ArrayDequeiterator();
    }
    private  class ArrayDequeiterator implements Iterator<T>{
        int loca=0;

        @Override
        public boolean hasNext() {
            return loca<size();
        }
        @Override
        public T next() {
            return items[this.loca++];
        }
    }
}
