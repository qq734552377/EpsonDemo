package com.ucast.jnidiaoyongdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ucast.jnidiaoyongdemo.testActs.BaseNavActivity;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseNavActivity {

    @ViewInject(R.id.upload_pic_edt)
    EditText upload_pic_host;
    @ViewInject(R.id.heart_beat_edt)
    EditText heart_beat_host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initToolbar(getString(R.string.settings));
        upload_pic_host.setText(save.readxml(SavePasswd.BMPUPLOADHOST,YinlianHttpRequestUrl.ANALYZEHOST));
        heart_beat_host.setText(save.readxml(SavePasswd.HEARTBEATHOST,YinlianHttpRequestUrl.HEART_BEAT_HOST));
    }

    @Event(R.id.set_host_btn)
    private void setHost(View v){
        String new_upload_pic_host = upload_pic_host.getText().toString().trim();
        String new_heartbeat_host = heart_beat_host.getText().toString().trim();
        //存到xml中
        save.save(SavePasswd.BMPUPLOADHOST,new_upload_pic_host);
        save.savexml(SavePasswd.BMPUPLOADHOST,new_upload_pic_host);
        save.save(SavePasswd.HEARTBEATHOST,new_heartbeat_host);
        save.savexml(SavePasswd.HEARTBEATHOST,new_heartbeat_host);
        YinlianHttpRequestUrl.ANALYZEHOST = new_upload_pic_host;
        YinlianHttpRequestUrl.HEART_BEAT_HOST= new_heartbeat_host;
        YinlianHttpRequestUrl.setMainServiceUrl();

        showToast("设置成功");
    }
}
