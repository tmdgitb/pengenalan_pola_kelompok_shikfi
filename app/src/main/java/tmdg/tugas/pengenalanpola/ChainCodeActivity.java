package tmdg.tugas.pengenalanpola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChainCodeActivity extends ActionBarActivity {
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String targetImgPath;
    Histogram histo;
    ChainCodeConverter chaincode;
    TextView chaincodeView1, chaincodeView2;

    int fontFace = Core.FONT_HERSHEY_PLAIN;
    double fontScale = 20;
    int thickness = 10;
    int[] baseline = {0};
    List<CharDef> charDefs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain_code);
//        histo = new Histogram();
//        chaincode = new ChainCode();
        Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        chaincodeView1 = (TextView) findViewById(R.id.chaincodeView);
        chaincodeView2 = (TextView) findViewById(R.id.chaincode2View);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
//            createAZ();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ChainCodeActivity.this);
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

//        histo.createHistogram(thumbnail);
//        drawHistogram();
        chaincode = new ChainCodeConverter(thumbnail, "");
        chaincode.getChainCode();
        displayChaincode();

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

//        histo.createHistogram(bm);
//        drawHistogram();
//        chaincode.findObject(bm);
        chaincode = new ChainCodeConverter(bm, "");
        chaincode.getChainCode();
        displayChaincode();
    }

    private void drawHistogram(){
        for (int i=0; i < histo.rImage.length; i++){
            Log.v("histogram", "("+i+") - R:"+histo.rImage[i]+" G:"+histo.gImage[i]+" B"+histo.bImage[i]);
        }
    }

    private void displayChaincode(){
        chaincode.charDef.calcDirChainCode();
        chaincode.charDef.calcRelChainCode();
        chaincodeView1.setText(chaincode.charDef.getChainCode());
        chaincodeView2.setText("Dir & Rel: " + chaincode.charDef.getDirChainCode() + ", " + chaincode.charDef.getRelChainCode());
    }


//    private void createAZ(){
//        for (char ch=33; ch<=126;ch++)
//        {
//            if (ch == '!' || ch == '"' || ch == '%' || ch == ':' || ch == ';' || ch == '=' || ch == '?') { // skip multi-chaincode for now
//                continue;
//            }
//
//            try{
//                if(ch==106)
//                {
//                    int i=0;
//                }
//                System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
//
//                Size textsize = Core.getTextSize(String.valueOf(ch), fontFace, fontScale, thickness, baseline);
//                int heightImg=(int)textsize.height;
//                int widthImg=(int)textsize.width;
//                Mat source = new Mat(heightImg*2,widthImg*2, CvType.CV_8UC1, new Scalar(250));
//                Core.putText(source, String.valueOf(ch), new Point(20, heightImg + (heightImg / 2)), fontFace, fontScale, new Scalar(0), thickness);
//                final String filename = "character/" + String.valueOf(ch) + ".png";
//                Imgcodecs.imwrite(filename, source);
//
//                prosesChainCode(source, String.valueOf(ch));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

//    private void prosesChainCode(Mat img,String msg)
//    {
//        ChainCodeConverter chainCodeConverter = new ChainCodeConverter(img,msg);
//        charDefs.add(chainCodeConverter.getChainCode());
//    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chain_code, menu);
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
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }
}
