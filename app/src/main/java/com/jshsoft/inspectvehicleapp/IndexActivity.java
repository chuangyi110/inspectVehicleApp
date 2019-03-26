package com.jshsoft.inspectvehicleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jshsoft.inspectvehicleapp.util.LogUtil;
import com.jshsoft.inspectvehicleapp.util.SharedPreferencesUtils;

/**
 * 主页
 * 点击分项时自动传入当前Search的内容，无输入时传入Null,交由分项自行处理
 */
public class IndexActivity extends BaseActivity implements View.OnClickListener{
    private Button violationButton;
    private Button historicalButton;
    private Button signButton;
    private Button obdButton;
    private Button vehicleDataButton;
    private Button backReflectionButton;
    private EditText search;
    private String plateName;
    private String username;
    public void setTAG(){
        super.setTAG("IndexActivity");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_activity);
        initViews();
        setupEvents();
        checkNetWork();
    }
    private void initViews() {
        setTAG();
        violationButton = (Button)findViewById(R.id.violation_button);
        historicalButton  = (Button)findViewById(R.id.historical_button);
        signButton = (Button)findViewById(R.id.sign_button);
        obdButton = (Button)findViewById(R.id.obd_button);
        vehicleDataButton = (Button)findViewById(R.id.vehicle_data_button);
        backReflectionButton = (Button)findViewById(R.id.back_reflection_button);
        mTv = (TextView) findViewById(R.id.warning);
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
}
