package tmdg.tugas.pengenalanpola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Button btnSelect, btnEq, btnGamma;
    ImageView ivImage;
    LineChart chart;
    Set<Integer> colors;
    TextView colorCount;
    String targetImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnEq = (Button) findViewById(R.id.btnEq);
        btnEq.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                equalizeHist();
            }
        });
        btnEq.setEnabled(false);
        btnGamma = (Button) findViewById(R.id.btnGamma);
        btnGamma.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                correctGamma();
            }
        });
        btnGamma.setEnabled(false);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        colorCount = (TextView) findViewById(R.id.colorCount);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription("");
        chart.setNoDataTextDescription("No Picture at the moment");
        chart.setHighlightEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
//        chart.setBackgroundColor(Color.LTGRAY);

    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

        btnEq.setEnabled(true);
        btnGamma.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File imageFolder = new File(Environment.getExternalStorageDirectory(), "PatternPic");

        if(!imageFolder.exists()){
            imageFolder.mkdir();
        }

        File destination = new File(imageFolder,
                System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        targetImgPath = destination.getAbsolutePath();
        Log.v("image_path", targetImgPath);

        ivImage.setImageBitmap(thumbnail);

        MakeHist(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Log.v("image_path", selectedImagePath);
        targetImgPath = selectedImagePath;

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ivImage.setImageBitmap(bm);

        MakeHist(bm);
    }

    public void MakeHist(Bitmap img){
        int levels[][] = CalculateHist(img);

        colorCount.setText("There are " + colors.size() + " colors");

        for(int i=0; i < levels[0].length; i++){
            Log.v("Hist", "R, G, B: " + levels[0][i] + ", " + levels[1][i] + ", " + levels[2][i] + " - " + i);
        }

        ArrayList<String> xVals   = new ArrayList<String>();
        ArrayList<Entry> redVal   = new ArrayList<Entry>();
        ArrayList<Entry> greenVal = new ArrayList<Entry>();
        ArrayList<Entry> blueVal  = new ArrayList<Entry>();

        for(int i = 0; i < levels[0].length; i++){
            xVals.add((i) + "");
            redVal.add(new Entry(levels[0][i], i));
            greenVal.add(new Entry(levels[1][i], i));
            blueVal.add(new Entry(levels[2][i], i));
        }

        LineDataSet redSet = new LineDataSet(redVal, "Red");
        redSet.setAxisDependency(AxisDependency.LEFT);
        redSet.setColor(Color.RED);
        redSet.setDrawCircles(false);
        redSet.setLineWidth(1f);
        redSet.setDrawCircleHole(false);

        LineDataSet greenSet = new LineDataSet(greenVal, "Green");
        greenSet.setAxisDependency(AxisDependency.LEFT);
        greenSet.setColor(Color.GREEN);
        greenSet.setDrawCircles(false);
        greenSet.setLineWidth(1f);
        greenSet.setDrawCircleHole(false);

        LineDataSet blueSet = new LineDataSet(blueVal, "Blue");
        blueSet.setAxisDependency(AxisDependency.LEFT);
        blueSet.setColor(Color.BLUE);
        blueSet.setDrawCircles(false);
        blueSet.setLineWidth(1f);
        blueSet.setDrawCircleHole(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(redSet);
        dataSets.add(greenSet);
        dataSets.add(blueSet);

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart.setData(data);
//        img.recycle();
    }

    public int[][] CalculateHist(Bitmap img) {
        int pixel;
        int levels[][] = new int[3][256];

        colors = new HashSet<>();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixel = img.getPixel(i, j);
                colors.add(pixel);
                levels[0][Color.red(pixel)]++;
                levels[1][Color.green(pixel)]++;
                levels[2][Color.blue(pixel)]++;
            }
        }

        return levels;
    }

    public int[][] CalculateYHist(Bitmap img) {
        int pixel;
        int levels[][] = new int[3][256];



        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixel = img.getPixel(i, j);
                levels[0][Color.red(pixel)]++;
                levels[1][Color.green(pixel)]++;
                levels[2][Color.blue(pixel)]++;
            }
        }

        return levels;
    }

    private void equalizeHist(){
        Intent i = new Intent(MainActivity.this, HistogramEqActivity.class);
        i.putExtra("target_img", targetImgPath);
        startActivity(i);
    }

    private void correctGamma(){
        Intent i = new Intent(MainActivity.this, GammaCorrActivity.class);
        i.putExtra("target_img", targetImgPath);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
