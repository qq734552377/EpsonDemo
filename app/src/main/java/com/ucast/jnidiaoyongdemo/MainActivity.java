package com.ucast.jnidiaoyongdemo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.Serial.SerialTest;
import com.ucast.jnidiaoyongdemo.db.UploadDBHelper;
import com.ucast.jnidiaoyongdemo.erweima.view.mysaomiao.CaptureActivity;
import com.ucast.jnidiaoyongdemo.testActs.TestMainActivity;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sendBtn ;
    private TextView tv;
    private TextView msTv;
    private RadioGroup auto_moneybox_radio_group;
    private RadioGroup select_service_radio_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        msTv = (TextView) findViewById(R.id.mscard);
        msTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        auto_moneybox_radio_group = findViewById(R.id.open_auto_moneybox);

        auto_moneybox_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.auto_open:
                        SavePasswd.getInstace().save(SavePasswd.ISAUTOMONEYBOX,SavePasswd.OPEN);
                        SavePasswd.getInstace().savexml(SavePasswd.ISAUTOMONEYBOX,SavePasswd.OPEN);
                        break;
                    case R.id.man_open:
                        SavePasswd.getInstace().save(SavePasswd.ISAUTOMONEYBOX,SavePasswd.CLOSE);
                        SavePasswd.getInstace().savexml(SavePasswd.ISAUTOMONEYBOX,SavePasswd.CLOSE);
                        break;
                }
            }
        });
        select_service_radio_group = findViewById(R.id.select_service);
        select_service_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_service:
                        setHost(true);;
                        SavePasswd.getInstace().save(SavePasswd.ISMAINSERVICE,SavePasswd.OPEN);
                        break;
                    case R.id.test_service:
                        setHost(false);
                        SavePasswd.getInstace().save(SavePasswd.ISMAINSERVICE,SavePasswd.CLOSE);
                        break;
                }
            }
        });



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
                String path = Environment.getExternalStorageDirectory().getPath() + "/ucast.bmp";
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
        findViewById(R.id.open_moneybox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTools.openMoneyBox();
            }
        });
        EventBus.getDefault().register(this);
//        MyTools.setCameraFixed();
//        startTestSerial();
        startUcastApkLockService();

    }
    public void startTestSerial(){
        String path = "/dev/ttymxc2";
        SerialTest print = new SerialTest(path);
        boolean isOpen = print.Open();
        if (isOpen){
            print.Send(path + " test ,if you see it ,it's ok !");
        }
    }


    public void startAc(int type) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtra(CaptureActivity.CAMERAKEY, type);
        startActivityForResult(intent, type);
    }

    public void startUcastApkLockService(){
        try {
            Intent intent = new Intent();
            intent.setClassName("com.ucast.applock_service","com.ucast.applock_service.MainActivity");
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "没有安装ucast_apklock", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDataBaseShow() {
        String isOpenMoneyBox = SavePasswd.getInstace().readxml(SavePasswd.ISAUTOMONEYBOX,SavePasswd.OPEN);
        boolean isOPen = isOpenMoneyBox.equals(SavePasswd.OPEN) ? true : false;
        if (isOPen){
            auto_moneybox_radio_group.check(R.id.auto_open);
        }else{
            auto_moneybox_radio_group.check(R.id.man_open);
        }

        String isMainService = SavePasswd.getInstace().getIp(SavePasswd.ISMAINSERVICE,SavePasswd.OPEN);
        boolean isMain = isMainService.equals(SavePasswd.OPEN) ? true : false;
        if (isMain){
            select_service_radio_group.check(R.id.main_service);
        }else{
            select_service_radio_group.check(R.id.test_service);
        }




        tv.setText("");
        int pathNum = 0;
        int datahNum = 0;
        List<UploadData> list = UploadDBHelper.getInstance().selectAll();
        if (list == null){
            tv.setText("数据库没数据");
            tv.setText(Config.DEVICE_ID);
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

    public void setHost(boolean isMain){
        if(isMain){
            YinlianHttpRequestUrl.setMainServiceUrl();
        }else{
            YinlianHttpRequestUrl.setTestServiceUrl();
        }
        msTv.setText( "图片上传地址：" + YinlianHttpRequestUrl.UPLOADFILEURL);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showMsg(String msData) {
        msTv.setText(msData);
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
//        MyTools.setCameraAuto();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        getDataBaseShow();
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //主界面右上角的menu菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this,"setttings",Toast.LENGTH_LONG).show();
                break;
            case R.id.test_device:
                startActivity(new Intent(this, TestMainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }








    private static final String TAG = "MainActivity";
}
