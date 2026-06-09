package bstmap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V>
{
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Performs the given action for each element of the {@code Iterable}
     * until all elements have been processed or the action throws an
     * exception.  Actions are performed in the order of iteration, if that
     * order is specified.  Exceptions thrown by the action are relayed to the
     * caller.
     * <p>
     * The behavior of this method is unspecified if the action performs
     * side-effects that modify the underlying source of elements, unless an
     * overriding class has specified a concurrent modification policy.
     *
     */
    private class node{
        public K key;
        public V value;
        public node left;
        public node right;
        public node(K key,V value){
            this.key=key;
            this.value=value;
            this.left=null;
            this.right=null;
        }
    }
    public node root;
    public int size;
    public BSTMap(){
        this.size=0;
        this.root=null;
    }
    @Override
    public void clear(){
       this.root=null;
       this.size=0;
    }

    @Override
    public boolean containsKey(K key) {
        node t=this.root;
        while(t!=null){
            int c=key.compareTo(t.key);
            if(c<0){
                t=t.left;
            }
            else if(c>0){
                t=t.right;
            }
            else{
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        V ret=null;
        node t=this.root;
        while(t!=null){
            int c=key.compareTo(t.key);
            if(c==0){
                ret=t.value;
                break;
            }
            else if(c<0){
                t=t.left;
            }
            else{
                t=t.right;
            }
        }
        return ret;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        if(this.root==null){
            this.root=new node(key,value);
            this.size++;
            return;
        }
        node t=this.root;
        while(true){
            int c=t.key.compareTo(key);
            if(c<0){
                if(t.right==null){
                    t.right=new node(key,value);
                    this.size++;
                    break;
                }
                else{
                    t=t.right;
                }
            }
            else if(c>0){
                if(t.left==null){
                    t.left=new node(key,value);
                    this.size++;
                    break;
                }
                else{
                    t=t.left;
                }
            }
            else{
                t.value=value;
                break;
            }
        }
    }


    @Override
    public Set keySet() {
        TreeSet<K> ret=new TreeSet<K>();
        keysetf(ret,this.root);
        return ret;
    }
    private void keysetf(TreeSet<K> st,node root){
        if(root==null){
            return;
        }
        keysetf(st,root.left);
        st.add(root.key);
        keysetf(st,root.right);
    }
    @Override
    public V remove(K key) {
        node t=this.root;
        node prior=null;
        while(t!=null){
            int c=key.compareTo(t.key);
            if(c<0){
                prior=t;
                t=t.left;
            }
            else if(c>0){
                prior=t;
                t=t.right;
            }
            else{
               rm(t,prior);
               this.size--;
               return t.value;
            }
        }
        return null;
    }
    private void rm(node t,node prior){
        node l=t.left;
        node r=t.right;
        if(t==this.root){
            if(l==null&&r==null){
                this.root=null;
            }
            else if(l==null){
                this.root=r;
            }
            else if(r==null){
                this.root=l;
            }
            else{
                getrightestnode(l).right=r;
                this.root=l;
            }
        }
        else{
            node newroot=null;
            if(l==null&&r==null){
                newroot=null;
            }
            else if(l==null){
                newroot =r;
            }
            else if(r==null){
                newroot=l;
            }
            else{
                getrightestnode(l).right=r;
                newroot=l;
            }
            int c=prior.key.compareTo(t.key);
            if(c>0){
                prior.left=newroot;
            }
            else{
                prior.right=newroot;
            }
        }
    }
    private node getrightestnode(node t){
        while(t.right!=null){
            t=t.right;
        }
        return t;
    }

    @Override
    public V remove(K key, V value) {
        node t=this.root;
        node prior=null;
        while(t!=null){
            int c=key.compareTo(t.key);
            if(c<0){
                prior=t;
                t=t.left;
            }
            else if(c>0){
                prior=t;
                t=t.right;
            }
            else{
                if(t.value==value){
                    rm(t,prior);
                    this.size--;
                    return t.value;
                }
                else{
                    return null;
                }

            }
        }
        return null;
    }
    public void printInOrder(){
        node t=this.root;
        midroot(t);
    }
    private void midroot(node root){
        if(root==null)return;
        midroot(root.left);
        System.out.println(root.key);
        midroot(root.right);
    }

}
