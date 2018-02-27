package com.ucast.jnidiaoyongdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.jnidiaoyongdemo.Serial.NoHeadEndPort;
import com.ucast.jnidiaoyongdemo.Serial.NoHeadEnd_16HEXPort;
import com.ucast.jnidiaoyongdemo.Serial.PADPort;
import com.ucast.jnidiaoyongdemo.Serial.SerialPort;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.bmpTools.GeneratePicture;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.user_permision.GetUsePermision;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoHeadEnd_16HEXPort padProt;
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        iv = (ImageView) findViewById(R.id.imageView);
//        iv.setVisibility(View.INVISIBLE);



        padProt = new NoHeadEnd_16HEXPort("/dev/g_printer0", 115200, handler);
        padProt.Open();



        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                printOne();
                Toast.makeText(MainActivity.this, "11", Toast.LENGTH_SHORT).show();
//                if (output != null){
//                    try {
//                        output.write("hhhhhhh".getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                if (padProt != null) {
                    padProt.SendMessage("this is from android devices");
                }


//                Paint print = new Paint();
//                print.setColor(Color.BLACK);
//                print.setStrokeCap(Paint.Cap.BUTT);
//                print.setTextSize(24);
////                print.setTypeface(Typeface.MONOSPACE);
//                Typeface font = Typeface.createFromAsset(getAssets(),"simsun.ttc ");
//                print.setTypeface(font);
//                float a= print.measureText("玩");
//                float b = print.measureText("A");
//
//                tv.setText(a+"    "+ b);

//                GetUsePermision.requestPermission(MainActivity.this, GetUsePermision.CODE_CAMERA, mPermissionGrant);
            }
        });
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        GetUsePermision.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private GetUsePermision.PermissionGrant mPermissionGrant = new GetUsePermision.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case GetUsePermision.CODE_RECORD_AUDIO:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_GET_ACCOUNTS:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_READ_PHONE_STATE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_CALL_PHONE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_CAMERA:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_ACCESS_FINE_LOCATION:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_ACCESS_COARSE_LOCATION:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case GetUsePermision.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what){
                case 10:
                    byte[] data = (byte[]) msg.obj;
//                    tv.setText(new String(data));
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/ucast/port.txt");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        fos.write(data);
//                        fos.write("\n-----000000000000000000000000-----\n".getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,new String(data),Toast.LENGTH_SHORT).show();

                    break;

                case 20:
                    String dataString = (String) msg.obj;
                    tv.setText("");
                    long oldTime = System.currentTimeMillis();
                    StringBuilder sb = new StringBuilder();
                    try {
                        if(dataString.contains(EpsonParseDemo.startEpsonStr)){
                            oldTime = System.currentTimeMillis();
                            List<String> paths = EpsonParseDemo.parseEpsonBitData(dataString.trim());
                            sb.append("解析EPSON生成图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

                            int pathNum = paths.size();
                            if ( pathNum< 1) {
                                Bitmap bit = BitmapFactory.decodeFile(paths.get(0));
                                tv.setText(sb.toString());
                                String path = EpsonPicture.saveBmpUse1Bit(SomeBitMapHandleWay.addHeadAndEndCutPosition(bit));
                                iv.setImageBitmap(BitmapFactory.decodeFile(path));
                                return;
                            }
                            Bitmap allBitMap = null;
                            oldTime = System.currentTimeMillis();
                            for (int i = 0; i < pathNum; i++) {
                                if (allBitMap == null){
                                    allBitMap= BitmapFactory.decodeFile(paths.get(i));
                                }
                                if(i + 1 == pathNum){
                                    break;
                                }
                                Bitmap backBitMap = BitmapFactory.decodeFile(paths.get(i + 1));

                                allBitMap = SomeBitMapHandleWay.mergeBitmap_TB(allBitMap,backBitMap);

                            }
                            sb.append("合成为一张图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

                            if (allBitMap != null){
                                oldTime = System.currentTimeMillis();
                                Bitmap bit = SomeBitMapHandleWay.addHeadAndEndCutPosition(allBitMap);
                                sb.append("加入切纸位的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
                                if (bit == null )
                                    return;
                                oldTime = System.currentTimeMillis();
                                String path = EpsonPicture.saveBmpUse1Bit(bit);
                                sb.append("保存为bmp图片文件的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
                                if (path != null && path != ""){
                                    iv.setImageBitmap(BitmapFactory.decodeFile(path));
                                    tv.setText(sb.toString());
                                }
                            }
                            return;
                        }

                        oldTime = System.currentTimeMillis();
                        String path = printOne(dataString);
                        sb.append("元数据保存为bmp图片文件的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
                        if (path != null && path != ""){
                            iv.setImageBitmap(BitmapFactory.decodeFile(path));
                        }
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/ucast/port_str.txt");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        fos.write(dataString.getBytes());
                        fos.write("\n-----000000000000000000000000-----\n".getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:

                    break;
            }


//            String [] bytes = data.split(" ");
//
//            byte [] datas =new byte[bytes.length];
//
//            for (int i = 0; i < bytes.length; i++) {
//                datas[i] = (byte) Integer.parseInt(bytes[i].substring(2), 16);
//            }
//
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(datas);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//
//            byte[] buf = new byte[1024];
//
//            int length = 0;
//            String path =null;
//            try{
//                while((length = bais.read(buf)) > 0){
//                    baos.write(buf, 0, length);
//                }
//                System.out.println(baos.toString("GB18030"));
//                path = GeneratePicture.getBitMap(baos.toString("GB18030"));
//                Log.e(TAG, "handleMessage: "+ path );
//                bais.close();
//                baos.close();
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//
//            if (path != null && path != ""){
//                iv.setImageBitmap(BitmapFactory.decodeFile(path));
//            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        if (padProt != null)
            this.padProt.Dispose();
        super.onDestroy();
    }

    public String printOne(String data){


        String [] bytes = data.split(" ");
        byte [] datas =new byte[bytes.length];
        String path = "" ;


        for (int i = 0; i < bytes.length; i++) {
            datas[i] = (byte) Integer.parseInt(bytes[i], 16);
        }

//             List<String> list = getEpsonFromStringArr(bytes);
        List<byte[]> byteList = EpsonParseDemo.getEpsonFromByteArr(datas);
        byte [] b = (byte[])byteList.get(byteList.size()-2);

        try {
            List<PrintAndDatas> printdatas = EpsonParseDemo.parseEpsonByteList(byteList);

            path = EpsonPicture.getBitMap(printdatas);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return path;
        }

    }
    public String printOneBitPic(String data){



        return null;
    }





    private static final String TAG = "MainActivity";
}
