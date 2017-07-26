package goLA.compute.impl;

import goLA.compute.QueryProcessor;
import goLA.data.Tree;
import goLA.filter.Filter;
import goLA.model.Query;
import goLA.model.Trajectory;
import goLA.utils.DiscreteFrechetDistance;
import goLA.utils.FrechetDistance;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stem_dong on 2017-07-19.
 */
public class QueryProcessorImpl implements QueryProcessor {

    @Override
    public List<String> query(Query query, Tree tree, Filter filter) {
        if (trh.size() == 0) return new ArrayList<>();
        List<Trajectory> possible_trajectoryHolder = tree.getPossible(q);
        int size1 = possible_trajectoryHolder.size();
        System.out.println("---- candidate number : " + size1 + " -------");

        Instant middle1 = Instant.now();
        System.out.println("---- getPossible Time : " + Duration.between(start, middle1));

        List<Trajectory> filtered_list;
        Instant middle2;

        if (this.filter != null) {
            filtered_list = filter.doFilter(q, possible_trajectoryHolder);
            middle2 = Instant.now();
            System.out.println("---- Filtering Time : " + Duration.between(middle1, middle2));
            System.out.println("---- After Filtering number : " + filtered_list.size() + " -------");
        } else {
            filtered_list = possible_trajectoryHolder;
            middle2 = middle1;
        }
        q_processor.query(q, filtered_list);
        List<String> trajectories = trh
                .stream()
                .filter(t ->
                        t.isResult() || DiscreteFrechetDistance.decisionDP(query.getTrajectory(), t, query.getDistance()) ||
                                FrechetDistance.decisionDP(query.getTrajectory(), t, query.getDistance())
                )
                .map( e -> e.getName())
                .collect(Collectors.toList());

        return trajectories;
    }
}
