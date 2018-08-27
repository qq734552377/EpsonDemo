package com.ucast.jnidiaoyongdemo.advAct;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.R;
import com.ucast.jnidiaoyongdemo.jsonObject.BaseAdvResult;
import com.ucast.jnidiaoyongdemo.jsonObject.BaseHttpResult;
import com.ucast.jnidiaoyongdemo.jsonObject.ImgAdvResult;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class AdvActivity extends AppCompatActivity {

    Banner banner;
    ArrayList<String> images;
    ArrayList<String> titles;

    Handler handler =new Handler();
    Runnable getAdv_callback = new Runnable() {
        @Override
        public void run() {
            getAdvs();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
               WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_adv);
        banner = findViewById(R.id.banner);

        images = new ArrayList<>();
        titles = new ArrayList<>();


        initAdvList();
        initBanner(images,titles);

    }

    private void initAdvList() {
//        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535098467658&di=ecc62460a66999b7a81f9b65715f24c3&imgtype=0&src=http%3A%2F%2Fpic.uzzf.com%2Fup%2F2017-1%2F14845481489436506.jpg");
//        titles.add("京东");
//        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535098613433&di=6b4360250a5131538aab1d7092b5c0f8&imgtype=0&src=http%3A%2F%2Faliyunzixunbucket.oss-cn-beijing.aliyuncs.com%2Fjpg%2Fa7e37f7a13e37ffe4ce4adfdb8ec2114.jpg%3Fx-oss-process%3Dimage%2Fresize%2Cp_100%2Fauto-orient%2C1%2Fquality%2Cq_90%2Fformat%2Cjpg%2Fwatermark%2Cimage_eXVuY2VzaGk%3D%2Ct_100");
//        titles.add("老马");
        titles.add("银联商务");
        String img_url_base64 = SavePasswd.getInstace().get(SavePasswd.ADVIMGURL);
        String img_msg_base64 = SavePasswd.getInstace().get(SavePasswd.ADVIMGTITLE);
        if (!img_url_base64.equals("")){
            images.clear();
            titles.clear();
            String[] base64_urls = img_url_base64.split(",");
            String[] base64_titles = img_msg_base64.split(",");

            for (int i = 0; i < base64_urls.length; i++) {
                String one = base64_urls[i];
                images.add(new String(MyTools.decode(one)));
            }
            for (int i = 0; i < base64_titles.length; i++) {
                String one = base64_titles[i];
                titles.add(new String(MyTools.decode(one)));
            }
        }

    }

    public void getAdvs(){
        RequestParams params = new RequestParams(YinlianHttpRequestUrl.ADVURL);
        params.addBodyParameter("DeviceID", Config.DEVICE_ID);
        params.setConnectTimeout(1000 * 45);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                BaseHttpResult base = JSON.parseObject(result, BaseHttpResult.class);
                if (base.getMsgType().equals(BaseHttpResult.SUCCESS) && base.getData() != null && !base.getData().equals("")){
                    BaseAdvResult baseAdvResult = JSON.parseObject(base.getData(),BaseAdvResult.class);
                    List<ImgAdvResult> imgAdvResults = JSON.parseArray(baseAdvResult.getImg(),ImgAdvResult.class);
                    if (imgAdvResults.size() > 0){
                        images.clear();
                        titles.clear();
                        StringBuffer img_url_sb = new StringBuffer();
                        StringBuffer img_msg_sb = new StringBuffer();
                        for (int i = 0; i <imgAdvResults.size() ; i++) {
                            ImgAdvResult one = imgAdvResults.get(i);
                            images.add(one.getImgurl());
                            titles.add(one.getImgmsg());
                            String img_url_base64 = MyTools.encode(one.getImgurl().getBytes());
                            String img_msg_base64 = MyTools.encode(one.getImgmsg().getBytes());
                            img_url_sb.append(img_url_base64);
                            img_msg_sb.append(img_msg_base64);
                            if (i < imgAdvResults.size() -1 ){
                                img_url_sb.append(",");
                                img_msg_sb.append(",");
                            }
                        }
                        String save_adv_urls = SavePasswd.getInstace().get(SavePasswd.ADVIMGURL);
                        String get_adv_urls = img_url_sb.toString();
                        if (save_adv_urls.equals(get_adv_urls)){
                            return;
                        }
                        SavePasswd.getInstace().save(SavePasswd.ADVIMGURL,get_adv_urls);
                        SavePasswd.getInstace().save(SavePasswd.ADVIMGTITLE,img_msg_sb.toString());
                        banner.stopAutoPlay();
                        initBanner(images,titles);
                        banner.startAutoPlay();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                showToast("没有获取到服务器广告数据！");
                handler.postDelayed(getAdv_callback,1000 * 10);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }



    public void initBanner(List<String> images,List<String> titles){
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        if (images.size()<=0){
            List<Integer> image_int = new ArrayList<>();
            image_int.add(R.mipmap.yl);
            banner.setImages(image_int);
        }else{
            banner.setImages(images);
        }
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
        getAdvs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
        handler.removeCallbacks(getAdv_callback);
    }

    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
