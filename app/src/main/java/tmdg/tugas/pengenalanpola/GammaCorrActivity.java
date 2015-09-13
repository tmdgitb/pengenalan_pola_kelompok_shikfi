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
import android.widget.SeekBar;
import android.widget.TextView;


public class GammaCorrActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private ImageView orgImgView, targetImgView;
    private SeekBar gammaControl;
    private TextView controlLabel;

    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamma_corr);

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

        img = Bitmap.createBitmap(orgImg);

        orgImgView    = (ImageView) findViewById(R.id.gcImgOrgView);
        targetImgView = (ImageView) findViewById(R.id.gcImgTargetView);
        gammaControl = (SeekBar) findViewById(R.id.gammaControl);
        controlLabel = (TextView) findViewById(R.id.gammaControlLabel);

        orgImgView.setImageBitmap(orgImg);
        targetImgView.setImageBitmap(orgImg);

        gammaControl.setOnSeekBarChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gamma_corr, menu);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float k = progress/10f;

        controlLabel.setText("Control Gamma : " + k);

        targetImgView.setImageBitmap(gammaCorrection(img, k));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private static Bitmap gammaCorrection(Bitmap original, float gamma) {

        int red, green, blue, pixel;

        float gamma_new = 1 / gamma;
        int[] gamma_LUT = gamma_LUT(gamma_new);

        Bitmap newImg = original.copy(Bitmap.Config.RGB_565, true);

        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {

                // Get pixels by R, G, B
                pixel = original.getPixel(i, j);

                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                red = gamma_LUT[red];
                green = gamma_LUT[green];
                blue = gamma_LUT[blue];

                // Return back to original format
                newImg.setPixel(i, j, Color.rgb(red, green, blue));

            }
        }
        return newImg;
    }

    private static int[] gamma_LUT(float gamma_new) {
        int[] gamma_LUT = new int[256];

        for(int i=0; i<gamma_LUT.length; i++) {
            gamma_LUT[i] = (int) (255 * (Math.pow((float) i / (float) 255, gamma_new)));
        }

        return gamma_LUT;
    }


}
