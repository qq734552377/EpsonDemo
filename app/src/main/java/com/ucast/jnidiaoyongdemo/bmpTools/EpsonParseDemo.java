package com.ucast.jnidiaoyongdemo.bmpTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class EpsonParseDemo {

    /* 产生钱箱驱动脉冲 */
    public static final byte[] MONEY_BOX = new byte[]{0x1B, 0x70, 0x00, 0x45, 0x45};


    /*设置字号*/
    public static final byte[] FONT_SIZE = new byte[]{0x1D, 0x21};
    /*设置字号   1倍*/
    public static final byte[] FONT_SIZE_1 = new byte[]{0x1D, 0x21, 0x00};
    /*设置字号   2倍*/
    public static final byte[] FONT_SIZE_2 = new byte[]{0x1D, 0x21, 0x01};

    /*设置着重操作*/
    public static final byte[] FONT_BOLD = new byte[]{0x1B, 0x45};
    /*设置着重*/
    public static final byte[] FONT_BOLD_YES = new byte[]{0x1B, 0x45, 0x01};
    /*取消着重*/
    public static final byte[] FONT_BOLD_NO = new byte[]{0x1B, 0x45, 0x00};
    public static final String startEpsonStr = "1D 38 4C";
    public static final String endEpsonStr = "1D 28 4C";


    //将指定byte数组以16进制的形式打印到控制台
    public static String printHexString(byte[] b) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r.append(hex.toUpperCase() + " ");
        }

        return r.toString();

    }

    public static byte[] addAByte(byte element, byte[] res) {
        if (res == null) {
            byte[] newBy = new byte[1];
            newBy[0] = element;
            return newBy;
        }
        byte[] newBy = new byte[res.length + 1];
        System.arraycopy(res, 0, newBy, 0, res.length);
        newBy[res.length] = element;

        return newBy;

    }


    public static List<String> getEpsonFromStringArr(String[] bytes) {
        List<String> epsonListString = new ArrayList<>();
        int indexStr = -1;
        System.out.println(epsonListString.toString());
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i].equals("1B") || bytes[i].equals("1C") || bytes[i].equals("1D")) {
                indexStr++;
                String a = new String(bytes[i]);
                epsonListString.add(a);
            } else {
                if (indexStr == -1) {
                    continue;
                }
                String res = (String) epsonListString.get(indexStr);
                res += " " + bytes[i];
                epsonListString.set(indexStr, res);
            }
        }

//          System.out.println(epsonListString.toString());
        for (int i = 0; i < epsonListString.size(); i++) {
            System.out.println(epsonListString.get(i));
        }

        return epsonListString;
    }

    public static List<byte[]> getEpsonFromByteArr(byte[] datas) {
        List<byte[]> epsonList = new ArrayList<>();
        int index = -1;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] == 27 || datas[i] == 28 || datas[i] == 29) {
                index++;
                byte[] headBy = addAByte(datas[i], null);
                epsonList.add(headBy);
            } else {
                if (index == -1) {
                    continue;
                }
                byte[] res = (byte[]) epsonList.get(index);
                epsonList.set(index, addAByte(datas[i], res));
            }
        }
