package com.jshsoft.inspectvehicleapp.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jshsoft.inspectvehicleapp.R;

public class LoadingDialog extends ProgressDialog {
    private String mMessage;
    private TextView mTitleTv;

    public LoadingDialog(Context context, String message, boolean canceledOnTouchOutside) {
        super(context, R.style.Theme_Light_LoadingDialog);
        this.mMessage=message;
        setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        mTitleTv = (TextView)findViewById(R.id.tv_loading_dialog);
        mTitleTv.setText(mMessage);
        setCancelable(false);
    }
    public void setmTitle(String messgae){
        this.mMessage = messgae;
        mTitleTv.setText(mMessage);
    }
    public void showButtom(){
    }

}
