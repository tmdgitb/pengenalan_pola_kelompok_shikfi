package tmdg.tugas.pengenalanpola;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;


public class HistogramEqActivity extends ActionBarActivity {

    private ImageView orgImgView, targetImgView;
    LineChart eqChart, eqChart2, cdfChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_eq);
        Intent intent = getIntent();

        Bitmap orgImg;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(intent.getStringExtra("target_img"), options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        orgImg = BitmapFactory.decodeFile(intent.getStringExtra("target_img"), options);

        orgImgView    = (ImageView) findViewById(R.id.eqImgOrgView);
        targetImgView = (ImageView) findViewById(R.id.eqImgTargetView);

        eqChart  = (LineChart) findViewById(R.id.chartEq);
        eqChart2 = (LineChart) findViewById(R.id.chartEq2);
        cdfChart = (LineChart) findViewById(R.id.chartEqCdf);

        orgImgView.setImageBitmap(orgImg);

        Bitmap newImg = orgImg.copy(Bitmap.Config.RGB_565, true);
        Bitmap newImg2 = orgImg.copy(Bitmap.Config.RGB_565, true);

        int pixel;
        int levels[][]   = new int[3][256];
        int levels2[][]  = new int[3][];
        int levelY[]     = new int[256];
        int levelY2[]    = new int[256];
        int pixelY[][]   = new int[orgImg.getWidth()][orgImg.getHeight()];
        int pixelCr[][]  = new int[orgImg.getWidth()][orgImg.getHeight()];
        int pixelCb[][]  = new int[orgImg.getWidth()][orgImg.getHeight()];
        int pixelR;//[][]   = new int[orgImg.getWidth()][orgImg.getHeight()];
        int pixelG;//[][]   = new int[orgImg.getWidth()][orgImg.getHeight()];
        int pixelB;//[][]   = new int[orgImg.getWidth()][orgImg.getHeight()];

        for (int i = 0; i < orgImg.getWidth(); i++) {
            for (int j = 0; j < orgImg.getHeight(); j++) {
                //get individual pixel
                pixel = orgImg.getPixel(i, j);

                //get level of each color val for histogram
                levels[0][Color.red(pixel)]++;
                levels[1][Color.green(pixel)]++;
                levels[2][Color.blue(pixel)]++;

                //convert RGB to YCbCr
//                pixelY[i][j]  = (int) ((0.299 * Color.red(pixel)) + (0.587 * Color.green(pixel)) + (0.114 * Color.blue(pixel)));
//                pixelCr[i][j] = (int) ((-0.169 * Color.red(pixel)) - (0.331 * Color.green(pixel)) + (0.5 * Color.blue(pixel)));
//                pixelCb[i][j] = (int) ((0.5 * Color.red(pixel)) - (0.419 * Color.green(pixel)) - (0.081 * Color.blue(pixel)));
                pixelY[i][j]  = (int) Math.round((0.299 * Color.red(pixel)) + (0.587 * Color.green(pixel)) + (0.114 * Color.blue(pixel))) + 0;
                pixelCb[i][j] = (int) Math.round((-0.169 * Color.red(pixel)) - (0.331 * Color.green(pixel)) - (0.500 * Color.blue(pixel))) + 128;
                pixelCr[i][j] = (int) Math.round((0.5 * Color.red(pixel)) - (0.419 * Color.green(pixel)) - (0.081 * Color.blue(pixel))) + 128;

                //make Y histogram
                levelY[pixelY[i][j]]++;


                //grayscale
                newImg2.setPixel(i, j, Color.rgb(pixelY[i][j],pixelY[i][j],pixelY[i][j]));
//                newImg2.setPixel(i, j, Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
            }
        }

        orgImgView.setImageBitmap(newImg2);
        makeHist1(levelY);

        int mass = orgImg.getWidth() * orgImg.getHeight();
        int sum = 0;

        //calculate the scale factor
        float pxScale = (float) 255.0 / mass;

        //make CDF
        for (int i = 0; i < levelY.length; i++){
            sum += levelY[i];
            int value = (int) (pxScale * sum);
            if (value > 255) { value = 255; }
            levelY[i] = value;
        }

        for (int i = 0; i < orgImg.getWidth(); i++) {
            for (int j = 0; j < orgImg.getHeight(); j++) {
                //set the new value
                pixelY[i][j] = levelY[pixelY[i][j]];

                //convert YCbCr to RGB
//                pixelR = (int) Math.round(pixelY[i][j] + (1.402 * pixelCr[i][j]));
//                pixelG = (int) Math.round(pixelY[i][j] - (0.344 * pixelCb[i][j]) - (0.714 * pixelCr[i][j]));
//                pixelB = (int) Math.round(pixelY[i][j] + (1.772 * pixelCb[i][j]));
               // pixelR = (int) Math.round(pr)
                pixelR = (int) Math.round(pixelY[i][j] + (1.4 * (pixelCr[i][j]) - 128));
                pixelG = (int) Math.round(pixelY[i][j] - (0.343 * (pixelCb[i][j] - 128)) - (0.711 * (pixelCr[i][j] -128)));
                pixelB = (int) Math.round(pixelY[i][j] + (1.768 * (pixelCb[i][j] - 128)));

                //get level of each color val for histogram2
                levelY2[pixelY[i][j]]++;
//                Log.v("convert2", i + "," + j + " - " + "RGB: " + pixelR + "," + pixelG + "," + pixelB);
//                levels2[0][pixelR]++;//[i][j]
//                levels2[1][pixelG]++;
//                levels2[2][pixelB]++;

                //draw new picture
//                newImg.setPixel(i, j, Color.rgb(pixelR, pixelG, pixelB));
                newImg.setPixel(i, j, Color.rgb(pixelY[i][j],pixelY[i][j],pixelY[i][j]));
            }
        }

        makeHist2(levelY2);

        sum = 0;

        //make CDF
        for (int i = 0; i < levelY2.length; i++){
            sum += levelY2[i];
            int value = (int) (pxScale * sum);
            if (value > 255) { value = 255; }
            levelY2[i] = value;
        }

        targetImgView.setImageBitmap(newImg);

        makeHistCdf(levelY, levelY2);

    }


   private void makeHist1(int[] levelY){
        ArrayList<String> xVals   = new ArrayList<String>();
        ArrayList<Entry> grVal   = new ArrayList<Entry>();

        for(int i = 0; i < levelY.length; i++){
            xVals.add((i) + "");
            grVal.add(new Entry(levelY[i], i));
        }

        LineDataSet grSet = new LineDataSet(grVal, "Grayscale");
        grSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        grSet.setColor(Color.DKGRAY);
        grSet.setDrawCircles(false);
        grSet.setLineWidth(1f);
        grSet.setDrawCircleHole(false);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(grSet);

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        eqChart.setData(data);
    }

    private void makeHist2(int[] levelY){
        ArrayList<String> xVals   = new ArrayList<String>();
        ArrayList<Entry> grVal   = new ArrayList<Entry>();

        for(int i = 0; i < levelY.length; i++){
            xVals.add((i) + "");
            grVal.add(new Entry(levelY[i], i));
        }

        LineDataSet grSet = new LineDataSet(grVal, "Grayscale");
        grSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        grSet.setColor(Color.DKGRAY);
        grSet.setDrawCircles(false);
        grSet.setLineWidth(1f);
        grSet.setDrawCircleHole(false);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(grSet);

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        eqChart2.setData(data);
    }

    private void makeHistCdf(int[] levelY, int[] levelY2){
        ArrayList<String> xVals   = new ArrayList<String>();
        ArrayList<Entry> cdf1    = new ArrayList<Entry>();
        ArrayList<Entry> cdf2    = new ArrayList<Entry>();

        for(int i = 0; i < levelY.length; i++){
            xVals.add((i) + "");
            cdf1.add(new Entry(levelY[i], i));
            cdf2.add(new Entry(levelY2[i], i));
        }

        LineDataSet cdfSet1 = new LineDataSet(cdf1, "CDF Original");
        cdfSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        cdfSet1.setColor(Color.BLUE);
        cdfSet1.setDrawCircles(false);
        cdfSet1.setLineWidth(1f);
        cdfSet1.setDrawCircleHole(false);

        LineDataSet cdfSet2 = new LineDataSet(cdf2, "CDF Equalized");
        cdfSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        cdfSet2.setColor(Color.RED);
        cdfSet2.setDrawCircles(false);
        cdfSet2.setLineWidth(1f);
        cdfSet2.setDrawCircleHole(false);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(cdfSet1);
        dataSets.add(cdfSet2);

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        cdfChart.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_histogram_eq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
