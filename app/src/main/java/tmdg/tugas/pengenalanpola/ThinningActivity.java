package tmdg.tugas.pengenalanpola;

import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ThinningActivity extends blankGetImageActivity {
    final static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
    final static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};
    static List<Point> toWhite = new ArrayList<>();

    private int toleransi=10,toleransiWhite=100;
//    static char[][] grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thinning);
    }




    @Override
    protected void callProcessing(){
        boolean firstStep = false;
        boolean hasChanged;
        byte[] imagByte=new byte[4];

        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int r = 1; r < imgMat.rows() - 1; r++) {
                Mat scanline = imgMat.row(r);

                for (int c = 1; c < imgMat.cols() - 1; c++) {
                    scanline.get(0,c,imagByte);
                    int curPoint = grayScale(imagByte);

                    if (curPoint > toleransi)
                        continue;

                    int nn = numNeighbors(r, c);
                    if (nn < 2 || nn > 6)
                        continue;

                    if (numTransitions(r, c) != 1)
                        continue;

                    if (!atLeastOneIsWhite(r, c, firstStep ? 0 : 1))
                        continue;

                    toWhite.add(new Point(c, r));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite)
//                grid[p.y][p.x] = ' ';
                // set points to white
            toWhite.clear();

        } while (hasChanged || firstStep);

        printResult();
    }

    static int numNeighbors(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == '#')
                count++;
        return count;
    }

    static int numTransitions(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == ' ') {
                if (grid[r + nbrs[i + 1][1]][c + nbrs[i + 1][0]] == '#')
                    count++;
            }
        return count;
    }

    static boolean atLeastOneIsWhite(int r, int c, int step) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (grid[r + nbr[1]][c + nbr[0]] == ' ') {
                    count++;
                    break;
                }
            }
        return count > 1;
    }

    static void printResult() {
        for (char[] row : grid)
            System.out.println(row);
    }
}
