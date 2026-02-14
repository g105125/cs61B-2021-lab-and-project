package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    Comparator<T> m_c;
    public MaxArrayDeque(Comparator<T> c){
        m_c=c;
    }
    public T max(){
        if(this.isEmpty()){
            return null;
        }
        T ret=this.m_items[0];
        for(int i=1;i<this.size();i++){
            if(m_c.compare(m_items[i],ret)==1){
                ret=m_items[i];
            }
        }
        return ret;
    }
    public T max(Comparator<T> c){
        if(this.isEmpty()){
            return null;
        }
        T ret=this.m_items[0];
        for(int i=1;i<this.size();i++){
            if(c.compare(m_items[i],ret)==1){
                ret=m_items[i];
            }
        }
        return ret;
    }
}
