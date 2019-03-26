package com.jshsoft.inspectvehicleapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jshsoft.inspectvehicleapp.receiver.HomeWatcherReceiver;



import com.jshsoft.inspectvehicleapp.widget.LoadingDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BaseActivity extends Activity{
    public String TAG = "BaseActivity";
    private static final int NETWORKDISCONNECTED = 1;
    private static final int NETWORKCONNECTED = 2;
    LoadingDialog mLoadingDialog;
    public TextView mTv;
    public Button mLoginBtn;

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        registerHomeKeyReceiver(this);
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        unregisterHomeKeyReceiver(this);
        super.onPause();
        handler.removeCallbacks(runnable);

    }


    /**
     * 页面销毁前回调方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoadingDialog!=null){
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
    }

    //自定义的广播接收者
    private HomeWatcherReceiver mHomeKeyReceiver = null;

    //注册广播接收者，监听Home键
    void registerHomeKeyReceiver(Context context) {
        Log.i(TAG, "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);


    }
    //取消监听广播接收者
    void unregisterHomeKeyReceiver(Context context) {
        Log.i(TAG, "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }

    }
    //消息框
    void showToast(Context context,final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
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
    boolean isHideInput(View v, MotionEvent ev) {
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
    void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 显示加载的进度条
     */
    void showLoading() {
        if(mLoadingDialog ==null){
            mLoadingDialog = new LoadingDialog(this,getString(R.string.loading),false);
        }
        mLoadingDialog.show();
    }

    /**
     * 隐藏加载中的进度条
     */
    void hideLoding() {
        if(mLoadingDialog!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });
        }
    }

    /*
     * 简单的发送通知
     */
    private void showNotification() {
        String channelId = "channel_chat";
        Log.i(TAG,"this.is.channel");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_see_pass)
                .setContentTitle("检车宝")
                .setContentText("程序后台运行")
                .build();
        int i = 1;
        notificationManager.notify(i++, notification);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORKDISCONNECTED:
                    mTv.setVisibility(View.VISIBLE);
                    break;

                case NETWORKCONNECTED:
                    mTv.setVisibility(View.INVISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkNetWork();
            //每隔十秒钟检查一下网络
            handler.postDelayed(runnable , 10000);
        }
    };
    public void checkNetWork(){
        OkHttpClient client = new OkHttpClient();
        Request request = new  Request.Builder().url("http://www.baidu.com").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG , "网络访问失败!");
                handler.sendEmptyMessage(NETWORKDISCONNECTED);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG , "网络访问成功!");
                handler.sendEmptyMessage(NETWORKCONNECTED);
            }
        });
    }
    public void setLoginBtnClickable(boolean clickable){
        mLoginBtn.setClickable(clickable);
    }
}
