package tmdg.tugas.pengenalanpola;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rini on 05/10/2015.
 */
public class ChainCodeWhiteConverter {
//    private static final Logger log = LoggerFactory.getLogger(HistogramApp.class);
    private boolean flag[][];
    private int toleransiWhite = 230, toleransi = 150;
    private boolean searchObject = true, searchSubObject = false;
    private int minHor = 0, maxHor = 0, minVer = 0, maxVer = 0;
    private Mat imgMat;
    public List<ChainCodeObj> chainCodes;

    /**
     *
     * @param data MUST BE GRAYSCALE
     */
    public ChainCodeWhiteConverter(Mat data)
    {
        this(data, "");
    }

    /**
     *
     * @param data MUST BE GRAYSCALE
     * @param msg
     */
    public ChainCodeWhiteConverter(Mat data, String msg)
    {
        imgMat=data;
        chainCodes=new ArrayList<>();
    }

    public List<ChainCodeObj> getChainCode()
    {
        byte[] imagByte = new byte[1];
        flag = new boolean[imgMat.rows()][imgMat.cols()];
        int objectIdx = 0;


        for (int y = 0; y < imgMat.rows(); y++) {
            Mat scanline = imgMat.row(y);
            for (int x = 0; x < imgMat.cols(); x++) {
                scanline.get(0, x, imagByte);
                int grayScale = grayScale(imagByte);

                if (grayScale > toleransiWhite && searchObject && !flag[y][x]) {

                    minVer = y;
                    maxVer = y;
                    minHor = x;
                    maxHor = x;
                    String chaincode = prosesChaincode(y, x, 3, imgMat, 1);
                    ChainCodeObj chainCode=new ChainCodeObj();
                    String kodeBelok=getKodeBelok(chaincode);
                    chainCode.setChainCode(chaincode);
                    chainCode.setKodeBelok(kodeBelok);

                    if (chaincode.length() > 20) {
//                        log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, chaincode);
                        objectIdx++;
                        List<ChainCodeObj> subChainCodes = subObject(imgMat);
                        if(subChainCodes.size()>0) {
                            chainCode.getSubChainCode().addAll(subChainCodes);
                        }
                        chainCodes.add(chainCode);
                    }
                    searchObject = false;
                }

                if (grayScale > toleransiWhite && flag[y][x]) {
                    scanline.get(0, x + 1, imagByte);
                    int grayScale1 = grayScale(imagByte);

                    if (grayScale1 < toleransiWhite) {
                        searchObject = true;
                    } else {
                        searchObject = false;
                    }
                }
            }
        }

        return  chainCodes;
    }

    private List<ChainCodeObj> subObject(Mat imgMat) {

        List<ChainCodeObj> subChainCodes=new ArrayList<>();

        byte[] imagByte = new byte[1];

        for (int y = minVer; y <= maxVer; y++) {
            Mat scanline = imgMat.row(y);
            for (int x = minHor; x <= maxHor; x++) {
                scanline.get(0, x, imagByte);
                int grayScale = grayScale(imagByte);

                scanline.get(0, x + 1, imagByte);
                int nextGrayScale = grayScale(imagByte);
                if (grayScale > toleransiWhite && flag[y][x]) {
                    if (nextGrayScale > toleransiWhite) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }

                if (grayScale < toleransi && searchSubObject && !flag[y][x]) {
                    scanline.get(0, x + 1, imagByte);
                    String chaincode2 = prosesChaincode(y, x, 3, imgMat, 0);

                    ChainCodeObj subChainCode=new ChainCodeObj();
                    subChainCode.setChainCode(chaincode2);
                    subChainCodes.add(subChainCode);

                    //charDef.getSubChainCode().add(chaincode2);
//                    log.info("Chaincode subobject : {}", chaincode2);
                    searchSubObject = false;
                }

                if (grayScale < toleransi && flag[y][x]) {
                    if (nextGrayScale > toleransi) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }
            }
        }

        return subChainCodes;
    }

