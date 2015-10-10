package tmdg.tugas.pengenalanpola;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by ilham1012 on 9/19/15.
 */
public class ChainCode {
    private boolean flag [][];
    private int toleransi=10,toleransiWhite=100;
    private boolean searchObject=true,searchSubObject=false;
    public String chaincodeOut[];

    private int minHor=0,maxHor=0,minVer=0,maxVer=0;

    public void findObject(Bitmap bmp){
        final Mat imgMat = new Mat();
        Utils.bitmapToMat(bmp, imgMat);
        chaincodeOut = new String[2];

        byte[] imagByte=new byte[4];
        imgMat.get(0, 0, imagByte);
        Log.v("imageByte", "Image {" + imagByte + "}");

        flag = new boolean[imgMat.rows()][imgMat.cols()];

        for (int i=0;i<imgMat.rows();i++)
        {
            Mat scanline = imgMat.row(i);
            for(int j=0;j<imgMat.cols();j++)
            {
                scanline.get(0,j,imagByte);
                int grayScale = grayScale(imagByte);

                if(grayScale<toleransi && searchObject &&!flag[i][j])
                {

                    minVer=i;maxVer=i;
                    minHor=j;maxHor=j;
                    String chaincode=ProsesChaincode(i, j, 3, imgMat, 0);
                    if(chaincode.length()>20)
                    {
                        Log.v("chaincode_", "Chaincode object : "+chaincode);
                        chaincodeOut[0] = chaincode;
                        subObject(imgMat);
                    }
                    searchObject=false;
                }

                if(grayScale<toleransi && flag[i][j] )
                {
                    scanline.get(0, j + 1, imagByte);
                    int grayScale1 = grayScale(imagByte);

                    if(grayScale1>toleransi)
                    {
                        searchObject=true;
                    }
                    else
                    {
                        searchObject=false;
                    }
                }
            }
        }

    }

    private void subObject(Mat imgMat)
    {
        byte[] imagByte=new byte[4];

        for (int i=minVer;i<=maxVer;i++)
        {
            Mat scanline = imgMat.row(i);
            for (int j = minHor; j <= maxHor; j++)
            {
                scanline.get(0,j,imagByte);
                int grayScale =grayScale(imagByte);

                scanline.get(0, j + 1, imagByte);
                int nextGrayScale = grayScale(imagByte);
                if(grayScale<toleransi&&flag[i][j])
                {
                    if(nextGrayScale<toleransi) {
                        searchSubObject = true;
                    }
                    else
                    {
                        searchSubObject = false;
                    }
                }

                if(grayScale>toleransiWhite && searchSubObject && !flag[i][j])
                {
                    scanline.get(0, j + 1, imagByte);
                    String chaincode2 = ProsesChaincode(i, j, 3, imgMat, 1);
                    Log.v("chaincode_", "Chaincode subobject : "+chaincode2);
                    chaincodeOut[1] = chaincode2;
                    searchSubObject=false;
                }

                if(grayScale>toleransiWhite && flag[i][j] )
                {

                    if(nextGrayScale<toleransiWhite)
                    {
                        searchSubObject=true;
                    }
                    else
                    {
                        searchSubObject=false;
                    }
                }
            }
        }
    }

