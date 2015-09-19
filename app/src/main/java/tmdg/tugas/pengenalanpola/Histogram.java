package tmdg.tugas.pengenalanpola;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by Rini on 19/09/2015.
 */
public class Histogram {
    public int jumlahWarna;
    public int[] rImage, gImage, bImage, grayScaleImage;

    public void createHistogram(Bitmap bmp){
        final Mat imgMat = new Mat();
        Utils.bitmapToMat(bmp, imgMat);

        byte[] imagByte=new byte[3];
        imgMat.get(0, 0, imagByte);
        Log.v("imageByte", "Image {" + imagByte + "}");

        boolean colorCounts[][][] = new boolean[256][256][256];
        jumlahWarna = 0;

        rImage         = new int[256];
        gImage         = new int[256];
        bImage         = new int[256];
        grayScaleImage = new int[256];

        for (int i = 0; i < imgMat.rows(); i++)
        {
            Mat scanline = imgMat.row(i);

            for (int j=0;j<imgMat.cols();j++)
            {
                byte[] newImagByte = new byte[3];
                scanline.get(0,j,imagByte);
                int b = byteToUnsignedInt(imagByte[0]);
                int g = byteToUnsignedInt(imagByte[1]);
                int r = byteToUnsignedInt(imagByte[2]);

                Log.v("jml_warna", "Jumlah Warna R{"+r+"} G{"+g+"} B {"+b+"}");

                if (!colorCounts[r][g][b])
                {
                    jumlahWarna++;
                }
                colorCounts[r][g][b] = true;

                rImage[r]++;
                gImage[g]++;
                bImage[b]++;

                int grayScale = Math.round((r + g + b)/3f);
                grayScaleImage[grayScale]++;

            }
        }

    }

    public static int byteToUnsignedInt(byte b) {
        return 0x00 << 24 | b & 0xff;
    }
}
