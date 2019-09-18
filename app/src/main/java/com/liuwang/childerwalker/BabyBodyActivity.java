package com.liuwang.childerwalker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class BabyBodyActivity extends BaseActivity {
    private String fire,topple,fall;
    private String text,incar;
    private TextView baby_body_status,baby_body_pee,baby_body_car,
            baby_body_time,baby_body_status_main,baby_body_pee_main;

    private TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_body);

        initNavBar(true,"婴儿生理状况");

        baby_body_status=findViewById(R.id.baby_body_status);
        baby_body_pee=findViewById(R.id.baby_body_pee);
        baby_body_car=findViewById(R.id.baby_body_car);
        baby_body_time=findViewById(R.id.baby_body_time);
        baby_body_status_main=findViewById(R.id.baby_body_status_main);
        baby_body_pee_main=findViewById(R.id.baby_body_pee_main);

        //轮询，不断向服务器发请求
        startTimer(1000,1000,mHandeler);


//        //隐藏标题栏
//        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public Timer startTimer(long delay, long pre, final Handler mHandeler) {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandeler.sendMessage(message);
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
        Intent intent=new Intent(BabyBodyActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(BabyBodyActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
        //设置通知栏点击跳转
        notification.setContentIntent(pi);
        //发送通知
        notificationManager.notify(id,notification.build());
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
//                    Toast.makeText(BabyBodyActivity.this,"http",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    text=s;
                    JWG(text);
//                    Toast.makeText(BabyBodyActivity.this,"https",Toast.LENGTH_LONG).show();
                    break;
                default :
                    break;
            }
        }

    }

    private void getDateByOkhttpUtils(){
        String url ="https://www.zhoujie.fun/babystatus/";

        OkHttpUtils.get().url(url)
                .id(100)
                .build()
                .execute(new MyStringCallBack());
    }

    private void JWG(String json) {
        try {
            JSONObject jsonObject=new JSONObject(json);
//            data=new Data();
//            data.setIncar(jsonObject.getString("incar"));



            if (jsonObject.getString("incar").equals("1")){
                baby_body_car.setText("是");
            }else {
                baby_body_car.setText("否");
            }

            incar=jsonObject.getString("incar");
            fire=jsonObject.getString("fire");
            topple=jsonObject.getString("topple");
            fall=jsonObject.getString("fall");

//            baby_body_car.setText(jsonObject.getString("incar"));
            baby_body_status.setText(String.valueOf(jsonObject.getInt("intemperature")));
            baby_body_pee.setText(String.valueOf(jsonObject.getInt("inhumidity")));
            baby_body_time.setText(String.valueOf(jsonObject.getInt("usetime")));

            if (jsonObject.getInt("intemperature")>37){
                baby_body_status.setText(String.valueOf(jsonObject.getInt("intemperature"))+"   婴儿体温过高");
                baby_body_status_main.setTextColor(Color.RED);
                baby_body_status.setTextColor(Color.RED);
            }else if (jsonObject.getInt("intemperature")<36){
                baby_body_status.setText(String.valueOf(jsonObject.getInt("intemperature"))+"   婴儿体温过低");
                baby_body_status_main.setTextColor(Color.RED);
                baby_body_status.setTextColor(Color.RED);
            }

            if (String.valueOf(jsonObject.getInt("inhumidity")).equals("1")){
                baby_body_pee.setText("是");
                baby_body_pee_main.setTextColor(Color.RED);
                baby_body_pee.setTextColor(Color.RED);
            }else if (String.valueOf(jsonObject.getInt("inhumidity")).equals("0")){
                baby_body_pee.setText("否");
                baby_body_pee_main.setTextColor(Color.BLACK);
                baby_body_pee.setTextColor(Color.BLACK);
            }

            if (incar.equals("0")){
                NotificationInit("是否在车","婴儿不在车内...",MainActivity.class,1);
            }
            if (fire.equals("1")){
                NotificationInit("火势预警","周边可能有火灾发生...",MainActivity.class,2);
            }
            if (topple.equals("1")){//倾斜
                NotificationInit("倾斜预警","婴儿车有侧翻的危险...",MainActivity.class,3);
            }
            if (fall.equals("1")){//倾斜
                NotificationInit("高低预警","婴儿车很危险...",MainActivity.class,4);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
