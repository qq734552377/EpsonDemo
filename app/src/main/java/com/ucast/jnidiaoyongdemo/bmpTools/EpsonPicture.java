package com.ucast.jnidiaoyongdemo.bmpTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;

import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 * 描述：打印图片生成
 */
public class EpsonPicture {
    public final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory().toString();
    private final static String BIT_NAME = "/Ucast/ucast.bmp";

    private final static int LINE_STRING_NUMBER = 32 ;
    private final static int OFFSET_X = 10 ;
    private final static int OFFSET_Y = 40 ;
    private final static int FONT_SIZE = 24 ;
    private final static int FONT_SIZE_TIMES = 1 ;
    private final static int LINE_HEIGHT = 40 ;
    private final static String FONT = "simsun.ttc" ;
    private final static int BITMAP_END_POINT = 384 ;
    private final static int CUT_PAPER_HEIGHT = 40 ;

    public static String getBitMap(List<PrintAndDatas> printAndDatasList) {

        int line_sizes = 0 ;
        for (int i = 0; i < printAndDatasList.size(); i++) {
            PrintAndDatas one = printAndDatasList.get(i);
            List<String> list = getLineStringDatas(one.datas);
            line_sizes += list.size() * one.FONT_SIZE_TIMES;
        }


        int Height = line_sizes * LINE_HEIGHT;
        Bitmap bmp = Bitmap.createBitmap(384, Height + CUT_PAPER_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.WHITE);


        int cur_line = 0;
        for (int i = 0; i < printAndDatasList.size(); i++) {

            PrintAndDatas one = printAndDatasList.get(i);
            List<String> list = getLineStringDatas(one.datas);
            Paint print = new Paint();
            print.setColor(Color.BLACK);
            print.setTextSize(one.FONT_SIZE);
            if(one.FONT_SIZE_TIMES ==2){
                print.setTextSize(one.FONT_SIZE * one.FONT_SIZE_TIMES *3 / 4);
            }

//        print.setTypeface(Typeface.MONOSPACE);
            Typeface font = Typeface.createFromAsset(ExceptionApplication.getInstance().getAssets(),FONT);
            print.setTypeface(Typeface.create(font,Typeface.NORMAL));
            for (int j = 0; j < list.size(); j++) {
                canvas.drawText(list.get(j), one.OFFSET_X, cur_line * one.LINE_HEIGHT +one.OFFSET_Y * one.FONT_SIZE_TIMES, print);
                cur_line ++;
            }

        }



        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        String path = saveBmp(bmp);
        return path;
    }


    /**
     * 通过字符串获取分行的数据
     *
     * */

    public static List<String> getLineStringDatas(String string){
        String[] dataString = string.split("\n");
        List<String> list = new ArrayList<>();
        List<String> splistlist;
        for (int i = 0; i < dataString.length; i++) {
            if (dataString[i].getBytes().length > LINE_STRING_NUMBER) {
                splistlist = splitString(dataString[i]);
                for (int t = 0; t < splistlist.size(); t++) {
                    list.add(splistlist.get(t));
                }
            } else {
                list.add(dataString[i]);
            }
        }

        return list;
    }



    /**
     * 拆分字符串
     *
     * @param data
     * @return
     */
    public static List<String> splitString(String data) {
        List<String> list = new ArrayList<>();
        String string = "";
        int offert = 0;
        for (int i = 0; i < data.length(); i++) {
            String s = data.substring(i, i + 1);
            if (s.getBytes().length > 1) {
                string += s;
                offert = offert + 2;
            } else {
                string += s;
                offert++;
            }
            if (offert >= LINE_STRING_NUMBER) {
                list.add(string);
                string = "";
                offert = 0;
            }
        }
        list.add(string);
        return list;
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @return
     */
    public static String saveBmp(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 );
        try {
            File dirFile = new File(ALBUM_PATH);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File myCaptureFile = new File(ALBUM_PATH + BIT_NAME);
            FileOutputStream fileos = new FileOutputStream(myCaptureFile);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头ͷ
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头ͷ
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    if(Color.red(clr) > 156){
                        bmpData[nRealCol * wWidth + wByteIdex] = (byte) 0xFF;
                        bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) 0xFF;
                        bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) 0xFF;
                    }else{
                        bmpData[nRealCol * wWidth + wByteIdex] = (byte) 0x00;
                        bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) 0x00;
                        bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) 0x00;
                    }

