package com.jshsoft.inspectvehicleapp.OBD;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2016/4/22.
 */
public interface OnBluetoothSearchListener {

    void onSearch(BluetoothDevice remoteBluetoothDevice);

    void onUnSearch();

    void onStatusChange(BlueStatus status, String note);
}
