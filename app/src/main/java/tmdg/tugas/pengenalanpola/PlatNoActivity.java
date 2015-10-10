package tmdg.tugas.pengenalanpola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlatNoActivity extends ActionBarActivity {
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    private List<String> hasilPengenalan=new ArrayList<>();
    private List<ChainCodeObj> dataTraining=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plat_no);

        trainingData();

        Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void trainingData(){
        //===================================Data Training======================================//
        List<ChainCodeObj> dataTraining=new ArrayList<>();
        List<String> stringTraining=new ArrayList<>();//B 14 IA -- 04.16
        stringTraining.add("platB.jpg");
        stringTraining.add("plat1.jpg");
        stringTraining.add("plat4.jpg");
        stringTraining.add("plati.jpg");
        stringTraining.add("platA.jpg");
        stringTraining.add("platTgl0.jpg");
        stringTraining.add("platTgl6.jpg");


        int[] intTraining = new int[7];
        intTraining[0] = R.drawable.plat_b;
        intTraining[1] = R.drawable.plat_a;
        intTraining[2] = R.drawable.plat_i;
        intTraining[3] = R.drawable.plat_1;
        intTraining[4] = R.drawable.plat_4;
        intTraining[5] = R.drawable.plat_tgl_0;
        intTraining[6] = R.drawable.plat_tgl_6;

        Context context = getApplicationContext();
        for(int i =0;i<intTraining.length;i++)
        {
            //asli
//            final File imageFile = new File(stringTraining.get(i));
//            Log.v("img_mat", "Processing image file " + imageFile);
//            final Mat imgMat = Imgcodecs.imread(imageFile.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

            //alter
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), intTraining[i]);

            Mat imgMat = new Mat();
            Utils.bitmapToMat(bmp, imgMat);

            Log.v("img_mat", "Image mat: rows=" + imgMat.rows() + " cols=" + imgMat.cols());
            ChainCodeWhiteConverter chainCodeWhiteConverter=new ChainCodeWhiteConverter(imgMat,"plat");
            List<ChainCodeObj> data = chainCodeWhiteConverter.getChainCode();
            data.get(0).setCharacter(stringTraining.get(i)); //intTraining[i] + ""
            dataTraining.add(data.get(0));
        }
    }

















    private void cekData(Bitmap bmp){
        //===================================Data Plat======================================//

        //asli
//        final File imageFile = new File("Plat_Nomor.jpg");//AA_1.jpg
//        Log.v("img_mat", "Processing image file " + imageFile);
//        final Mat imgMat = Imgcodecs.imread(imageFile.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        //alter
        final Mat imgMat = new Mat();
        Utils.bitmapToMat(bmp, imgMat);

        Log.v("img_mat", "Image mat: rows=" + imgMat.rows() + " cols=" + imgMat.cols());
        ChainCodeWhiteConverter chainCodeWhiteConverter=new ChainCodeWhiteConverter(imgMat,"plat");
        List<ChainCodeObj> dataPlat = chainCodeWhiteConverter.getChainCode();

        //===================================Cek Data======================================//
        for (int i=0;i<dataPlat.size();i++)
        {
            ChainCodeObj charPlat=dataPlat.get(i);

            for(int j=0;j<dataTraining.size();j++)
            {
                if( charPlat.getKodeBelok().equals(dataTraining.get(j).getKodeBelok()))
                {
                    hasilPengenalan.add(charPlat.getCharacter());
                    break;
                }
            }
        }

        for(int i = 0; i < hasilPengenalan.size(); i++){
            Log.v("platno_result", "(" + i + ")" + hasilPengenalan.get(i));
        }
    }










    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PlatNoActivity.this);
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
        Bitmap cameraBmp = (Bitmap) data.getExtras().get("data");
        Bitmap thumbnail = cameraBmp.copy(Bitmap.Config.ARGB_8888, false);
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

        cekData(thumbnail);

//        histo.createHistogram(thumbnail);
//        drawHistogram();
//        chaincode = new ChainCodeConverter(thumbnail, "");
//        chaincode.getChainCode();
//        displayChaincode();

        //targetImgPath = destination.getAbsolutePath();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Log.v("image_path", selectedImagePath);
//        targetImgPath = selectedImagePath;

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        cekData(bm);
//        histo.createHistogram(bm);
//        drawHistogram();
//        chaincode.findObject(bm);
//        chaincode = new ChainCodeConverter(bm, "");
//        chaincode.getChainCode();
//        displayChaincode();
    }
















    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    Log.i("OpenCV", "OpenCV loading error");
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plat_no, menu);
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
