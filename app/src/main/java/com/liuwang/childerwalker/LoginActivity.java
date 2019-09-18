package com.liuwang.childerwalker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login;
    private EditText user_login,psw_login;
    private String account,password,text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //隐藏标题栏
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        user_login=findViewById(R.id.user_login);
        psw_login=findViewById(R.id.psw_login);
        btn_login=findViewById(R.id.btn_login);



        Log.d("------", "onCreate: "+user_login);
        Log.d("------", "onCreate: "+psw_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account=user_login.getText().toString();
                password=psw_login.getText().toString();
                postDateByOkhttpUtils();
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
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
//                    Toast.makeText(LoginActivity.this,"http",Toast.LENGTH_LONG).show();
                    break;
                default :
                    break;
            }
        }

    }

    private void JWG(String json) {
        try {
            JSONObject jsonObject=new JSONObject(json);
            Log.d("-------", "onResponse: "+jsonObject.getInt("msg"));
            if (jsonObject.getInt("msg")==1){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }else if (jsonObject.getInt("msg")==0){
                Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LoginActivity.this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postDateByOkhttpUtils(){
        String url="https://www.zhoujie.fun/signin/";
        OkHttpUtils.post().url(url)
                .id(100)//请求的id
                .addParams("account",account)
                .addParams("password",password)
                .build()
                .execute(new MyStringCallBack());
    }


    public void onRegisterClick(View v){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    public void onForgetClick(View v){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
}
