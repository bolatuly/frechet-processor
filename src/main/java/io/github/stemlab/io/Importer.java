package io.github.stemlab.io;

import io.github.stemlab.data.Index;
import io.github.stemlab.exceptions.CustomException;
import io.github.stemlab.model.Coordinate;
import io.github.stemlab.model.Query;
import io.github.stemlab.model.Trajectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Importer {

    public void loadFiles(String src, Index index) {
        try (Stream<String> stream = Files.lines(Paths.get(src))) {
            stream.forEach(e -> {
                        if (!e.isEmpty() && !e.equals(null)) {
                            Trajectory trajectory = new Trajectory();
                            trajectory.setName(e);
                            trajectory.setCoordinates(getCoordinateList(e));
                            index.addTrajectory(e, trajectory);
                        }
                    }
            );

        } catch (NoSuchFileException e) {
            new CustomException("Dataset not found");
        } catch (IOException e) {
            new CustomException("IO exception on dataset import: " + e.getMessage());
        }
        index.initialize();
    }

    public List<Query> getQueries(String path) {
        List<Query> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach(e -> {
                        if (!e.isEmpty() && !e.equals(null)) {
                            String lines[] = e.split("\\s+");
                            if (lines.length != 2)
                                new CustomException("Query line doesn't have two properties");

                            Trajectory trajectory = new Trajectory();
                            trajectory.setName(lines[0]);
                            trajectory.setCoordinates(getCoordinateList(lines[0]));
                            double dist = Double.parseDouble(lines[1]);

                            Query query = new Query(trajectory, dist);

                            list.add(query);
                        }
                    }
            );

        } catch (NoSuchFileException e) {
            new CustomException("Query file not found");
        } catch (IOException e) {
            new CustomException("IO exception on query file import: " + e.getMessage());
        }
        return list;
    }

    private List<Coordinate> getCoordinateList(String s) {

        List<Coordinate> list = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(s)).skip(1)) {
            stream.forEach(e -> {
                        String lines[] = e.split("\\s+");
                        if (lines.length < 4) {
                            new CustomException("One of trajectory properties(x,y,k,tid) not found in file \"" + s + "\"");
                        }
                        Coordinate coordinate = new Coordinate();
                        coordinate.setPointX(Double.valueOf(lines[0]));
                        coordinate.setPointY(Double.valueOf(lines[1]));
                        coordinate.setOrder(Integer.valueOf(lines[2]));
                        coordinate.setId(Integer.valueOf(lines[3]));
                        list.add(coordinate);
                    }
            );

        } catch (NoSuchFileException e) {
            new CustomException("File not found : \"" + s + "\"");
        } catch (IOException e) {
            new CustomException("IO exception on coordinates import: " + e.getMessage());
        }
        return list;
    }


}