    private String ProsesChaincode(int row, int col,int arah,Mat imgMat,int mode)
    {
        if(flag[row][col])
        {
            return "";
        }
        flag[row][col]=true;

        //kondisi perjalanan arah 1
        if(arah==1)
        {
            //
            //cek arah 7 (samping kiri)
            //
            String arah7=Objectarah7(row,col,imgMat,mode);
            if(arah7!="")
            {
                return arah7;
            }
            //
            //cek arah 8
            //
            String arah8=Objectarah8(row,col,imgMat,mode);
            if(arah8!="")
            {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1=Objectarah1(row,col,imgMat,mode);
            if(arah1!="")
            {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2=Objectarah2(row, col, imgMat,mode);
            if(arah2!="")
            {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3=Objectarah3(row, col, imgMat,mode);
            if(arah3!="")
            {
                return arah3;
            }

        }
        //kondisi perjalanan arah 2
        else if(arah==2)
        {
            //
            //cek arah 8 (samping kiri)
            //
            String arah8=Objectarah8(row, col, imgMat,mode);
            if(arah8!="")
            {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1=Objectarah1(row, col, imgMat,mode);
            if(arah1!="")
            {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2=Objectarah2(row, col, imgMat,mode);
            if(arah2!="")
            {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3=Objectarah3(row, col, imgMat,mode);
            if(arah3!="")
            {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4=Objectarah4(row, col, imgMat,mode);
            if(arah4!="")
            {
                return arah4;
            }

        }
        else if(arah==3)
        {
            //
            //cek arah 1 (depan)
            //
            String arah1=Objectarah1(row, col, imgMat,mode);
            if(arah1!="")
            {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2=Objectarah2(row, col, imgMat,mode);
            if(arah2!="")
            {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3=Objectarah3(row, col, imgMat,mode);
            if(arah3!="")
            {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4=Objectarah4(row, col, imgMat,mode);
            if(arah4!="")
            {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5=Objectarah5(row, col, imgMat,mode);
            if(arah5!="")
            {
                return arah5;
            }
        }
        else if(arah==4)
        {
            //
            //cek arah 2
            //
            String arah2=Objectarah2(row, col, imgMat,mode);
            if(arah2!="")
            {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3=Objectarah3(row, col, imgMat,mode);
            if(arah3!="")
            {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4=Objectarah4(row, col, imgMat,mode);
            if(arah4!="")
            {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5=Objectarah5(row, col, imgMat,mode);
            if(arah5!="")
            {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6=Objectarah6(row, col, imgMat,mode);
            if(arah6!="")
            {
                return arah6;
            }
        }
        else if(arah==5)
        {
            //
            //cek arah 3
            //
            String arah3=Objectarah3(row, col, imgMat,mode);
            if(arah3!="")
            {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4=Objectarah4(row, col, imgMat,mode);
            if(arah4!="")
            {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5=Objectarah5(row, col, imgMat,mode);
            if(arah5!="")
            {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6=Objectarah6(row, col, imgMat,mode);
            if(arah6!="")
            {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7=Objectarah7(row, col, imgMat,mode);
            if(arah7!="")
            {
                return arah7;
            }
        }
        else if(arah==6)
        {
            //
            //cek arah 4
            //
            String arah4=Objectarah4(row, col, imgMat,mode);
            if(arah4!="")
            {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5=Objectarah5(row, col, imgMat,mode);
            if(arah5!="")
            {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6=Objectarah6(row, col, imgMat,mode);
            if(arah6!="")
            {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7=Objectarah7(row, col, imgMat,mode);
            if(arah7!="")
            {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8=Objectarah8(row, col, imgMat,mode);
            if(arah8!="")
            {
                return arah8;
            }
        }
        else if(arah==7)
        {
            //
            //cek arah 5
            //
            String arah5=Objectarah5(row, col, imgMat,mode);
            if(arah5!="")
            {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6=Objectarah6(row, col, imgMat,mode);
            if(arah6!="")
            {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7=Objectarah7(row, col, imgMat,mode);
            if(arah7!="")
            {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8=Objectarah8(row, col, imgMat,mode);
            if(arah8!="")
            {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1=Objectarah1(row, col, imgMat,mode);
            if(arah1!="")
            {
                return arah1;
            }
        }
        else //if(arah==8)
        {
            //
            //cek arah 6
            //
            String arah6=Objectarah6(row, col, imgMat,mode);
            if(arah6!="")
            {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7=Objectarah7(row, col, imgMat,mode);
            if(arah7!="")
            {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8=Objectarah8(row, col, imgMat,mode);
            if(arah8!="")
            {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1=Objectarah1(row, col, imgMat,mode);
            if(arah1!="")
            {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2=Objectarah2(row, col, imgMat,mode);
            if(arah2!="")
            {
                return arah2;
            }
        }
        return "";
    }

    private int grayScale(byte[] imagByte)
    {
        int b = byteToUnsignedInt(imagByte[0]);
        int g = byteToUnsignedInt( imagByte[1]);
        int r = byteToUnsignedInt(imagByte[2]);
        return   Math.round((r + g + b) / 3f);
    }

    private String Objectarah1(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row-1;
        tempcol=col;
        imgMat.get(temprow,tempcol,imagByte);
        int gray1=grayScale(imagByte);
        if(mode==1)
        {
            if(gray1 > toleransiWhite)
            {
                return "1" + ProsesChaincode(temprow, tempcol, 1, imgMat,mode);
            }
        }
        else {
            if (gray1 < toleransi) {
                AreaObject(row, col);
                return "1" + ProsesChaincode(temprow, tempcol, 1, imgMat,mode);
            }
        }
        return "";
    }

    private String Objectarah2(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row-1;
        tempcol=col+1;
        imgMat.get(temprow,tempcol,imagByte);
        int gray2=grayScale(imagByte);
        if(mode==1)
        {
            if (gray2 > toleransiWhite)
            {
                return "2" + ProsesChaincode(temprow, tempcol, 2, imgMat, mode);
            }
        }
        else
        {
            if (gray2 < toleransi)
            {
                AreaObject(row, col);
                return "2" + ProsesChaincode(temprow, tempcol, 2, imgMat,mode);
            }
        }
        return "";
    }

    private String Objectarah3(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row;
        tempcol=col + 1;
        imgMat.get(temprow,tempcol,imagByte);
        int gray3=grayScale(imagByte);
        if(mode==1)
        {
            if(gray3>toleransiWhite)
            {
                return "3"+ProsesChaincode(temprow, tempcol, 3, imgMat,mode);
            }
        }
        else
        {
            if(gray3<toleransi)
            {
                AreaObject(row, col);
                return "3"+ProsesChaincode(temprow, tempcol, 3, imgMat,mode);
            }
        }

        return "";
    }

    private String Objectarah4(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row+1;
        tempcol=col+1;
        imgMat.get(temprow,tempcol,imagByte);
        int gray4=grayScale(imagByte);
        if(mode==1)
        {
            if(gray4 > toleransiWhite)
            {
                return "4"+ProsesChaincode(temprow, tempcol, 4, imgMat,mode);
            }
        }
        else
        {
            if(gray4 < toleransi)
            {
                AreaObject(row, col);
                return "4"+ProsesChaincode(temprow, tempcol, 4, imgMat,mode);
            }
        }

        return "";
    }

    private String Objectarah5(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row+1;
        tempcol=col;
        imgMat.get(temprow,tempcol,imagByte);
        int gray5=grayScale(imagByte);
        if(mode==1)
        {
            if(gray5 > toleransiWhite)
            {
                return "5"+ProsesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        }
        else
        {
            if(gray5 < toleransi)
            {
                AreaObject(row, col);
                return "5"+ProsesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah6(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row+1;
        tempcol=col-1;
        imgMat.get(temprow,tempcol,imagByte);
        int gray6=grayScale(imagByte);
        if(mode==1)
        {
            if(gray6 > toleransiWhite)
            {
                return "6"+ProsesChaincode(temprow, tempcol, 6, imgMat,mode);
            }
        }
        else
        {
            if(gray6 < toleransi)
            {
                AreaObject(row, col);
                return "6"+ProsesChaincode(temprow, tempcol, 6, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah7(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row;
        tempcol=col-1;
        imgMat.get(temprow,tempcol,imagByte);
        int gray7=grayScale(imagByte);
        if(mode==1)
        {
            if(gray7 > toleransiWhite)
            {
                return "7"+ProsesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        }
        else
        {
            if(gray7 < toleransi)
            {
                AreaObject(row, col);
                return "7"+ProsesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah8(int row, int col,Mat imgMat,int mode)
    {
        int temprow,tempcol;
        byte[] imagByte=new byte[4];

        temprow=row-1;
        tempcol=col-1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray8=grayScale(imagByte);
        if(mode==1){
            if(gray8 > toleransiWhite){
                return "8" + ProsesChaincode(temprow, tempcol, 8, imgMat,mode);
            }
        } else {
            if(gray8 < toleransi){
                AreaObject(row, col);
                return "8" + ProsesChaincode(temprow, tempcol, 8, imgMat,mode);
            }
        }

        return "";
    }

    private  void AreaObject(int row, int col)
    {
        if(minHor>col){
            minHor=col;
        } else if(maxHor<col){
            maxHor=col;
        }

        if(minVer>row){
            minVer=row;
        } else if(maxVer<row){
            maxVer=row;
        }
    }

    public static int byteToUnsignedInt(byte b) {
        return 0x00 << 24 | b & 0xff;
    }
}