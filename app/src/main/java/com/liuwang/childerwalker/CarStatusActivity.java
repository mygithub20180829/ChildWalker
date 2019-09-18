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

public class CarStatusActivity extends BaseActivity {
    private TextView tv_rollover,tv_hanging,car_hanging,car_rollover,car_airbag,tv_airbag;
    private TimerTask mTimerTask;
    private String topple,fall,text,incar,fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_status);

        initNavBar(true,"车辆状态");

        startTimer(1000,1000,mHandeler);

        tv_rollover=findViewById(R.id.tv_rollover);
        car_rollover=findViewById(R.id.car_rollover);
        tv_hanging=findViewById(R.id.tv_hanging);
        car_hanging=findViewById(R.id.car_hanging);
        car_airbag=findViewById(R.id.car_airbag);
        tv_airbag=findViewById(R.id.tv_airbag);
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
        Intent intent=new Intent(CarStatusActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(CarStatusActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
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
//                    Toast.makeText(CarStatusActivity.this,"http",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    text=s;
                    JWG(text);
                    Toast.makeText(CarStatusActivity.this,"https",Toast.LENGTH_LONG).show();
                    break;
                default :
                    break;
            }
        }

    }

    private void getDateByOkhttpUtils(){
        String url ="https://www.zhoujie.fun/carstatus/";

        OkHttpUtils.get().url(url)
                .id(100)
                .build()
                .execute(new MyStringCallBack());
    }

    private void JWG(String json) {
        try {
            JSONObject jsonObject=new JSONObject(json);

            if (jsonObject.getString("topple").equals("1")){
                tv_rollover.setText("婴儿车侧翻");
                tv_rollover.setTextColor(Color.RED);
                car_rollover.setTextColor(Color.RED);
            }else {
                tv_rollover.setText("无侧翻危险");
                tv_rollover.setTextColor(Color.BLACK);
                car_rollover.setTextColor(Color.BLACK);
            }

            if (jsonObject.getString("fall").equals("1")){
                tv_hanging.setText("跌落危险");//高低跌落
                tv_hanging.setTextColor(Color.RED);
                car_hanging.setTextColor(Color.RED);
            }else {
                tv_hanging.setText("无跌落危险");//高低跌落
                tv_hanging.setTextColor(Color.BLACK);
                car_hanging.setTextColor(Color.BLACK);
            }


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
