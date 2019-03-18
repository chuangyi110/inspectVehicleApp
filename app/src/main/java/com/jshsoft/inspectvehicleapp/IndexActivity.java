package com.jshsoft.inspectvehicleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jshsoft.inspectvehicleapp.util.LogUtil;
import com.jshsoft.inspectvehicleapp.util.SharedPreferencesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 * 点击分项时自动传入当前Search的内容，无输入时传入Null,交由分项自行处理
 */
public class IndexActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "IndexActivity";
    private Button violationButton;
    private Button historicalButton;
    private Button signButton;
    private Button obdButton;
    private Button vehicleDataButton;
    private Button backReflectionButton;
    private Button b7;
    private Button b8;
    private EditText search;
    private String plateName;
    private String username;

    private void initViews() {
        violationButton = (Button)findViewById(R.id.violation_button);
        historicalButton  = (Button)findViewById(R.id.historical_button);
        signButton = (Button)findViewById(R.id.sign_button);
        obdButton = (Button)findViewById(R.id.obd_button);
        vehicleDataButton = (Button)findViewById(R.id.vehicle_data_button);
        backReflectionButton = (Button)findViewById(R.id.back_reflection_button);
        //b7 = (Button) findViewById(R.id.b7);
        //b8 = (Button) findViewById(R.id.b8);
        search = (EditText)findViewById(R.id.et_search);
        Intent i = getIntent();
        username = i.getStringExtra("username");
    }
    private void setupEvents(){
        violationButton.setOnClickListener(this);
        historicalButton.setOnClickListener(this);
        signButton.setOnClickListener(this);
        obdButton.setOnClickListener(this);
        vehicleDataButton.setOnClickListener(this);
        backReflectionButton.setOnClickListener(this);
        //b7.setOnClickListener(this);
        //b8.setOnClickListener(this);
    }

    private String getSearch(){
        plateName =
                search.getText().toString().trim().equals("请输入要查询的车辆")?null:search.getText().toString().trim();

        return plateName;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        initViews();
        setupEvents();
    }

    private void startActivity(Context packageContext, Class<?> cls,String plateNumber){
        Intent intent = new Intent(packageContext,cls);
        intent.putExtra("plateNumber",plateNumber);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.violation_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转违章查询页面++++++++++++++++++");
                startActivity(IndexActivity.this, ViolationActivity.class,getSearch());
                break;
            case R.id.historical_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转历史记录查询页面++++++++++++++++++");
                startActivity(IndexActivity.this, HistoricalActivity.class,getSearch());
                break;
            case R.id.vehicle_data_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转违章查询页面++++++++++++++++++");
                startActivity(IndexActivity.this, VehicleDataActivity.class,getSearch());
                break;
            case R.id.sign_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转签名保存页面++++++++++++++++++");
                startActivity(IndexActivity.this,SignActivity.class,getSearch());
                break;
            case R.id.obd_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转OBD页面++++++++++++++++++");
                startActivity(IndexActivity.this, OBDActivity.class,getSearch());
                break;
            case R.id.back_reflection_button:
                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++跳转逆反射页面++++++++++++++++++");
                startActivity(IndexActivity.this, BackReflectionActivity.class,getSearch());
                break;
//            case R.id.b7:
//                break;
//            case R.id.b8:
//                break;
        }
    }
    public void toLogin(View view){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        helper.putValues(new SharedPreferencesUtils.ContextValue("autoLogin",false));
        startActivity(new Intent(this,MainActivity.class));
    }
    public void toLogin2(View view){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        helper.putValues(new SharedPreferencesUtils.ContextValue("autoLogin",false),
                new SharedPreferencesUtils.ContextValue("password",""),
                new SharedPreferencesUtils.ContextValue("remenberPassword",false));

        startActivity(new Intent(this,MainActivity.class));
    }
    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.init(this);
        LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++进入主页面++++++++++++++++++");

    }
    /**
     * 获取点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
                view.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏
     */
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
