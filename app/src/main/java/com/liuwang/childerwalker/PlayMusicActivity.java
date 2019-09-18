package com.liuwang.childerwalker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayMusicActivity extends BaseActivity {
    private Button music1,music2,music3,music4,music5,music6,music7;

    private String topple,fall,incar,fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        initNavBar(true,"音乐播放");
        initView();
    }

    public void initView(){
        music1=findViewById(R.id.music1);
        music2=findViewById(R.id.music2);
        music3=findViewById(R.id.music3);
        music4=findViewById(R.id.music4);
        music5=findViewById(R.id.music5);
        music6=findViewById(R.id.music6);
        music7=findViewById(R.id.music7);

        music1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("B");
            }
        });
        music2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("C");
            }
        });
        music3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("D");
            }
        });
        music4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("E");
            }
        });
        music5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("F");
            }
        });
        music6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("G");
            }
        });
        music7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDateByOkhttpUtils("A");
            }
        });
    }

    public void NotificationInit(String title, String text, Object object,int id) {
        //获取系统的NotificationManager服务
        final NotificationManager notificationManager=
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //创建一个Notification对象
        Notification.Builder notification=new Notification.Builder(this);
        //设置打开该通知，该通知自动消失
        notification.setAutoCancel(true);
        //设置通知的图标
        notification.setSmallIcon(R.mipmap.packet);
        //设置通知的标题内容
        notification.setContentTitle(title);
        //设置通知内容
        notification.setContentText(text);
        //设置使用系统默认的声音，默认震动
        notification.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        //设置发送时间
        notification.setWhen(System.currentTimeMillis());
        //创建一个启动其他Activity的Intent
        Intent intent=new Intent(PlayMusicActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(PlayMusicActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
        //设置通知栏点击跳转
        notification.setContentIntent(pi);
        //发送通知
        notificationManager.notify(id,notification.build());
    }


    private void postDateByOkhttpUtils(String ch){
        String url="https://www.zhoujie.fun/control/";
        OkHttpUtils.post().url(url)
                .id(100)//请求的id
                .addParams("music",ch)
                .build()
                .execute(new MyStringCallBack());
    }

    private class MyStringCallBack extends StringCallback {

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
            setTitle("sample-okhttp");
        }

        @Override
        public void onError(Call call, Exception e, int i) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(String s, int i) {
            switch (i){
                case 100:
                    JWG(s);
//                    Toast.makeText(RangeSettingActivity.this,"http:上传成功",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    Toast.makeText(PlayMusicActivity.this,"https:上传成功",Toast.LENGTH_LONG).show();
                    break;
                default :
                    break;
            }
        }

    }

    private void JWG(String json) {
        try {
            JSONObject jsonObject=new JSONObject(json);

            incar=jsonObject.getString("incar");
            fire=jsonObject.getString("fire");
            topple=jsonObject.getString("topple");
            fall=jsonObject.getString("fall");

            if (incar.equals("0")){
                NotificationInit("是否在车","婴儿不在车内...",MainActivity.class,9);
            }
            if (fire.equals("1")){
                NotificationInit("火势预警","周边可能有火灾发生...",MainActivity.class,10);
            }
            if (topple.equals("1")){//倾斜
                NotificationInit("倾斜预警","婴儿车有侧翻的危险...",MainActivity.class,11);
            }
            if (fall.equals("1")){//倾斜
                NotificationInit("高低预警","婴儿车很危险...",MainActivity.class,12);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
