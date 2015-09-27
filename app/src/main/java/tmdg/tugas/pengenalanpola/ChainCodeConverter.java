package tmdg.tugas.pengenalanpola;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by ilham1012 on 9/27/15.
 */
public class ChainCodeConverter {
        private boolean flag[][];
        private int toleransi = 10, toleransiWhite = 230;
        private boolean searchObject = true, searchSubObject = false;
        private int minHor = 0, maxHor = 0, minVer = 0, maxVer = 0;
        private Mat imgMat;
        public  CharDef charDef;


        /**
         *
         * @param bmp MUST BE GRAYSCALE
         * @param msg
         */
        public ChainCodeConverter(Bitmap bmp, String msg)
        {
            imgMat = new Mat();
            Utils.bitmapToMat(bmp, imgMat);
            charDef=new CharDef();
            charDef.setCharacter(msg);
        }

        public CharDef getChainCode()
        {
            byte[] imagByte = new byte[4];
            flag = new boolean[imgMat.rows()][imgMat.cols()];
            int objectIdx = 0;


            for (int y = 0; y < imgMat.rows(); y++) {
                Mat scanline = imgMat.row(y);
                for (int x = 0; x < imgMat.cols(); x++) {
                    scanline.get(0, x, imagByte);
                    int grayScale = grayScale(imagByte);

                    if (grayScale < toleransi && searchObject && !flag[y][x]) {

                        minVer = y;
                        maxVer = y;
                        minHor = x;
                        maxHor = x;
                        String chaincode = prosesChaincode(y, x, 3, imgMat, 0);

                        Log.v("chaincode_object", "Chaincode object #" + objectIdx + " at ("+ x +", "+ y +"): " + chaincode);

                        if (chaincode.length() > 20) {
                            charDef.setChainCode(chaincode);
                            objectIdx++;
                            subObject(imgMat);
                        }
                        searchObject = false;
                    }

                    if (grayScale < toleransi && flag[y][x]) {
                        scanline.get(0, x + 1, imagByte);
                        int grayScale1 = grayScale(imagByte);

                        if (grayScale1 > toleransi) {
                            searchObject = true;
                        } else {
                            searchObject = false;
                        }
                    }
                }
            }

            return  charDef;
        }

        private void subObject(Mat imgMat) {
            byte[] imagByte = new byte[4];

            for (int y = minVer; y <= maxVer; y++) {
                Mat scanline = imgMat.row(y);
                for (int x = minHor; x <= maxHor; x++) {
                    scanline.get(0, x, imagByte);
                    int grayScale = grayScale(imagByte);

                    scanline.get(0, x + 1, imagByte);
                    int nextGrayScale = grayScale(imagByte);
                    if (grayScale < toleransi && flag[y][x]) {
                        if (nextGrayScale < toleransi) {
                            searchSubObject = true;
                        } else {
                            searchSubObject = false;
                        }
                    }

                    if (grayScale > toleransiWhite && searchSubObject && !flag[y][x]) {
                        scanline.get(0, x + 1, imagByte);
                        String chaincode2 = prosesChaincode(y, x, 3, imgMat, 1);
                        charDef.getSubChainCode().add(chaincode2);
                        Log.v("chaincode_subobject", "Chaincode subobject : " + chaincode2);
                        searchSubObject = false;
                    }

                    if (grayScale > toleransiWhite && flag[y][x]) {
                        if (nextGrayScale < toleransiWhite) {
                            searchSubObject = true;
                        } else {
                            searchSubObject = false;
                        }
                    }
                }
            }
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
            byte[] imagByte = new byte[4];

            temprow = row - 1;
            tempcol = col;
            imgMat.get(temprow, tempcol, imagByte);
            int gray1 = grayScale(imagByte);
            if (mode == 1) {
                if (gray1 > toleransiWhite) {
                    return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
                }
            } else {
                if (gray1 < toleransi) {
                    areaObject(row, col);
                    return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
                }
            }
            return "";
        }

        private String objectarah2(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row - 1;
            tempcol = col + 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray2 = grayScale(imagByte);
            if (mode == 1) {
                if (gray2 > toleransiWhite) {
                    return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
                }
            } else {
                if (gray2 < toleransi) {
                    areaObject(row, col);
                    return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
                }
            }
            return "";
        }

        private String objectarah3(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row;
            tempcol = col + 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray3 = grayScale(imagByte);
            if (mode == 1) {
                if (gray3 > toleransiWhite) {
                    return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
                }
            } else {
                if (gray3 < toleransi) {
                    areaObject(row, col);
                    return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
                }
            }

            return "";
        }

        private String objectarah4(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row + 1;
            tempcol = col + 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray4 = grayScale(imagByte);
            if (mode == 1) {
                if (gray4 > toleransiWhite) {
                    return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
                }
            } else {
                if (gray4 < toleransi) {
                    areaObject(row, col);
                    return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
                }
            }

            return "";
        }

        private String objectarah5(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row + 1;
            tempcol = col;
            imgMat.get(temprow, tempcol, imagByte);
            int gray5 = grayScale(imagByte);
            if (mode == 1) {
                if (gray5 > toleransiWhite) {
                    return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
                }
            } else {
                if (gray5 < toleransi) {
                    areaObject(row, col);
                    return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
                }
            }

            return "";
        }

        private String objectarah6(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row + 1;
            tempcol = col - 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray6 = grayScale(imagByte);
            if (mode == 1) {
                if (gray6 > toleransiWhite) {
                    return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
                }
            } else {
                if (gray6 < toleransi) {
                    areaObject(row, col);
                    return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
                }
            }

            return "";
        }

        private String objectarah7(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row;
            tempcol = col - 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray7 = grayScale(imagByte);
            if (mode == 1) {
                if (gray7 > toleransiWhite) {
                    return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
                }
            } else {
                if (gray7 < toleransi) {
                    areaObject(row, col);
                    return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
                }
            }

            return "";
        }

        private String objectarah8(int row, int col, Mat imgMat, int mode) {
            int temprow, tempcol;
            byte[] imagByte = new byte[4];

            temprow = row - 1;
            tempcol = col - 1;
            imgMat.get(temprow, tempcol, imagByte);
            int gray8 = grayScale(imagByte);
            if (mode == 1) {
                if (gray8 > toleransiWhite) {
                    return "8" + prosesChaincode(temprow, tempcol, 8, imgMat, mode);
                }
            } else {
                if (gray8 < toleransi) {
                    areaObject(row, col);
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

        public static int byteToUnsignedInt(byte b) {
            return 0x00 << 24 | b & 0xff;
        }
    }
