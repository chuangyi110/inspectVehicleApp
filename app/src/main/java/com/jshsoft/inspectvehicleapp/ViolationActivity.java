package com.jshsoft.inspectvehicleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jshsoft.inspectvehicleapp.moel.ViolationInformationEntity;
import com.jshsoft.inspectvehicleapp.widget.LoadingDialog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 违章记录查询
 */
public class ViolationActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "ViolationActivity";
    private Button search_button;
    private EditText search;
    private LoadingDialog mLoadingDialog;
    private String plateNumber;
    private Handler handler=null;
    private String data;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.violation_activity);
        initViews();
        setupEvents();
        initData();
        //创建属于主线程的handler
        handler=new Handler();
        if(plateNumber!=null&&!plateNumber.trim().isEmpty()){
            getData();
        }

    }
    private void initViews() {
        search = (EditText)findViewById(R.id.et_search);
        search_button = (Button)findViewById(R.id.search_button);
    }
    private void setupEvents() {
        search_button.setOnClickListener(this);
    }


    private void initData(){
        Intent intent = getIntent();
        plateNumber = (intent.getStringExtra("plateNumber")).toString();
        System.out.println(plateNumber.trim().isEmpty());
        boolean b  =plateNumber!=null&&!plateNumber.trim().isEmpty();
        if(b){
            search.setText(plateNumber);
        }else{
            showToast("请输入要查询车牌号");
        }
    }
    private void getData(){
        if(plateNumber.isEmpty()){
            showToast("请输入要查询车牌号");
            return;
        }
        showLoading();//显示加载框
        Thread getVilationDataRunable = new Thread(){
            @Override
            public void run() {
                super.run();
                setSearchBtnClickable(false);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("plateNumber", plateNumber)
                        .build();
                Request request = new Request.Builder()
                        .url("https://vehicle.jshsoft.com:8080/wfjl")
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                        }
                        String req = response.body().string();
                        Log.d(TAG, "onResponse: " + req);
                        try{
                            Map map = (Map) JSONObject.parse(req);
                            if(Integer.parseInt(map.get("code").toString())==0){
                                System.out.println(map.get("wfjl").toString());
                                data = map.get("wfjl").toString();
                                handler.post(runnableUi);
                            }else {
                                setSearchBtnClickable(true);
                                hideLoding();
                                showToast(map.get("msg").toString());

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });


            }
        };
        getVilationDataRunable.start();
    }
    private String getPlateNumber(){
        String plateNumberSearch = search.getText().toString().trim();
        if(plateNumberSearch!="请输入要查询车牌号"&&plateNumberSearch!=null&&!plateNumberSearch.trim().isEmpty()) {
            plateNumber = plateNumberSearch;
        }else{
            plateNumberSearch = null;
        }
        return plateNumberSearch;
    }
    private void showLoading() {
        if(mLoadingDialog ==null){
            mLoadingDialog = new LoadingDialog(this,getString(R.string.loading),false);
        }
        mLoadingDialog.show();
    }
    private void hideLoding() {
        if(mLoadingDialog!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });
        }
    }
    /**
     * 登陆按钮锁定
     */
    public void setSearchBtnClickable(boolean clickable){
        search_button.setClickable(clickable);
    }
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ViolationActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 构建Runnable对象，在runnable中更新界面
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            final LayoutInflater inflater = LayoutInflater.from(ViolationActivity.this);
            // 获取需要被添加控件的布局
            final LinearLayout lin = (LinearLayout) findViewById(R.id.mainLinearLayout);
            // 获取需要添加的布局
            List<ViolationInformationEntity> list = JSON.parseArray(data,ViolationInformationEntity.class);

            for(ViolationInformationEntity v :list){
                LinearLayout layout = (LinearLayout) inflater.inflate(
                        R.layout.violation_view, null);

                TextView wzmc = layout.findViewById(R.id.wzmc);
                TextView wzwz = layout.findViewById(R.id.wzwz);
                TextView wzsj = layout.findViewById(R.id.wzsj);
                TextView wzcf = layout.findViewById(R.id.wzcf);
                lin.addView(layout);
                //wzmc.setId(i);
                wzmc.setText(v.getViolation());
                //wzwz.setId(i);
                wzwz.setText(v.getViolationSite());
                //wzsj.setId(i);
                String text = Integer.parseInt(v.getType())==0?"已处理":"未处理";
                wzsj.setText("处理情况"+text);
                //wzcf.setId(i);
                wzcf.setText("处罚"+v.getPoint()+"分"+v.getPenalty()+"元");
                //System.out.println(i);

            }
            setSearchBtnClickable(true);
            hideLoding();
        }

    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_button:
                getPlateNumber();
                getData();
                break;
        }
    }
    /**
     *  监听回退键
     */
    @Override
    public void onBackPressed() {
        if(mLoadingDialog!=null){
            if(mLoadingDialog.isShowing()){
                mLoadingDialog.cancel();
            }else {
                finish();
            }
        }else{
            finish();
        }
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
