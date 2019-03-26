package com.jshsoft.inspectvehicleapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jshsoft.inspectvehicleapp.MainActivity;
import com.jshsoft.inspectvehicleapp.util.SharedPreferencesUtils;

public class MyReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferencesUtils helper= new SharedPreferencesUtils(context,"setting");
            int stateNum = helper.getInt("state");
            if(stateNum==10){
                context.startActivity(i);
            }

        }
    }
}