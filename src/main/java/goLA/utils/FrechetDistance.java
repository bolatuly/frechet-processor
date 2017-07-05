package goLA.utils;

import goLA.model.Coordinates;
import goLA.model.Trajectory;

import java.util.List;

/**
 * Created by stem-dong-li on 17. 7. 5.
 */
public class FrechetDistance {
    static public boolean decisionDP(Trajectory q_tr, Trajectory t_tr, double dist) {
        int p, q;

        List<Coordinates> p_coordinates = q_tr.getCoordinates();
        p = p_coordinates.size() - 1;

        List<Coordinates> q_coordinates = t_tr.getCoordinates();
        q = q_coordinates.size() - 1;

        boolean[][] bottom = new boolean[p + 1][q + 1];
        boolean[][] left = new boolean[p + 1][q + 1];

        boolean[][] result_bottom = new boolean[p + 1][q + 1];
        boolean[][] result_left = new boolean[p + 1][q + 1];

        for (int i = 0; i < p; i++) {
            for (int j = 0; j < q; j++) {
                //i && (j,j+1)
                left[i][j] = (dist >= EuclideanDistance.pointAndLine(p_coordinates.get(i), q_coordinates.get(j), q_coordinates.get(j + 1)));

                //j && (i,i+1)
                bottom[i][j] = (dist >= EuclideanDistance.pointAndLine(q_coordinates.get(j), p_coordinates.get(i), p_coordinates.get(i + 1)));
            }
        }

        for (int i = 0; i < p; i++) {
            result_bottom[i][0] = bottom[i][0];
            bottom[i][q] = (dist >= EuclideanDistance.pointAndLine(q_coordinates.get(q), p_coordinates.get(i), p_coordinates.get(i + 1)));
        }

        for (int j = 0; j < q; j++) {
            result_left[0][j] = left[0][j];
            left[p][j] = (dist >= EuclideanDistance.pointAndLine(p_coordinates.get(p), q_coordinates.get(j), q_coordinates.get(j + 1)));
        }

        for (int i = 0; i < p; i++) {
            boolean sw = false;
            for (int j = 0; j < q; j++) {
                if (!result_left[i][j] && !result_bottom[i][j]) {
                    result_left[i + 1][j] = false;
                    result_bottom[i][j + 1] = false;
                } else {
                    sw = true;
                    result_bottom[i][j + 1] = bottom[i][j + 1];
                    result_left[i + 1][j] = left[i + 1][j];
                }
            }
            if (!sw) return false;
        }
        if (result_left[p][q - 1] && result_bottom[p - 1][q]) {
            if (EuclideanDistance.distance(p_coordinates.get(p), q_coordinates.get(q)) <= dist && EuclideanDistance.distance(p_coordinates.get(0), q_coordinates.get(0)) <= dist)
                return true;
        }
        return false;
    }
}
