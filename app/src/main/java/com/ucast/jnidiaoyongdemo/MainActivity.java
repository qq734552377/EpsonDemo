package com.ucast.jnidiaoyongdemo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.Serial.UsbSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;
import com.ucast.jnidiaoyongdemo.Serial.SerialPort;
import com.ucast.jnidiaoyongdemo.db.UploadDBHelper;
import com.ucast.jnidiaoyongdemo.erweima.view.mysaomiao.CaptureActivity;
import com.ucast.jnidiaoyongdemo.tools.MyDialog;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UsbSerial usbPort;
    private OpenPrint print;
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private Button sendBtn ;
    private TextView tv;
    private TextView msTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        msTv = (TextView) findViewById(R.id.mscard);
        msTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        Intent ootStartIntent = new Intent(this, UpdateService.class);
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startService(ootStartIntent);
        sendBtn = findViewById(R.id.send);
//        sendBtn.setVisibility(View.INVISIBLE);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_SHORT).show();
//                String str  = "http://www.ucast.sg";
//                KeyboardSwitch.sendToKeyboard(str);
//                MyDialog.showDialogWithMsg("这是对话框",114).show();
                String path = Environment.getExternalStorageDirectory().getPath() + "/ums.bmp";
                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path),true));
            }
        });

        findViewById(R.id.open_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAc(0);
            }
        });
        findViewById(R.id.open_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAc(1);
            }
        });
        EventBus.getDefault().register(this);
    }
    public void startAc(int type) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtra(CaptureActivity.CAMERAKEY, type);
        startActivityForResult(intent, type);
    }


    public void getDataBaseShow() {
        tv.setText("");
        int pathNum = 0;
        int datahNum = 0;
        List<UploadData> list = UploadDBHelper.getInstance().selectAll();
        if (list == null){
            tv.setText("数据库没数据");
            return;
        }

        for (int i = 0; i < list.size() ; i++) {
            UploadData one = list.get(i);
            if(one.getType() == UploadData.PATH_TYPE){
                pathNum ++ ;
            }else{
                datahNum ++ ;
            }
        }
        tv.setText("所有的数据为:" + list.size()+" \n未上传的路径数据为："+pathNum+" \n 为上传的数据为："+datahNum);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showMsg(String msData) {
        msTv.setText(msData);
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        getDataBaseShow();
        super.onResume();
    }

    private static final String TAG = "MainActivity";
}