    private String prosesChaincode(int row, int col, int arah, Mat imgMat, int mode) {
        if (flag[row][col]) {
            return "";
        }
        flag[row][col] = true;

        //kondisi perjalanan arah 1
        if (arah == 1) {
            //
            //cek arah 7 (samping kiri)
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

        }
        //kondisi perjalanan arah 2
        else if (arah == 2) {
            //
            //cek arah 8 (samping kiri)
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

        } else if (arah == 3) {
            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }
        } else if (arah == 4) {
            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 5) {
            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 6) {
            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }
        } else if (arah == 7) {
            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }
        } else //if(arah==8)
        {
            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }
        }
        return "";
    }


    private int grayScale(byte[] imagByte) {
        return byteToUnsignedInt(imagByte[0]);
    }

    private String objectarah1(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col;
        imgMat.get(temprow, tempcol, imagByte);
        int gray1 = grayScale(imagByte);
        if (mode == 1) {
            if (gray1 > toleransiWhite) {
                areaObject(row, col);
                return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
            }
        } else {
            if (gray1 < toleransi) {
                return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
            }
        }
        return "";
    }

    private String objectarah2(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray2 = grayScale(imagByte);
        if (mode == 1) {
            if (gray2 > toleransiWhite) {
                areaObject(row, col);
                return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
            }
        } else {
            if (gray2 < toleransi) {
                return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
            }
        }
        return "";
    }

    private String objectarah3(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray3 = grayScale(imagByte);
        if (mode == 1) {
            if (gray3 > toleransiWhite) {
                areaObject(row, col);
                return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
            }
        } else {
            if (gray3 < toleransi) {
                return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
            }
        }

        return "";
    }

    private String objectarah4(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray4 = grayScale(imagByte);
        if (mode == 1) {
            if (gray4 > toleransiWhite) {
                areaObject(row, col);
                return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
            }
        } else {
            if (gray4 < toleransi) {
                return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
            }
        }

        return "";
    }

    private String objectarah5(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col;
        imgMat.get(temprow, tempcol, imagByte);
        int gray5 = grayScale(imagByte);
        if (mode == 1) {
            if (gray5 > toleransiWhite) {
                areaObject(row, col);
                return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        } else {
            if (gray5 < toleransi) {
                return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        }

        return "";
    }

    private String objectarah6(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray6 = grayScale(imagByte);
        if (mode == 1) {
            if (gray6 > toleransiWhite) {
                areaObject(row, col);
                return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
            }
        } else {
            if (gray6 < toleransi) {
                return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
            }
        }

        return "";
    }

    private String objectarah7(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray7 = grayScale(imagByte);
        if (mode == 1) {
            if (gray7 > toleransiWhite) {
                areaObject(row, col);
                return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        } else {
            if (gray7 < toleransi) {
                return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        }

        return "";
    }

    private String objectarah8(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray8 = grayScale(imagByte);
        if (mode == 1) {
            if (gray8 > toleransiWhite) {
                areaObject(row, col);
                return "8" + prosesChaincode(temprow, tempcol, 8, imgMat, mode);
            }
        } else {
            if (gray8 < toleransi) {

                return "8" + prosesChaincode(temprow, tempcol, 8, imgMat, mode);
            }
        }

        return "";
    }

    private void areaObject(int row, int col) {
        if (minHor > col) {
            minHor = col;
        } else if (maxHor < col) {
            maxHor = col;
        }

        if (minVer > row) {
            minVer = row;
        } else if (maxVer < row) {
            maxVer = row;
        }
    }

    private String getKodeBelok(String chainCode)
    {
        String kodeBelok="";
        String Temp = String.valueOf(chainCode.charAt(0));

        char[] tempChar=new char[2];
        tempChar[0]=chainCode.charAt(0);
        tempChar[1]=chainCode.charAt(0);
        kodeBelok +=chainCode.charAt(0);
        boolean rep=false;

        for(int i=0;i<chainCode.length()-1;i++)
        {
            if(i==105)
            {
                char f=chainCode.charAt(i);
            }
            char ff=chainCode.charAt(i);
            if(tempChar[0]!=chainCode.charAt(i))
            {
                if(tempChar[1]!=chainCode.charAt(i)) {
                    tempChar[0]=tempChar[1];
                    tempChar[1]=chainCode.charAt(i);
                    kodeBelok += chainCode.charAt(i);
                }
                else
                {
                    tempChar[0]=tempChar[1];
                    tempChar[1]=chainCode.charAt(i);
                }

            }
            else
            {
                if(tempChar[1]==chainCode.charAt(i+1) && tempChar[0]!=tempChar[1]) {
                    i++;
                } else if(tempChar[1]==chainCode.charAt(i+1)) {

                } else {
                    tempChar[0]=tempChar[1];
                    tempChar[1]=chainCode.charAt(i+1);
                    kodeBelok += chainCode.charAt(i+1);
                    i++;
                }
            }
        }
        return  kodeBelok;
    }


    public static int byteToUnsignedInt(byte b) {
        return 0x00 << 24 | b & 0xff;
    }
}
