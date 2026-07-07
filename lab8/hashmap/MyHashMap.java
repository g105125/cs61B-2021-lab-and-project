package hashmap;

import java.util.*;

import static java.lang.Math.abs;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Removes all of the mappings from this map.
     */
    private Set<K> keysetStorage;
    private int bucketsSize;
    @Override
    public void clear() {
        for(int i=0;i<this.bucketsSize;i++){
            Collection<Node>bucket=this.buckets[i];
            bucket.clear();
        }
        this.size=0;
        return;
    }
    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key
     */
    @Override
    public boolean containsKey(K key) {
        boolean ret=false;
        Collection<Node>bucket=this.buckets[this.gethash(key)];
        for(Node nd:bucket){
            if(nd.key.equals(key)){
                ret=true;
                break;
            }
        }
        return ret;
    }
    private int gethash(K key){
        return abs(key.hashCode())%this.bucketsSize;
    }
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key
     */
    @Override
    public V get(K key) {
        V ret=null;
        Collection<Node>bucket=this.buckets[this.gethash(key)];
        for (Node nd : bucket) {
            if (nd.key.equals(key)) {
                ret = nd.value;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        boolean exists=false;
        if((double)(this.size+1)/this.bucketsSize>this.maxLoad){
            this.resize(this.bucketsSize*2);
        }
        Collection<Node>bucket=this.buckets[this.gethash(key)];
        for(Node nd:bucket){
            if(nd.key.equals(key)){
                nd.value=value;
                exists=true;
                break;
            }
        }
        if(!exists){
            Node newnode=new Node(key,value);
            bucket.add(newnode);
            this.size++;
            this.keysetStorage.add(key);
        }
    }
    public void resize(int newbucketssize){
        this.bucketsSize=newbucketssize;
        Collection<Node>[]newbuckets=createTable(newbucketssize);
        for(Collection<Node>bucket:this.buckets){
            for(Node oldnd:bucket){
                Node newnd=createNode(oldnd.key,oldnd.value);
                newbuckets[gethash(oldnd.key)].add(newnd);
            }
        }
        this.buckets=newbuckets;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        return this.keysetStorage;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     *
     * @param key
     */
    @Override
    public V remove(K key) {
        V ret=null;
        int hash=this.gethash(key);
        Collection<Node>bucket=this.buckets[hash];
        Node delnd=null;
        for(Node nd:bucket){
            if(nd.key.equals(key)){
                delnd=nd;
            }
        }
        if(delnd!=null){
            ret=delnd.value;
            bucket.remove(delnd);
            this.size--;
        }
        return ret;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     *
     * @param key
     * @param value
     */
    @Override
    public V remove(K key, V value) {
        V ret=null;
        int hash=this.gethash(key);
        Collection<Node>bucket=this.buckets[hash];
        Node delnd=null;
        for(Node nd:bucket){
            if(nd.key.equals(key)){
                delnd=nd;
            }
        }
        if(delnd!=null&&delnd.value.equals(value)){
            ret=delnd.value;
            bucket.remove(delnd);
            this.size--;
        }
        return ret;
    }

    @Override
    public Iterator<K> iterator() {
        return this.keysetStorage.iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private double maxLoad;
    private int size;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16,0.75);
    }

    public MyHashMap(int initialSize) {
       this(initialSize,0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets=createTable(initialSize);
        this.maxLoad=maxLoad;
        this.size=0;
        this.bucketsSize=initialSize;
        this.keysetStorage=new HashSet<K>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] collections=new Collection[tableSize];
        for(int i=0;i<tableSize;i++){
            collections[i]=createBucket();
        }
        return collections;
    }


}
