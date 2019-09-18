package com.liuwang.childerwalker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ExternalEnvironmentActivity extends BaseActivity {
    private TimerTask mTimerTask;
    private String text,air,fire,incar,topple,fall;
    private TextView tv_air_quality,tv_hum,tv_temp,tv_fire,tv_air_quality_main;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_environment);

        initNavBar(true,"周边环境检测");

        startTimer(1000,1000,mHandeler);

        tv_air_quality=findViewById(R.id.tv_air_quality);
        tv_air_quality_main=findViewById(R.id.tv_air_quality_main);
        tv_hum=findViewById(R.id.tv_hum);
        tv_temp=findViewById(R.id.tv_temp);
        tv_fire=findViewById(R.id.tv_fire);


//        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        Intent intent=new Intent(ExternalEnvironmentActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(ExternalEnvironmentActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
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
//                    Toast.makeText(ExternalEnvironmentActivity.this,"http",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    text=s;
                    JWG(text);
                    Toast.makeText(ExternalEnvironmentActivity.this,"https",Toast.LENGTH_LONG).show();
                    break;
                default :
                    break;
            }
        }

    }

    private void getDateByOkhttpUtils(){
        String url ="https://www.zhoujie.fun/outenvironment/";

        OkHttpUtils.get().url(url)
                .id(100)
                .build()
                .execute(new MyStringCallBack());
    }

    private void JWG(String json) {
        try {
            JSONObject jsonObject=new JSONObject(json);
            data=new Data();
            data.setAir(jsonObject.getString("air"));

            incar=jsonObject.getString("incar");
            fire=jsonObject.getString("fire");
            topple=jsonObject.getString("topple");
            fall=jsonObject.getString("fall");

//            tv_air_quality.setText(jsonObject.getString("air"));
            tv_temp.setText(String.valueOf(jsonObject.getInt("outtemperature")));
            tv_hum.setText(String.valueOf(jsonObject.getString("outhumidity")));
            tv_fire.setText(jsonObject.getString("fire"));

            if (String.valueOf(jsonObject.getString("air")).equals("1")){
                tv_air_quality.setText("室外空气质量差");
                tv_air_quality_main.setTextColor(Color.RED);
                tv_air_quality.setTextColor(Color.RED);
            }else if (String.valueOf(jsonObject.getString("air")).equals("0")){
                tv_air_quality.setText("室外空气质量优良");
                tv_air_quality.setTextColor(Color.BLACK);
                tv_air_quality_main.setTextColor(Color.BLACK);
            }

            if (incar.equals("0")){
                NotificationInit("是否在车","婴儿不在车内...",MainActivity.class,5);
            }
            if (fire.equals("1")){
                tv_fire.setText("着火");
                NotificationInit("火势预警","周边可能有火灾发生...",MainActivity.class,6);
            }else if (fire.equals("0")){
                tv_fire.setText("暂无着火");
            }
            if (topple.equals("1")){//倾斜
                NotificationInit("倾斜预警","婴儿车有侧翻的危险...",MainActivity.class,7);
            }
            if (fall.equals("1")){//倾斜
                NotificationInit("高低预警","婴儿车很危险...",MainActivity.class,8);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Data{
        public String air,fire,outtemperature,outhumidity;

        public String getAir() {
            return air;
        }

        public void setAir(String air) {
            this.air = air;
        }

        public String getFire() {
            return fire;
        }

        public void setFire(String fire) {
            this.fire = fire;
        }

        public String getOuttemperature() {
            return outtemperature;
        }

        public void setOuttemperature(String outtemperature) {
            this.outtemperature = outtemperature;
        }

        public String getOuthumidity() {
            return outhumidity;
        }

        public void setOuthumidity(String outhumidity) {
            this.outhumidity = outhumidity;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "air='" + air + '\'' +
                    ", fire='" + fire + '\'' +
                    ", outtemperature='" + outtemperature + '\'' +
                    ", outhumidity='" + outhumidity + '\'' +
                    '}';
        }
    }
}
