package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    int m_size;
    int m_maxsize;
    T[] m_items;
    public ArrayDeque(){
        //noinspection unchecked
        this.m_items=(T[])new Object[8];
       this.m_size=0;
       this.m_maxsize=8;
    }
    @Override
    public void addLast(T item){
        if(m_size==m_maxsize){
            this.resize((int)(this.m_maxsize*1.15));
        }
        this.m_items[this.m_size++]=item;
    }
    public void resize(int newmaxsize){
        if(newmaxsize<8){
            return;
        }
        @SuppressWarnings("unchecked") T[]newitems=(T[])new Object[newmaxsize];
        int i=0;
        while(i<this.m_size){
            newitems[i]=this.m_items[i];
            i++;
        }
        this.m_items=newitems;
        this.m_maxsize=newmaxsize;
    }
    @Override
    public void addFirst(T item){
        if(m_size==m_maxsize){
            this.resize((int)(this.m_maxsize*1.15));
        }
        for(int i=this.m_size;i>0;i--) {
            this.m_items[i] = this.m_items[i - 1];
        }
        this.m_items[0]=item;
        this.m_size++;
    }

    @Override
    public void printDeque(){
        for(int i=0;i<this.m_size;i++){
            System.out.println(this.m_items[i]);
        }
    }
    public T removeFirst(){
        if(this.isEmpty()){
            return null;
        }
        if(this.m_size<=this.m_maxsize*0.25){
            this.resize((int) ((this.m_size-1)*1.15));
        }
       T ret=this.m_items[0];
        for(int i=1;i<this.m_size;i++){
            this.m_items[i-1]=this.m_items[i];
        }
        this.m_size--;
        return ret;
    }
    public int size(){
        return this.m_size;
    }
    public T removeLast(){
        if(this.isEmpty()){
            return null;
        }
        if(this.m_size<=this.m_maxsize*0.25){
            this.resize((int)((this.m_size-1)*1.15));
        }
        return this.m_items[(this.m_size--)-1];
    }
    public T get(int index){
        if(index<0||index>=this.size()){
            return null;
        }
        return this.m_items[index];
    }
    public boolean equals(Object o){
        ArrayDeque<T>deque2=(ArrayDeque<T>)o;
        if(this.size()!=deque2.size()){
            return false;
        }
        int i=0;
        while(i<this.size()){
            if(!(this.m_items[i]==deque2.m_items[i])){
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
            return m_items[this.loca++];
        }
    }
}
