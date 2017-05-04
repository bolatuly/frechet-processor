package goLA.data;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import goLA.model.Coordinates;
import goLA.model.Trajectory;
import goLA.model.TrajectoryHolder;
import goLA.model.TrajectoryQuery;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stem_dong on 2017-05-02.
 * UPPER LEFT is (0,0)
 */
public class Start_End_Rtree implements Tree {
    public RTree<String, Point> start_tree;
    public RTree<String, Point> end_tree;

    public Start_End_Rtree(){
        start_tree = RTree.star().create();
        end_tree = RTree.star().create();
    }

    @Override
    public void addTrajectory(String id, Trajectory tr) {
        List<Coordinates<Double,Double>> list = tr.getCoordinates();

        Coordinates<Double,Double> start = list.get(0);
        start_tree = start_tree.add(id, Geometries.point(start.getPointX(), start.getPointY()));

        Coordinates<Double,Double> end = list.get(list.size()-1);
        end_tree = end_tree.add(id, Geometries.point(end.getPointX(), end.getPointY()));
    }

    @Override
    public TrajectoryHolder getPossible(TrajectoryQuery query, TrajectoryHolder origin) {
        Coordinates<Double, Double> q_start = query.getTrajectory().getCoordinates().get(0);
        Coordinates<Double, Double> q_end = query.getTrajectory().getCoordinates().get(query.getTrajectory().getCoordinates().size() - 1);
        double dist = query.dist;

        Observable<Entry<String, Point>> s_results = start_tree.search(Geometries.point(q_start.getPointX(),q_start.getPointY()), dist);
        Observable<Entry<String, Point>> e_results = end_tree.search(Geometries.point(q_end.getPointX(),q_end.getPointY()), dist);

        Map<String, Integer> count = new HashMap<String, Integer>();

        TrajectoryHolder poss = new TrajectoryHolder();

        s_results.forEach(
                e->{
                    count.put(e.value(),1);
                }
        );

        e_results.forEach(e->{
            count.put(e.value(),1);
        });

        count.forEach((e,v)->{
            poss.addTrajectory(e,origin.getTrajectories().get(e));
        });

        return poss;
    }
}