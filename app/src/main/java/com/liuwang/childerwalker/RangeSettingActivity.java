package com.liuwang.childerwalker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class RangeSettingActivity extends BaseActivity {
    public WifiManager wifiManager;
    public ConnectivityManager connectManager;
    public NetworkInfo netInfo;
    public WifiInfo wifiInfo;
    public DhcpInfo dhcpInfo;

    List<ScanResult> list;

    private TextView ip_distance,ip_mask,ip_address;
    private String topple,fall,incar,fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range_setting);

        initNavBar(true,"车辆距离");
        getDateByOkhttpUtils();

        ip_distance=findViewById(R.id.ip_distance);

//        //隐藏标题栏
//        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Thread(){
            @Override
            public void run() {
                while(true){
                    SystemClock.sleep(1000);

                    wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    dhcpInfo = wifiManager.getDhcpInfo();
                    wifiInfo = wifiManager.getConnectionInfo();
                    list = (List<ScanResult>) wifiManager.getScanResults();

                    wifiInfo.getSSID();

                    final String wifiProperty = "当前连接WIFI信息如下:" + wifiInfo.getSSID() + '\n' +
                            "ip:" + FormatString(dhcpInfo.ipAddress) + '\n' +
                            "mask:" + FormatString(dhcpInfo.netmask) + '\n' +
                            "netgate:" + FormatString(dhcpInfo.gateway) + '\n' +
                            "dns:" + FormatString(dhcpInfo.dns1) + '\n' +
                            "rssi:"+ wifiInfo.getRssi() +'\n' +
                            "d:"+pow(10,(abs(wifiInfo.getRssi()-47)/(10*3.8)))+'\n'+
                            DisByRssi(wifiInfo.getRssi());

                    Log.d("Wifi Connect Message-------", "run: "+wifiProperty);



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double value=pow(10,(abs(wifiInfo.getRssi()-47)/(10*3.8)))/10000;
                            DecimalFormat df=new DecimalFormat("#.00");
                            String va=df.format(value);
                            ip_distance.setText(va);
                        }
                    });
                }

            }

            public Double DisByRssi(int rssi){

                int iRssi = abs(rssi);
                double power = (iRssi- 35)/(10*2.1);
                return pow(10,power);
            }

        }.start();

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
        Intent intent=new Intent(RangeSettingActivity.this, object.getClass());
        PendingIntent pi=PendingIntent.getActivity(RangeSettingActivity.this,0,intent,0);//PendingIntent.FLAG_UPDATE_CURRENT
        //设置通知栏点击跳转
        notification.setContentIntent(pi);
        //发送通知
        notificationManager.notify(id,notification.build());
    }

    public String FormatString(int value){
        String strValue ="";
        byte[] ary = intToByteArray(value);
        for(int i = ary.length-1;i>0;i--){
            strValue += (ary[i]& 0xFF);
            if(i>0){
                strValue +=".";

            }
        }
        return strValue;
    }

    public byte[] intToByteArray(int value){
        byte[] b = new byte[4];
        for(int i = 0;i<4;i++){
            int offset = (b.length-1-i)*8;
            b[i]= (byte)((value >>> offset) & 0xFF);
        }
        return b;
    }

    private void getDateByOkhttpUtils(){
        String url ="https://www.zhoujie.fun/babystatus/";

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
                    JWG(s);
//                    Toast.makeText(RangeSettingActivity.this,"http:上传成功",Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    Toast.makeText(RangeSettingActivity.this,"https:上传成功",Toast.LENGTH_LONG).show();
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
