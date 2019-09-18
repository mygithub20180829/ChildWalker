package com.liuwang.childerwalker;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class BaseActivity extends Activity {
    private ImageView mIvBack;
    private TextView mTvTitle;

    protected void initNavBar(boolean isShowBack,String title){
        mIvBack=findViewById(R.id.iv_back);
        mTvTitle=findViewById(R.id.tv_title);

        mIvBack.setVisibility(isShowBack?View.VISIBLE:View.GONE);
        mTvTitle.setText(title);

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();//回退操作
            }
        });
    }
}
