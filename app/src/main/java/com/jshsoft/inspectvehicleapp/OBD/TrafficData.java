package com.jshsoft.inspectvehicleapp.OBD;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class TrafficData
{

    /**
     * 车速 Km/h
     */
    private String speed;

    /**
     * @return 车速Km/h
     */
    public String getSpeed()
    {
        return speed;
    }

    /**
     * 发动机转速 Rpm
     */
    private String rotaSpeed;

    /**
     * @return 发动机转速 Rpm
     */
    public String getRotaSpeed()
    {
        return rotaSpeed;
    }

    /**
     * 冷却液温度 ℃
     */
    private String ect;

    /**
     * @return 冷却液温度℃
     */
    public String getEct()
    {
        return ect;
    }


//    /**
//     * 电池电压 V
//     */
//    private String batteryVoltage;
//
//    /**
//     * @return 电池电压V
//     */
//    public String getBatteryVoltage()
//    {
//        return batteryVoltage;
//    }



    /**
     * 进气温度 ℃
     */
    private String intakeTemperature;

    /**
     * @return 进气温度℃
     */
    public String getIntakeTemperature()
    {
        return intakeTemperature;
    }

    /**
     * 进气量 g/s
     */
    private String airVolumeIntake;

    /**
     * @return 进气量g/s
     */
    public String getAirVolumeIntake()
    {
        return airVolumeIntake;
    }

    /**
     * 进气岐管绝对压力 KPa
     */
    private String intakeManifold;

    /**
     * @return 进气岐管绝对压力KPa
     */
    public String getIntakeManifold()
    {
        return intakeManifold;
    }

    /**
     * 进气门绝对位置%
     */
    private String throttlePosition;


    /**
     * @return 进气门绝对位置%
     */
    public String getThrottlePosition()
    {
        return throttlePosition;
    }

    /**
     * 气缸点火提前角°
     */
    private String cylinderIgnitionAdvanceAngle;

    /**
     * @return 气缸点火提前角°
     */
    public String getCylinderIgnitionAdvanceAngle()
    {
        return cylinderIgnitionAdvanceAngle;
    }


    /**
     * 发动机负荷%
     */
    private String engineLoad;

    /**
     * @return 发动机负荷%
     */
    public String getEngineLoad()
    {
        return engineLoad;
    }

    /**
     * 发动机机油温度℃
     */
    private String oilTemperature;

    /**
     * @return 发动机机油温度℃
     */
    public String getOilTemperature()
    {
        return oilTemperature;
    }

    /**
     * 带故障运行时间Minutes
     */
    private String MTBF;

    /**
     * @return 带故障运行时间Minutes
     */
    public String getMTBF()
    {
        return MTBF;
    }


    /**
     * 清除故障码运行的里程Km
     */
    private String clearMTBEMileage;

    /**
     * @return 清除故障码运行的里程Km
     */
    public String getClearMTBEMileage()
    {
        return clearMTBEMileage;
    }

    /**
     * 发动机运行时长Seconds
     */
    private String engineTickCount;

    /**
     * @return 发动机运行时长Seconds
     */
    public String getEngineTickCount()
    {
        return engineTickCount;
    }

    /**
     * 发动机耗油率L/H
     */
    private String engineFuelRate;

    /**
     * @return 发动机耗油率L/H
     */
    public String getEngineFuelRate()
    {
        return engineFuelRate;
    }

    /**
     * 燃油绝对压力 kPa
     */
    private String fuelAbsPressure;

    /**
     * @return 燃油绝对压力kPa
     */
    public String getFuelAbsPressure()
    {
        return fuelAbsPressure;
    }

//    /**
//     * 瞬时蚝油L/KM
//     */
//    private String instantFuel;
//
//    /**
//     * @return 瞬时蚝油L/KM
//     */
//    public String getInstantFuel()
//    {
//        return instantFuel;
//    }

//    /**
//     * 发动机总耗油量（启动到现在）L
//     */
//    private String totalBurnoff;
//
//    /**
//     * @return 发动机总耗油量（启动到现在）L
//     */
//    public String getTotalBurnoff()
//    {
//        return totalBurnoff;
//    }

    /**
     * 剩余油量%
     */
    private String remainingFuel;

    /**
     * @return 剩余油量%
     */
    public String getRemainingFuel()
    {
        return remainingFuel;
    }

//    /**
//     * 总里程Km
//     */
//    private String TOTALODO;
//
//    /**
//     * @return 总里程Km
//     */
//    public String getTOTALODO()
//    {
//        return TOTALODO;
//    }
//
//    /**
//     * 本次行驶里程Km
//     */
//    private String TRIPKM;
//
//    /**
//     * @return 本次行驶里程Km
//     */
//    public String getTRIPKM()
//    {
//        return TRIPKM;
//    }
//
//    /**
//     * 故障码数量
//     */
//    private String faultCodeCount;
//
//    /**
//     * @return 故障码数量
//     */
//    public String getFaultCodeCount()
//    {
//        return faultCodeCount;
//    }

    //$OBD+RTD=7,1003,12.1,68,30,11.32,113,25.9,28,27.5,50,150,550,521,3,2580,12.0,55, 33.7,12347,2126,2


    private static String TAG = "TrafficData";

    /**
     * @param data obd返回的数据流
     * @return 行车数据
     */
    public static TrafficData StringFormat(String data)
    {
        Log.d(TAG, "StringFormat: " +data);
        if (null == data || data.trim().length() == 0)
        {
            return null;
        }
        if (data.startsWith("$OBD+RTD?="))
        {
            data = data.replace("$OBD+RTD?=", "");
        }
        if (data.startsWith("$AT+RTD?="))
        {
            data = data.replace("$AT+RTD?=", "");
        }
        data = data.replace("$","");
        String[] datas = data.split(",");

        if (datas.length != 16)
        {
            return null;
        }
        //$6,68,66,-39,13.12,4,1.6,-60,3.1,-40,0,0,7967,0.0,0,4.7
        Log.d(TAG, "StringFormat datas.length: " + datas.length);

        TrafficData td = new TrafficData();
        td.speed = datas[0];
        td.rotaSpeed = datas[1];
        //td.batteryVoltage = datas[2];
        td.ect = datas[2];
        td.intakeTemperature = datas[3];
        td.airVolumeIntake = datas[4];
        td.intakeManifold = datas[5];
        td.throttlePosition = datas[6];
        td.cylinderIgnitionAdvanceAngle = datas[7];
        td.engineLoad = datas[8];
        td.oilTemperature = datas[9];
        td.MTBF = datas[10];
        td.clearMTBEMileage = datas[11];
        td.engineTickCount = datas[12];
        td.engineFuelRate = datas[13];
        td.fuelAbsPressure = datas[14];
//        td.instantFuel = datas[15];
//        td.totalBurnoff = datas[16];
        td.remainingFuel = datas[15];
//        td.TOTALODO = datas[19];
//        td.TRIPKM = datas[20];
//        td.faultCodeCount = datas[21];
        //td.speed
        return td;
    }

    public static List<TrafficDataModel> createMdeol(TrafficData data) throws IllegalAccessException
    {
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field f : fields)
        {
            Log.d(TAG, "createMdeol: " + f.getName() + " : " + f.get(data));
        }
        return null;
    }

}