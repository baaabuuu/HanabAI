package monte_carlo;

import java.util.Collections;
import java.util.Comparator;

public class UCT {
    public static double uctValue(
      int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit)
          + Math.sqrt(2*Math.log(totalVisit) / (double) nodeVisit);
    }
 
    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getAvgState().getVisitCount();
        return Collections.max(
          node.getChildArray(),
          Comparator.comparing(c -> uctValue(parentVisit,c.getAvgState().getWinScore(), c.getAvgState().getVisitCount())));
    }
}