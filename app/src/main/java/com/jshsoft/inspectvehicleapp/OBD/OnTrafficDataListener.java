package com.jshsoft.inspectvehicleapp.OBD;

/**
 * Created by Administrator on 2016/4/21.
 */
public interface OnTrafficDataListener
{
    void onStatusChange(String s);
    void onRTDData(TrafficData data);
//    void onExeData(String s);
//    void onCDRData(String s);
//    void onTDRData(String s);

    void onVin(String vin);
}
