package com.ucast.jnidiaoyongdemo.testActs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ucast.jnidiaoyongdemo.R;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class BaseNavActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public SavePasswd save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        save = SavePasswd.getInstace();
    }


    public void initToolbar(String s) {
        if (toolbar == null)
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(s);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void setViewBackgronudFail(int id){
        findViewById(id).setBackgroundColor(Color.RED);
    }

    public void setViewBackgronudSuccess(int id){
        findViewById(id).setBackgroundColor(Color.GREEN);
    }

    public void setImageViewBackgroundFail(ImageView iv){
        iv.setBackgroundColor(Color.RED);
    }
    public void setImageViewBackgroundSuccess(ImageView iv){
        iv.setBackgroundColor(Color.GREEN);
    }

    public void setEnable(int id,boolean select){
        findViewById(id).setEnabled(select);
    }
}
