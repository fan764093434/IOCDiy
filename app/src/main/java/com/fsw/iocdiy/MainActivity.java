package com.fsw.iocdiy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fsw.baselibrary.CheckNet;
import com.fsw.baselibrary.FindViewById;
import com.fsw.baselibrary.IOCUtils;
import com.fsw.baselibrary.OnClick;


public class MainActivity extends AppCompatActivity {

    @FindViewById(R.id.text_one)
    TextView testIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IOCUtils.bind(this);
        testIv.setText("我是自己的IOC完成的!");
    }

    @OnClick(R.id.text_one)
    @CheckNet("TMD,断网了!")
    public void onViewClicked(View view) {
        Log.e("fsw--", "点击");
    }

}
