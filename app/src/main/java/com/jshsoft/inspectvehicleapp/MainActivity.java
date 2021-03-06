package com.jshsoft.inspectvehicleapp;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jshsoft.inspectvehicleapp.intercepter.RetryIntercepter;
import com.jshsoft.inspectvehicleapp.moel.LoginForm;
import com.jshsoft.inspectvehicleapp.util.APKVersionCodeUtils;
import com.jshsoft.inspectvehicleapp.util.ASEUtil;
import com.jshsoft.inspectvehicleapp.util.AppManager;
import com.jshsoft.inspectvehicleapp.util.BaseApplication;
import com.jshsoft.inspectvehicleapp.util.LogUtil;
import com.jshsoft.inspectvehicleapp.util.SharedPreferencesUtils;
import com.jshsoft.inspectvehicleapp.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登陆页面
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "MainActivity";
    //布局内的控件
    private EditText et_name;
    private EditText et_password;
    private Button mLoginBtn;
    private CheckBox checkBox_password;
    private CheckBox checkBox_login;
    private ImageView iv_see_password;
    private LoadingDialog mLoadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupEvents();
        initData();
        //添加app进程控制
        AppManager.getAppManager().addActivity(this);
        //TODO 版本检查  服务器无法上传暂时注解
         checkVersions();
        //校验版本前无法点击登录按钮
        setLoginBtnClickable(false);
    }

    private void checkVersions() {
        Thread checkVersionsRunable = new Thread(){
            @Override
            public void run() {
                super.run();
                String localVersion = APKVersionCodeUtils.getVersionCode(MainActivity.this) + "";
                LogUtil.i(TAG,"当前版本："+localVersion);
                if(localVersion.isEmpty()){
                    LogUtil.i(TAG,"版本校验失败请重试");
                    showToast("版本校验失败请重试");
                    return;
                }
                OkHttpClient okHttpClient = new OkHttpClient
                        .Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20,TimeUnit.SECONDS)
                        .addInterceptor(new RetryIntercepter(2))
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("localVersion", localVersion)
                        .build();
                Request request = new Request.Builder()
                        .url("https://vehicle.jshsoft.com:8080/localVersion")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e instanceof ConnectException){
                            showToast("网络异常！请确认网络情况");
                        }else{
                            showToast(e.getMessage());
                        }
                        LogUtil.i(TAG,"++++++++++++++++++版本验证失败:错误原因"+e.getMessage()+"++++++++++++++++++");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String req = response.body().string();
                        Map map = (Map)JSONObject.parse(req);
                        String code = map.get("code")==null?"1":map.get("code").toString();
                        System.out.println("###################"+code);
                        if(Integer.parseInt(code)==0){
                            setLoginBtnClickable(true);
                            LogUtil.i(TAG,"++++++++++++++++++版本验证成功++++++++++++++++++");
                        }else {
                            String msg = map.get("msg")==null?"连接失败":map.get("msg").toString();
                            showToast(msg);
                            setLoginBtnClickable(false);
                            LogUtil.i(TAG,"+++++++++++++++版本验证失败__失败原因:"+msg+"+++++++++++++");


                        }

                    }
                });

            }
        };
        checkVersionsRunable.start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.init(this);
        LogUtil.i(TAG,"++++++++++++++++++开启软件++++++++++++++++++");
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {

        mLoginBtn = (Button)findViewById(R.id.btn_login);
        et_name  = (EditText)findViewById(R.id.et_account);
        et_password = (EditText)findViewById(R.id.et_password);
        checkBox_login = (CheckBox)findViewById(R.id.checkBox_login);
        checkBox_password = (CheckBox)findViewById(R.id.checkBox_password);
        iv_see_password = (ImageView)findViewById(R.id.iv_see_password);
        //创建通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_chat";
            String channelName = "新消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }
        //添加试错次数
        SharedPreferencesUtils helper =new SharedPreferencesUtils(this,"setting");
        helper.putValues(new SharedPreferencesUtils.ContextValue("loginTime",0));
    }
    /**
     * 添加监听事件
     */
    private void setupEvents() {
        mLoginBtn.setOnClickListener(this);
        checkBox_password.setOnCheckedChangeListener(this);
        checkBox_login.setOnCheckedChangeListener(this);
        iv_see_password.setOnClickListener(this);
    }

    /**
     * 初始执行
     */
    private void initData(){
        //判断用户第一次登陆
        if(firstLogin()){
            //取消记住密码和自动登陆复选框
            checkBox_login.setChecked(false);
            checkBox_password.setChecked(false);
        }
        //判断是否记住密码
        if(remenberPassword()){
            checkBox_password.setChecked(true);
            setTextNameAndPassword();
        }else{
            setTextName();
        }
        //判断是否自动登陆
        if(autoLogin()){
            checkBox_login.setChecked(true);
            login();
        }
    }

    /**
     * 登陆
     */
    private void login() {
        //判断账号密码是否为空
        if(getAccount().isEmpty()){
            showToast("你输入的账号为空");
            return;
        }
        if(getPassword().isEmpty()){
            showToast("您输入的密码为空");
            SharedPreferencesUtils helper = new SharedPreferencesUtils(MainActivity.this,"setting");
            int i = helper.getInt("loginTime");
            if(i<=10){
                i++;
                helper.putValues(new SharedPreferencesUtils.ContextValue("loginTime",i));
                showToast("登录错误"+i+"次");
            }else {
                setLoginBtnClickable(false);
                showToast("登录异常锁定账号");
                lockUser();
            }


            return;
        }
        //登陆一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        Thread loginRunable = new Thread(){
            @Override
            public void run() {

                super.run();
                setLoginBtnClickable(false);//点击登陆按钮后，设置登陆按钮不可点击
                //网络连接
                String username = getAccount();
                String pwd = getPassword();
                String captcha = "jjj";
                String uuid ="ttt";
                LoginForm lf = new LoginForm();
                lf.setCaptcha(captcha);
                lf.setPassword(pwd);
                lf.setUsername(username);
                lf.setUuid(uuid);
                String json = JSON.toJSONString(lf);
                String timestamp = Long.toString(System.currentTimeMillis());
                String url = "login";
                String transport =new String(Base64.encode(json.getBytes(),Base64.DEFAULT));
                LogUtil.i(TAG,"timestamp="+timestamp+",transport="+transport);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("transport", transport)
                        .add("url", url)
                        .add("timestamp", timestamp)
                        .build();
                Request request = new Request.Builder()
                        .url("https://vehicle.jshsoft.com:8080/login")
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e instanceof ConnectException){
                            showToast("网络异常！请确认网络情况");
                        }else{
                            showToast(e.getMessage());
                        }
                        LogUtil.i(TAG,"++++++++++++++++++查询失败:错误原因"+e.getMessage()+"++++++++++++++++++");

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String req = response.body().string();
                        try{
                            Map map = (Map)JSONObject.parse(req);
                            if(Integer.parseInt(map.get("code").toString())==0){
                                LogUtil.i(TAG,"[账号:"+username+"]"+"++++++++++++++++++验证成功++++++++++++++++++");
                                startActivity(new Intent(MainActivity.this, IndexActivity.class).putExtra("username",username));
                                finish();//关闭页面
                            }else {
                                String msg = map.get("msg").toString();
                                LogUtil.i(TAG,"[账号:"+username+"]+++++++++++++++验证失败__失败原因:"+msg+"+++++++++++++");
                                SharedPreferencesUtils helper = new SharedPreferencesUtils(MainActivity.this,"setting");
                                int i = helper.getInt("loginTime");
                                i++;
                                if(i<=10){
                                    helper.putValues(new SharedPreferencesUtils.ContextValue("loginTime",i));
                                    showToast("登录错误"+i+"次");
                                }else {
                                    setLoginBtnClickable(false);
                                    showToast("登录异常锁定账号");
                                    lockUser();
                                }
                            }
                        }catch (Exception e){

                        }
                    }
                });
                setLoginBtnClickable(true);//释放登陆按钮锁定状态
                hideLoding();//隐藏加载框
            }
        };
        loginRunable.start();
    }

    private void loadCheckBoxState() {
        loadCheckBoxState(checkBox_login,checkBox_password);
    }

    private void loadCheckBoxState(CheckBox checkBox_login, CheckBox checkBox_password) {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        if(checkBox_login.isChecked()){
            //创建记住密码和自动登陆都是选择，保存密码数据
            helper.putValues(
                    new SharedPreferencesUtils.ContextValue("remenberPassword",true),
                    new SharedPreferencesUtils.ContextValue("autoLogin",true),
                    new SharedPreferencesUtils.ContextValue("password",getPassword()));
        }else if(!checkBox_password.isChecked()){
            helper.putValues(
                    new SharedPreferencesUtils.ContextValue("remenberPassword",true),
                    new SharedPreferencesUtils.ContextValue("autoLogin",false),
                    new SharedPreferencesUtils.ContextValue("password",""));
        }else if(checkBox_password.isChecked()){
            helper.putValues(new SharedPreferencesUtils.ContextValue("remenberPassword",true),
                    new SharedPreferencesUtils.ContextValue("autoLogin",false),
                    new SharedPreferencesUtils.ContextValue("password",getPassword()));
        }
    }


    /**
     * 登陆按钮锁定
     */
    public void setLoginBtnClickable(boolean clickable){
        mLoginBtn.setClickable(clickable);
    }

    /**
     * 显示加载的进度条
     */
    private void showLoading() {
        if(mLoadingDialog ==null){
            mLoadingDialog = new LoadingDialog(this,getString(R.string.loading),false);
        }
        mLoadingDialog.show();
    }

    /**
     * 吟唱加载中的进度条
     */
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
     * 获取账号
     * @return
     */
    private String getAccount() {
        return et_name.getText().toString().trim();
    }
    /**
     * 获取密码
     */
    private String getPassword(){
        return et_password.getText().toString().trim();
    }

    /**
     * 判断是否自动登陆
     * @return
     */
    private boolean autoLogin() {
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        boolean autoLogin = helper.getBoolean("autoLogin",false);
        return autoLogin;
    }

    //设置数据到账号输入框
    private void setTextName() {
        et_name.setText(""+getLocalName());
    }

    //设置账号密码到输入框内
    private void setTextNameAndPassword() {
        et_name.setText(""+getLocalName());
        et_password.setText(""+getLocalPassword());
    }
    //获取保存在本地的密码
    private String getLocalPassword() {
        SharedPreferencesUtils helper= new SharedPreferencesUtils(this,"setting");
        String password = helper.getString("password");
        return password;
    }
    //获取保存在本地的用户名
    private String getLocalName() {
        SharedPreferencesUtils helper =new SharedPreferencesUtils(this,"setting");
        String name = helper.getString("name");
        return name;
    }

    /**
     * 判断是否第一次登陆
     */
    private boolean firstLogin(){
        SharedPreferencesUtils  helper = new SharedPreferencesUtils(this,"setting");
        boolean first = helper.getBoolean("first",true);
        if(first){
            helper.putValues(new SharedPreferencesUtils.ContextValue("first",false),
                    new SharedPreferencesUtils.ContextValue("remenberPassword",false),
                    new SharedPreferencesUtils.ContextValue("autoLogin",false),
                    new SharedPreferencesUtils.ContextValue("name",""),
                    new SharedPreferencesUtils.ContextValue("password",""));
            return true;
        }
        return false;
    }
    //判断是否记住密码
    private boolean remenberPassword() {
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        boolean remenberPassword = helper.getBoolean("remenberPassword",false);
        return remenberPassword;
    }
    //消息框
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:

                loadUserName();
                login();

                break;
            case R.id.iv_see_password:
                setPasswordVisibility();
                break;
        }

    }

    /**
     * 设置密码可见不可见的相互转换
     */
    private void setPasswordVisibility() {
        if(iv_see_password.isSelected()){
            iv_see_password.setSelected(false);
            et_password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else {
            iv_see_password.setSelected(true);
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    /**
     * 保存用户账号
     */
    private void loadUserName() {
        if(!getAccount().equals("")||!getAccount().equals("请输入登陆账号")){
            SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
            helper.putValues(new SharedPreferencesUtils.ContextValue("name",getAccount()));
        }
    }

    /**
     * CheckBox点击时的回调方法，不管是勾选还是取消勾选都会得到回调
     * @param compoundButton
     * @param b
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton ==checkBox_password){//记住密码选框发生改变时
            if(!b){//如果取消"记住密码"，那么同样取消自动登陆
                checkBox_login.setChecked(false);
            }
        }else if(compoundButton==checkBox_login){//自动登陆选框发生改变时
            if(b){//如果选择"自动登陆"，那么同样选中"记住密码"
                checkBox_password.setChecked(true);
            }
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
        //显示通知
        showNotification();
        //结束进程
        AppManager.getAppManager().AppExit(this);
    }

    /**
     * 页面销毁前回调方法
     */
    @Override
    protected void onDestroy() {
        if(mLoadingDialog!=null){
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }

        super.onDestroy();
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
    //锁定账号
    private void lockUser(){
        Thread lockUserRunable = new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("status", "1")
                        .build();
                Request request = new Request.Builder()
                        .url("https://vehicle.jshsoft.com:8080/user/lock")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e instanceof ConnectException){
                            showToast("网络异常！请确认网络情况");
                        }else{
                            showToast(e.getMessage());
                        }
                        LogUtil.i(TAG, "++++++++++++++++++用户锁定失败:错误原因" + e.getMessage() + "++++++++++++++++++");

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String req = response.body().string();
                        try {
                            Map map = (Map) JSONObject.parse(req);
                            if (Integer.parseInt(map.get("code").toString()) == 0) {
                                LogUtil.i(TAG, "++++++++++++++++++用户验证成功++++++++++++++++++");
                            } else {
                                String msg = map.get("msg").toString();
                                LogUtil.i(TAG, "+++++++++++++++用户锁定失败__失败原因:" + msg + "+++++++++++++");
                                showToast(msg);
                                AppManager.getAppManager().AppExit(MainActivity.this);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        lockUserRunable.start();
    }



}