//        for (int i = 0; i < epsonList.size(); i++) {
//       	 byte[] res = (byte [])epsonList.get(i);
//       	 for (int j = 0; j < res.length; j++) {
//				System.out.print(res[j]);
//			}
//       	 System.out.println("   ");
//		}
//
        return epsonList;
    }


    //判断数组是否相等
    public static boolean isArrEqual(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    //判断数组是否相等
    public static boolean isArr2HeadEqual(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < 2; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public static List<PrintAndDatas> parseEpsonByteList(List<byte[]> lists) throws UnsupportedEncodingException {
        List<PrintAndDatas> printLists = new ArrayList<>();
        PrintAndDatas one_data = null;
        for (int i = 0; i < lists.size(); i++) {
            byte[] b = lists.get(i);

            while (b.length <= 7 && b[b.length - 1] == 0x0A) {
                byte[] b_tem = new byte[b.length - 1];
                System.arraycopy(b, 0, b_tem, 0, b.length - 1);
                b = null;
                b = b_tem;
            }
            if (one_data == null) {
                one_data = new PrintAndDatas();
            }

            switch (b.length) {
                case 2:
                    //目前可以忽略
                    continue;
                case 3:
                    setPrintAndDataWithEpson(one_data, b);
                    break;
                case 4:
                    //目前可以忽略
                    continue;
                case 5:
                    //目前可以忽略
                    continue;
                case 6:
                    //目前可以忽略
                    continue;
                case 7:
                    //目前可以忽略
                    continue;

                default:
                    if (b.length > 7) {
                        int position = 0;
                        byte[] datas = null;
                        byte[] lastEpson = null;


                        for (int j = 0; j < b.length; j++) {
                            if (b[j] == 0x00 || b[j] == 0x01) {
                                position = j + 1;
                                datas = new byte[b.length - position];
                                System.arraycopy(b, position, datas, 0, datas.length);

                                lastEpson = new byte[position];
                                System.arraycopy(b, 0, lastEpson, 0, position);

                                setPrintAndDataWithEpson(one_data, lastEpson);

                                one_data.datas = new String(datas, "GB18030");

                                if (!one_data.datas.equals("")) {
                                    printLists.add(one_data);
                                    one_data = null;
                                }

                                break;
                            }

                            if (b[j] == 0x0A || b[j] == 0x20 || b[j] == 0x2D) {
                                position = j;
                                datas = new byte[b.length - position];
                                System.arraycopy(b, position, datas, 0, datas.length);

                                lastEpson = new byte[position];
                                System.arraycopy(b, 0, lastEpson, 0, position);

                                setPrintAndDataWithEpson(one_data, lastEpson);

                                one_data.datas = new String(datas, "GB18030");

                                if (!one_data.datas.equals("")) {
                                    printLists.add(one_data);
                                    one_data = null;
                                }
                                break;
                            }

                        }
                    }
                    break;
            }
        }
        for (int i = 0; i < printLists.size(); i++) {
            PrintAndDatas one = printLists.get(i);
            System.out.println(one.FONT_SIZE_TIMES + "    " + one.FONT_SIZE_TYPE);
            System.out.println(one.datas);
        }
        return printLists;
    }


    public static void setPrintAndDataWithEpson(PrintAndDatas one_data, byte[] b) {
        if (isArrEqual(b, FONT_SIZE_1)) {
            one_data.FONT_SIZE_TIMES = 1;
        } else if (isArrEqual(b, FONT_SIZE_2)) {
            one_data.FONT_SIZE_TIMES = 2;
        } else if (isArr2HeadEqual(b, FONT_SIZE)) {
            one_data.FONT_SIZE_TIMES = 2;
        } else if (isArrEqual(b, FONT_BOLD_NO)) {
            one_data.FONT_SIZE_TYPE = 0;
        } else if (isArrEqual(b, FONT_BOLD_YES)) {
            one_data.FONT_SIZE_TYPE = 1;
        }
    }


    public static List<String> parseEpsonBitData(String datas) {
//        FileInputStream fis= null;
        List<String> bmpPaths = new ArrayList<>();
        try {
//            fis = new FileInputStream(new File(path));
//            StringBuilder sb =new StringBuilder();
//            byte []  buf = new byte[1024];
//            int num = 0;
//            while((num = fis.read(buf))!=-1) {
//                sb.append(new String(buf, 0, num));
//            }
//            String str = sb.toString().trim();

            int lineNumStart = 39;
            int dataStart = 51;
            String str = datas.trim();
            List<EpsonBitData> bitPicLists = new ArrayList<>();


            while (str.indexOf(startEpsonStr) >= 0) {
                int start = str.indexOf(startEpsonStr);
                int end = str.indexOf(endEpsonStr);
                if (start > end)
                    return null;
                String oneBitData = str.substring(start, end);

                String lineNumstr = oneBitData.substring(lineNumStart, lineNumStart + 6);
                String[] numStrs = lineNumstr.split(" ");
                int high = Integer.parseInt(numStrs[1].substring(0), 16);
                int low = Integer.parseInt(numStrs[0].substring(0), 16);
                int line_num = (high * 256 + low + 7) / 8;
                String dataStr = oneBitData.substring(dataStart);

                if (bitPicLists.size() > 0) {
                    EpsonBitData lastBit = bitPicLists.get(bitPicLists.size() - 1);
                    if (lastBit.getWith() == line_num * 8) {
                        lastBit.addDatas(dataStr);
                        str = str.substring(end + 21);
                        continue;
                    }
                }
                EpsonBitData oneBit = new EpsonBitData();
                oneBit.setWith(line_num * 8);
                oneBit.setDatas(dataStr);
                bitPicLists.add(oneBit);
                str = str.substring(end + 21);
            }
            for (int i = 0; i < bitPicLists.size(); i++) {
                String bmpPath = EpsonPicture.ALBUM_PATH + File.separator + "Ucast/" + "ucast_bit_" + i + ".bmp";
                EpsonBitData one = bitPicLists.get(i);
                saveAsBitmapWithByteData(one.getByteDatas(), one.getWith(), bmpPath);
                bmpPaths.add(bmpPath);
            }
//            fis.close();
        } catch (Exception e) {
            return null;
        }
        return bmpPaths;
    }


    public static void saveAsBitmapWithByteData(byte[] datas ,int with ,String path) {
        FileOutputStream fos = null;
        int nBmpWidth = with;

        try {
            fos = new FileOutputStream(new File(path));
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
                        int one  =( clr >> (7-i)) & 0x01;
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
            // TODO: handle exception
        }
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

    public static byte[] addByteArrToByteArr(byte[] dest ,byte[] src,int srcLen) {
        if(dest == null) {
            byte[] datas = new byte[srcLen];
            System.arraycopy(src, 0, datas, 0, srcLen);
            return datas;
        }
        byte[] datas = new byte[dest.length + srcLen];

        System.arraycopy(dest, 0, datas, 0, dest.length);
        System.arraycopy(src, 0, datas, dest.length, srcLen);

        return datas;
    }

}
