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
    protected void callProcessing() {
    }
}
