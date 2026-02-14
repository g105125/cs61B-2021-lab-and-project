package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>,Iterable<T> {
    ListNode first;
    int size;
    public LinkedListDeque(){
        this.first=new ListNode();
        this.first.m_prior=this.first;
        this.first.m_next=this.first;
        this.size=0;
    }
    private class ListNode{
        public T m_value;
        public ListNode m_next;
        public ListNode m_prior;
        public ListNode(){
            this(null,null,null);
        }
        public ListNode(T value){
            this(value,null,null);
        }
        public ListNode(T value,ListNode prior,ListNode next){
            this.m_value=value;
            this.m_next=next;
            this.m_prior=prior;
        }
    }
    @Override
    public void addLast(T item){
        ListNode t=this.first.m_prior;
        t.m_next=new ListNode(item,t,this.first);
        this.first.m_prior=t.m_next;
        this.size++;
    }

    @Override
    public void addFirst(T item){
       this.first.m_next=new ListNode(item,this.first,this.first.m_next);
       this.first.m_next.m_next.m_prior=this.first.m_next;
       this.size++;
    }

    @Override
    public void printDeque(){
        ListNode t=this.first.m_next;
        while(t!=this.first){
            System.out.println(t.m_value);
            t=t.m_next;
        }

    }
    public T removeFirst(){
        if(this.isEmpty()){
            return null;
        }
        T ret=this.first.m_next.m_value;
        this.first.m_next.m_next.m_prior=this.first;
        this.first.m_next= this.first.m_next.m_next;
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
        T ret=this.first.m_prior.m_value;
        this.first.m_prior.m_prior.m_next=this.first;
        this.first.m_prior=this.first.m_prior.m_prior;
        this.size--;
        return ret;
    }
    public T get(int index){
        int i=0;
        ListNode t=this.first.m_next;
        while(i<this.size()&&i<index){
            t=t.m_next;
            i++;
        }
        if(i==index){
            return t.m_value;
        }
        else{
            return null;
        }
    }
    public boolean equals(Object o){
        if(o==null||!(o instanceof LinkedListDeque))
            return false;
        LinkedListDeque<T>l2=(LinkedListDeque<T>)o;
        if(this.size()!=l2.size()){
            return false;
        }
        int i=0;
        ListNode p=this.first.m_next;
        ListNode q=this.first.m_next;
        while(i<this.size()){
           if(!(p.m_value==q.m_value)){
               return false;
           }
           p=p.m_next;
           q=q.m_next;
            i++;
        }
        return true;
    }
    public Iterator<T> iterator(){
        return new LinkedListDequeiterator();
    }
    private  class LinkedListDequeiterator implements Iterator<T>{
        ListNode loca=first.m_next;

        @Override
        public boolean hasNext() {
            return !(loca==first);
        }

        @Override
        public T next() {
            T ret=loca.m_value;
            loca=loca.m_next;
            return ret;
        }
    }
    public T getRecursive(int index){
        if(index<0||index>=this.size()){
            return null;
        }
        return getRecursivet(index,this.first.m_next);
    }
    private T getRecursivet(int index,ListNode l){
        if(index==0){
            return l.m_value;
        }
        else{
            return getRecursivet(index-1,l.m_next);
        }
    }
}