//                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
//                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
//                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ALBUM_PATH + BIT_NAME;
    }

    protected static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    protected static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    protected static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }




    public static void strToBmp() throws IOException {
        String path = ALBUM_PATH + BIT_NAME;

        String path_res = ALBUM_PATH + "/point_data_result.bmp";
        String path_txt = ALBUM_PATH + "/point_data.txt";

        FileOutputStream fos = null;
        FileOutputStream fosToTxt = null;

        try {
            File write_file = new File(path_res);
            byte[] datas = TurnBytes(BitmapFactory.decodeFile(path));
            fos = new FileOutputStream(write_file);

            fosToTxt = new FileOutputStream(path_txt);
            fosToTxt.write(datas);

            int nBmpWidth = 384;
            int line_byte_num = nBmpWidth/8;
            int nBmpHeight = datas.length/line_byte_num;
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            int bufferSize = nBmpHeight * (nBmpWidth * 3);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头ͷ
            writeWord(fos, bfType);
            writeDword(fos, bfSize);
            writeWord(fos, bfReserved1);
            writeWord(fos, bfReserved2);
            writeDword(fos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头ͷ
            writeDword(fos, biSize);
            writeLong(fos, biWidth);
            writeLong(fos, biHeight);
            writeWord(fos, biPlanes);
            writeWord(fos, biBitCount);
            writeDword(fos, biCompression);
            writeDword(fos, biSizeImage);
            writeLong(fos, biXpelsPerMeter);
            writeLong(fos, biYPelsPerMeter);
            writeDword(fos, biClrUsed);
            writeDword(fos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];

            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth/8; wRow++) {
                    int index_24 = line_byte_num * nCol + wRow;
                    int clr = datas[index_24];
                    for (int i = 0; i < 8; i++) {
                        int one  =( clr >> (7 - i)) & 0x01;
                        if(one == 1) {
                            bmpData[nRealCol * wWidth + wByteIdex] = (byte) (0x00);
                            bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) (0x00 & 0xFF);
                            bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) (0x00 & 0xFF);
                        }else {
                            bmpData[nRealCol * wWidth + wByteIdex] = (byte) (0xff);
                            bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) (0xff & 0xFF);
                            bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) (0xff & 0xFF);
                        }
                        wByteIdex += 3;
                    }
                }
            fos.write(bmpData);
            fos.flush();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("-->>>>  " + e.toString());
        }finally {
            fos.close();
            fosToTxt.close();
        }

    }

    public static void saveDataToBmp(byte[] datas){
        String path = ALBUM_PATH + BIT_NAME;

        String path_res = ALBUM_PATH + "/point_data_result.bmp";

        FileOutputStream fos = null;

        int nBmpWidth = 384;

        try {
            File write_file = new File(path_res);
            fos = new FileOutputStream(write_file);


            int line_byte_num = nBmpWidth/8;
            int nBmpHeight = datas.length/line_byte_num;
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            int bufferSize = nBmpHeight * (nBmpWidth*3);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头ͷ
            writeWord(fos, bfType);
            writeDword(fos, bfSize);
            writeWord(fos, bfReserved1);
            writeWord(fos, bfReserved2);
            writeDword(fos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头ͷ
            writeDword(fos, biSize);
            writeLong(fos, biWidth);
            writeLong(fos, biHeight);
            writeWord(fos, biPlanes);
            writeWord(fos, biBitCount);
            writeDword(fos, biCompression);
            writeDword(fos, biSizeImage);
            writeLong(fos, biXpelsPerMeter);
            writeLong(fos, biYPelsPerMeter);
            writeDword(fos, biClrUsed);
            writeDword(fos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];

            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth/8; wRow++) {
                    int index_24 = line_byte_num * nCol + wRow;
                    int clr = datas[index_24];
                    for (int i = 0; i < 8; i++) {
                        int one  =( clr >> (7 - i)) & 0x01;
                        if(one == 1) {
                            bmpData[nRealCol * wWidth + wByteIdex] = (byte) (0x00);
                            bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) (0x00 & 0xFF);
                            bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) (0x00 & 0xFF);
                        }else {
                            bmpData[nRealCol * wWidth + wByteIdex] = (byte) (0xff);
                            bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) (0xff & 0xFF);
                            bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) (0xff & 0xFF);
                        }
                        wByteIdex += 3;
                    }
                }
            fos.write(bmpData);
            fos.flush();
            fos.close();
        } catch (Exception e) {

        }finally {
        }
    }





    private static byte[] TurnBytes(Bitmap bitmap) {
        int W = bitmap.getWidth();
        int H = bitmap.getHeight();
        byte[] bt = new byte[W / 8 * H];
        int idx = 0;
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j = j + 8) {
                byte value = 0;
                for (int s = 0; s <= 7; s++) {
                    int a = bitmap.getPixel(j + s, i);
                    int aa = a & 0xff;
                    if (aa != 255) {
                        value |= 1 << s;
                    }
                }
                bt[idx] = value;
                idx++;
            }
        }
        return bt;
    }



}
