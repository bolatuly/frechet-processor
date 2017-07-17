package test;

import goLA.exceptions.CustomException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by stem-dong-li on 17. 7. 5.
 */
public class CompareResult {
    private static String ROOT = "result/SampleData/";
    private static int tr_wrong_count = 0;
    private static int whole_sol_tr = 0;

    public static void main(String[] args) {
        String solution = ROOT + "v0.1.4/";
        String new_answer = ROOT + "v0.1.4.3/";

        File[] f_sol = new File(solution).listFiles();
        File[] f_na = new File(new_answer).listFiles();

        int file_wrong_count = 0;
        for (int i = 0; i < f_sol.length; i++) {
            boolean checked = false;
            for (int j = 0; j < f_na.length; j++) {
                if (f_sol[i].getName().equals(f_na[j].getName())) {
                    checked = true;
                    if (f_sol[i].getPath().contains("QueryInfo") || f_na[j].getPath().contains("QueryInfo")) continue;
                    if (!compare(f_sol[i], f_na[j])) {
                        System.out.println(f_sol[i].getPath() + ", " + f_na[j].getPath());
                        file_wrong_count++;
                    }
                    break;
                }
            }
        }

        System.out.println("Wrong File : " + file_wrong_count + "/" + f_sol.length);
        System.out.println("Score : " + (double) (f_sol.length - file_wrong_count) / (double) f_sol.length * 100 + "%");
        System.out.println("Wrong Trajectories : " + tr_wrong_count + "/" + whole_sol_tr);
        System.out.println("Score : " + (double) (whole_sol_tr - tr_wrong_count) / (double) whole_sol_tr * 100 + "%");

    }

    private static boolean compare(File a, File b) {
        List<String> a_list = new ArrayList<>();
        List<String> b_list = new ArrayList<>();

        try {
            Stream<String> a_stream = Files.lines(Paths.get(a.getPath()));
            a_stream.forEach(e -> {
                a_list.add(e);
            });

            Stream<String> b_stream = Files.lines(Paths.get(b.getPath()));
            b_stream.forEach(e -> {
                b_list.add(e);
            });
        } catch (NoSuchFileException e) {
            new CustomException("not found");
        } catch (IOException e) {
            e.printStackTrace();
        }


        boolean ret = true;
        int right_b = 0;
        whole_sol_tr += a_list.size();

        for (String sa : a_list) {
            boolean temp = false;
            for (int i = 0; i < b_list.size(); i++) {
                String sb = b_list.get(i);
                if (sa.equals(sb)) {
                    temp = true;
                    right_b++;
                    break;
                }
            }
            if (!temp) {
                ret = false;
                tr_wrong_count++;
            }
        }

        if (right_b != b_list.size()) {
            tr_wrong_count += Math.abs(right_b - b_list.size());
            return false;
        }
        return ret;
    }
}