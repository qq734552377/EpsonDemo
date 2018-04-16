package com.ucast.jnidiaoyongdemo.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;


/**
 * Created by Administrator on 2016/2/16.
 */
public class ReadBitmap {

    private boolean _mDispose;

    private ArrayQueue<BitmapWithOtherMsg> _mQueues = new ArrayQueue<BitmapWithOtherMsg>(0x400);

    // Methods
    public ReadBitmap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WRun();
            }
        }).start();
    }

    /// <summary>
    /// 添加列队
    /// </summary>
    /// <param name="iObj"></param>
    public void Add(BitmapWithOtherMsg iObj) {
        synchronized (ReadBitmap.class) {
            ExceptionApplication.gLogger.info(" add one bitmap to readPicQueue  " +  MyTools.millisToDateString(System.currentTimeMillis()));
            _mQueues.enqueue(iObj);
        }
    }

    /// <summary>
    /// 释放线程
    /// </summary>
    public void Dispose() {
        if (!_mDispose) {
            _mDispose = true;
        }
    }

    private BitmapWithOtherMsg GetItem() {
        synchronized (ReadBitmap.class) {
            if (_mQueues.size() > 0) {
                return _mQueues.dequeue();
            }
            return null;
        }
    }


    private void OnRun() {
        BitmapWithOtherMsg item = GetItem();

        try {
            if (item != null) {
                Bitmap bitmap = item.getBitmap();
                byte[] pictureByte;
                if (bitmap == null){
                    pictureByte  =  EpsonPicture.turnBytes(bitmap);//转换byte数组
                }else{
                    pictureByte = getBmpByteFromBMPFile(item.getPath());
                }
                //解析获得了一张图片的完整信息PictureModel
                PictureModel info = WholeBytes(pictureByte);
                info.setCutPapper(item.isCutPapper());
                if(item.getChannel() != null)
                    info.setChannel(item.getChannel());
                //接收完数据数据后 开始加入到打印队列里并发送
                ListPictureQueue.Add(info);
            } else {
                Thread.sleep(3);
            }
        } catch (Exception e) {

        }
    }

    private void WRun() {
        while (!_mDispose) {
            OnRun();
        }
    }

    private byte[] HeadBytes(int total) {
        byte[] btHead = new byte[11];
        btHead[0] = 0x02;
        btHead[1] = 0x50;
        btHead[2] = 0x43;
        btHead[3] = 0x31;
        btHead[4] = 0x01;
        //数据长度
        btHead[4 + 1] = 0x02;
        btHead[5 + 1] = 0x00;
        //总包数
        btHead[6 + 1] = 0x01;
        btHead[7 + 1] = 0x00;
        //当前包数
        btHead[8 + 1] = 0x30;
        btHead[9 + 1] = 0x00;

        btHead[6 + 1] = (byte) (total & 0Xff);
        btHead[7 + 1] = (byte) ((total & 0Xff00) >> 8);
        return btHead;

    }

    private int PackageTotal(int data_size) {
        return data_size % 960 == 0 ? data_size / 960 : (data_size / 960) + 1;
    }

    private PictureModel WholeBytes(byte[] btData) {

        int package_total = PackageTotal(btData.length); //获取总包数


        byte[] btHead = HeadBytes(package_total);//包头
        PictureModel model = new PictureModel();
        int sum = btData.length % 960;
        byte[] sum_L_H = new byte[2];
        sum_L_H[0] = (byte) ((sum + 4) & 0Xff);
        sum_L_H[1] = (byte) (((sum + 4) & 0Xff00) >> 8);
        int t = 0;
        for (t = 0; t < btData.length / 960; t++) {
            byte[] content_senf = join(btHead, content_send_data(btData, t * 960, 960));
            content_senf[8 + 1] = (byte) ((t + 1) & 0Xff);
            content_senf[9 + 1] = (byte) (((t + 1) & 0Xff00) >> 8);
            content_senf[4 + 1] = (byte) 0xc4;
            content_senf[5 + 1] = (byte) 0x03;

            model.BufferPicture.add(Common.pakageOneProtocol(content_senf));
        }
        if (btData.length % 960 != 0) {
            byte[] content_senf = join(btHead, content_send_data(btData, t * 960, sum));
            content_senf[8 + 1] = (byte) ((t + 1) & 0Xff);
            content_senf[9 + 1] = (byte) (((t + 1) & 0Xff00) >> 8);
            content_senf[4 + 1] = sum_L_H[0];
            content_senf[5 + 1] = sum_L_H[1];

            model.BufferPicture.add(Common.pakageOneProtocol(content_senf));
        }


        model.setOutTime(SystemClock.elapsedRealtime());
        model.setTotal(package_total);
        return model;
    }

    private byte[] join(byte[] a1, byte[] a2) {
        byte[] result = new byte[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

    // 图片分包
    private byte[] content_send_data(byte[] by, int start, int size) {
        byte[] send = new byte[size];
        System.arraycopy(by, start, send, 0, size);
        return send;
    }



    //图片base64之后包装
    private String getPackageString(String cmd, String type, String number, int total, int serial, int len, String data) {
        return "@" + cmd + "," + type + "," + number + "," + total + "," + serial + "," + len + "," + data + "$";
    }

    private byte[] TurnBytes(Bitmap bitmap) {
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

    private byte[] getBmpByteFromBMPFile(String path){

        byte [] allFileData = EpsonPicture.getByteArrayFromFile(path);

        if (allFileData == null){
            return null;
        }

        int  w =  ((allFileData[18] << 0 ) & 0xFF)
                + ((allFileData[19] << 8 ) & 0xFF00)
                + ((allFileData[20] << 16) & 0xFF0000)
                + ((allFileData[21] << 24) & 0xFF000000);
        int  h =  ((allFileData[22] << 0 ) & 0xFF)
                + ((allFileData[23] << 8 ) & 0xFF00)
                + ((allFileData[24] << 16) & 0xFF0000)
                + ((allFileData[25] << 24) & 0xFF000000);

        int bitCount =  ((allFileData[28] << 0 ) & 0xFF)
                      + ((allFileData[29] << 8 ) & 0xFF00);
        //不是1位图数据
        if (bitCount != 1) {
            ExceptionApplication.gLogger.error("Bitmap is not 1 bit ! Paser bitCount = " + bitCount);
            return EpsonPicture.turnBytes(BitmapFactory.decodeFile(path));
        }
        int bmpLen = allFileData.length - 62;
        w = w / 8;
        if( bmpLen != w * h){
            ExceptionApplication.gLogger.error("Bitmap File data length is wrong");
            return EpsonPicture.turnBytes(BitmapFactory.decodeFile(path));
        }
        long oldTime = System.currentTimeMillis();
        byte[] bmpData = new byte[bmpLen];//1位图bmp的所有数据
        System.arraycopy(allFileData, 62, bmpData, 0, bmpLen);
        int print_width = SomeBitMapHandleWay.PRINT_WIDTH / 8;//设置打印机的打印宽度
        byte[] bt = new byte[print_width * h];//打印机的打印数据
        int copy_width = print_width < w ? print_width : w;//取数据选择宽度小的 防止数组越位
        for (int i = 0; i < h ; i ++) {
            for (int j = 0; j < copy_width ; j++) {
                bt[i * print_width + j] = EpsonPicture.fanWei(bmpData[bmpLen - (i + 1) * w + j]);
            }
        }
        ExceptionApplication.gLogger.error("获取一张图片的数据的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
        return bt;
    }



}
