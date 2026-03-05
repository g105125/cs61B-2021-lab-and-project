package flik;
import org.junit.Test;
import static flik.Flik.isSameNumber;
import static org.junit.Assert.*;
public class testa{
    @Test
    public void test1(){
        assertTrue(isSameNumber((Integer)(-129),(Integer)(-129)));
    }
    @Test
    public void test2(){
        assertTrue(!isSameNumber(1,0));
    }
}
