package com.ucast.jnidiaoyongdemo.bmpTools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;

import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 * 描述：打印图片生成
 */
public class GeneratePicture {
    public final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory().toString();
    private final static String BIT_NAME = "/ucast.bmp";

    private final static int LINE_STRING_NUMBER = 30 ;
    private final static int OFFSET_X = 10 ;
    private final static int OFFSET_Y = 40 ;
    private final static int FONT_SIZE = 24 ;
    private final static int FONT_SIZE_TIMES = 1 ;
    private final static int LINE_HEIGHT = 40 ;
    private final static String FONT = "fangsong_GB2312.ttf" ;
    private final static int BITMAP_END_POINT = 374 ;

    public static String getBitMap(String string) {
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
        int Height = list.size() * LINE_HEIGHT * FONT_SIZE_TIMES;
        Bitmap bmp = Bitmap.createBitmap(384, Height + 220, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.WHITE);
        Paint print = new Paint();
        print.setColor(Color.BLACK);
        print.setTextSize(FONT_SIZE * FONT_SIZE_TIMES);
//        print.setTypeface(Typeface.MONOSPACE);
        Typeface font = Typeface.createFromAsset(ExceptionApplication.getInstance().getAssets(),FONT);
        print.setTypeface(Typeface.create(font,Typeface.BOLD));
        for (int i = 0; i < list.size(); i++) {
            canvas.drawText(list.get(i), OFFSET_X, i * LINE_HEIGHT * FONT_SIZE_TIMES + OFFSET_Y, print);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        String path = saveBmp(bmp);
        return path;
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
    private static String saveBmp(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth / 4);
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
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
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
}
