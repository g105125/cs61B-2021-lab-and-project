package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void randomizedTest() {
      AListNoResizing<Integer> L1 = new AListNoResizing<>();
      BuggyAList<Integer>L2=new BuggyAList<>();
      int N = 5000;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 4);
          if (operationNumber == 0) {
              // addLast
              int randVal = StdRandom.uniform(0, 100);
              L1.addLast(randVal);

              L2.addLast(randVal);

          } else if (operationNumber == 1) {
              // size
              int size1 = L1.size();

              int size2 = L2.size();

          }
          else if(operationNumber==2){
              if(L1.size()==0||L2.size()==0)continue;
              int last1=L1.getLast();

              int last2=L2.getLast();
          }
          else if(operationNumber==3){
              if(L1.size()==0||L2.size()==0)continue;
              int last1=L1.removeLast();
              int last2=L2.removeLast();

          }
      }

  }
}
