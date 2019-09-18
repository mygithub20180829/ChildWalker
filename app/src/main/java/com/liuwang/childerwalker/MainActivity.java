package com.liuwang.childerwalker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private int id=0;
    private TimerTask mTimerTask;
    private String text;
    private ImageView music;

    private int intemperature,inhumidity;
    private String air,fire,topple,fall,incar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavBar(false,"BabyWalker");

        music=findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PlayMusicActivity.class));
            }
        });

        //轮询，不断向服务器发请求
        startTimer(1000,1000,mHandeler);


//        handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent=new Intent();
//                //bundle中传入消息到MyReceiver
//                intent.setAction("testScreenDown");
//                sendBroadcast(intent);
//            }
//        },5*1000);

    }

    public void NotificationInit(String title, String text, Object object) {
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
        Intent intent=new Intent(MainActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(MainActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
        //设置通知栏点击跳转
        notification.setContentIntent(pi);
        //发送通知
        notificationManager.notify(id++,notification.build());
        notificationManager.cancel(10);
    }

    public Timer startTimer(long delay, long pre, final Handler mHandeler1) {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandeler1.sendMessage(message);
            }
        };
        Timer timer = new Timer();
        timer.schedule(mTimerTask, pre, delay);
        return timer;
    }

    private Handler mHandeler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            {
                switch (msg.what) {
                    case 1://要轮询的功能
                        getDateByOkhttpUtils();
                        break;
                }
            }
        }
    };

    private void getDateByOkhttpUtils(){
        String url ="https://www.zhoujie.fun/allalarm/";

        OkHttpUtils.get().url(url)
                .id(100)
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
                    text=s;
                    JWG(text);
//                    Toast.makeText(MainActivity.this,"http",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    text=s;
                    JWG(text);
                    Toast.makeText(MainActivity.this,"https", Toast.LENGTH_LONG).show();
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
                NotificationInit("是否在车","婴儿不在车内...",MainActivity.class);
            }

            if (fire.equals("1")){
                NotificationInit("火势预警","周边可能有火灾发生...",MainActivity.class);
            }

            if (topple.equals("1")){//倾斜
                NotificationInit("倾斜预警","婴儿车有侧翻的危险...",MainActivity.class);
            }

            if (fall.equals("1")){//倾斜
                NotificationInit("高低预警","婴儿车很危险...",MainActivity.class);
            }

            intemperature=jsonObject.getInt("intemperature");
            inhumidity=jsonObject.getInt("inhumidity");
            air=jsonObject.getString("air");


//            if (intemperature==1){
//                NotificationInit("温度预警","婴儿车温度过高...",FallingDangerActivity.class,PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            if (inhumidity==1){
//                NotificationInit("车内湿度","婴儿车湿度过大...",FallingDangerActivity.class,PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            if (air.equals("1")){
//                NotificationInit("空气质量","当前空气质量不佳...",AirActivity.class,PendingIntent.FLAG_UPDATE_CURRENT);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBaybyStatusClick(View v){
        startActivity(new Intent(MainActivity.this,BabyBodyActivity.class));
    }

    public void onEnvironmentClick(View v){
        startActivity(new Intent(MainActivity.this,ExternalEnvironmentActivity.class));
    }

    public void onCarStatusClick(View v){
        startActivity(new Intent(MainActivity.this,CarStatusActivity.class));
    }

    public void onRangeSettingClick(View v){
        startActivity(new Intent(MainActivity.this,RangeSettingActivity.class));
    }
}
