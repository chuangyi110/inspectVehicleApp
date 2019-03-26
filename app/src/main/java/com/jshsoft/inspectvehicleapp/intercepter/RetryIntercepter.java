package com.jshsoft.inspectvehicleapp.intercepter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryIntercepter implements Interceptor {
    public int maxRetry;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
    private Context context;
    private Activity activity;
    public RetryIntercepter(int maxRetry,Activity activity){
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.maxRetry = maxRetry;

    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("retryNum=" + retryNum);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context,"正在连接",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryNum++;
            System.out.println("retryNum=" + retryNum);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context,"正在进行第"+retryNum+"次重试",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
            response = chain.proceed(request);
        }
        return response;
    }
}
