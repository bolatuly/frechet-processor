import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDList;
import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDListIter;
import io.github.stemlab.data.impl.TestIndexImpl;
import io.github.stemlab.io.Importer;
import io.github.stemlab.logic.DiscreteFrechet;
import io.github.stemlab.logic.RealFrechet;
import io.github.stemlab.logic.StraightForwardSimplification;
import io.github.stemlab.model.Coordinate;
import io.github.stemlab.model.Query;
import io.github.stemlab.model.Trajectory;
import io.github.stemlab.utils.*;

/**
 * Created by stem-dong-li on 17. 7. 6.
 */
public class SimplificationTest {
    private static double MAX_RAN = 40000;
    private static double MIN_RAN = 45000;

    public static void main(String[] args) {
        TestIndexImpl tree = new TestIndexImpl();
        Importer di = new Importer();
        di.loadFiles("dataset.txt", tree);
        System.out.println("--- Complete put All data in Tree ---");
        for (int i = 0; i < 300; i++) {
            int index = (int) (Math.random() * (tree.size() - 1));
            double dist = MIN_RAN + (Math.random() * MAX_RAN);
            Trajectory q = (Trajectory) tree.holder.values().toArray()[index];
            System.out.println("--- " + i + " : " + index + " ---");
            System.out.println("dist : " + dist);

            Query query = new Query(q, dist);
            Coordinate start = query.getTrajectory().getCoordinates().get(0);
            Coordinate end = query.getTrajectory().getCoordinates().get(query.getTrajectory().getCoordinates().size() - 1);

            DoubleDBIDList result = tree.rStarTree.search(new double[]{start.getPointX(), start.getPointY()}, dist);

            Trajectory simple = StraightForwardSimplification.getReduced(query.getTrajectory(), dist);
            q.setSimplified(simple);
            for (DoubleDBIDListIter x = result.iter(); x.valid(); x.advance()) {
                Trajectory trajectory = tree.holder.get(tree.rStarTree.getRecordName(x));
                Coordinate last = trajectory.getCoordinates().get(trajectory.getCoordinates().size() - 1);
                if (EuclideanDistance.distance(last, end) <= dist) {
                    Trajectory simple_query = query.getTrajectory().getSimplified();
                    Trajectory simple_trajectory = StraightForwardSimplification.getReduced(trajectory, query.getDistance());
                    double modified_dist = dist + 2 * dist * StraightForwardSimplification.EPSILON * StraightForwardSimplification.CONSTANT;
                    double modified_dist2 = dist - 1 * dist * StraightForwardSimplification.EPSILON * StraightForwardSimplification.CONSTANT;
                    if (DiscreteFrechet.decision(simple_query, trajectory, modified_dist2)){
                        if (RealFrechet.decision(q, trajectory, dist)) {

                        } else {
                            System.out.println("DiscreteFrechet is Result wrong");
                        }
                    }
                    if (!RealFrechet.decision(simple_query, simple_trajectory, modified_dist)){
                        if (!RealFrechet.decision(q, trajectory, dist)) {

                        } else {
                            System.out.println("wrong");
                        }
                    }
                    if (RealFrechet.decision(simple_query, trajectory, modified_dist2) ){
                        if (RealFrechet.decision(q, trajectory, dist)) {

                        } else {
                            System.out.println("RealFrechet is Result wrong");
                        }
                    }

                }

            }
        }
    }

}