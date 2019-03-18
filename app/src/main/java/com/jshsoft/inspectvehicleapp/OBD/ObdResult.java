package com.jshsoft.inspectvehicleapp.OBD;

/**
 * Created by Administrator on 2016/8/25.
 */
public class ObdResult {
    /**
     * Vin号
     */
    private String vin;

    public String getVin()
    {
        return vin;
    }

    public void setVin(String vin)
    {
        this.vin = vin;
    }

    /**
     * 进气岐管绝对压力
     */
    private String intakeABSPressure;

    public String getIntakeABSPressure()
    {
        return intakeABSPressure;
    }

    public void setIntakeABSPressure(String intakeABSPressure)
    {
        this.intakeABSPressure = intakeABSPressure;
    }

    /**
     * 节气门位置
     */
    private String throttlePosition;

    public String getThrottlePosition()
    {
        return throttlePosition;
    }

    public void setThrottlePosition(String throttlePosition)
    {
        this.throttlePosition = throttlePosition;
    }


    /**
     * 点火提前角
     */
    private String timingAdvance;

    public String getTimingAdvance()
    {
        return timingAdvance;
    }

    public void setTimingAdvance(String timingAdvance)
    {
        this.timingAdvance = timingAdvance;
    }


    /**
     * 发动机负荷
     */
    private String engineLoadValue;

    public String getEngineLoadValue()
    {
        return engineLoadValue;
    }

    public void setEngineLoadValue(String engineLoadValue)
    {
        this.engineLoadValue = engineLoadValue;
    }


    /**
     * 剩余油量
     */
    private String fuelLevelInput;

    public String getFuelLevelInput()
    {
        return fuelLevelInput;
    }

    public void setFuelLevelInput(String fuelLevelInput)
    {
        this.fuelLevelInput = fuelLevelInput;
    }

    /**
     * 车速
     */
    private String speed;

    public String getSpeed()
    {
        return speed;
    }

    public void setSpeed(String speed)
    {
        this.speed = speed;
    }


    /**
     * 转速
     */
    private String engineRPM;

    public String getEngineRPM()
    {
        return engineRPM;
    }

    public void setEngineRPM(String engineRPM)
    {
        this.engineRPM = engineRPM;
    }


    /**
     * 冷却液温度
     */
    private String engineCoolantTemperature;

    public String getEngineCoolantTemperature()
    {
        return engineCoolantTemperature;
    }

    public void setEngineCoolantTemperature(String engineCoolantTemperature)
    {
        this.engineCoolantTemperature = engineCoolantTemperature;
    }

    /**
     * 进气温度
     */
    private String intakeAirTemperature;

    public String getIntakeAirTemperature()
    {
        return intakeAirTemperature;
    }

    public void setIntakeAirTemperature(String intakeAirTemperature)
    {
        this.intakeAirTemperature = intakeAirTemperature;
    }

    /**
     * 进气流量
     */
    private String massAirflowRate;

    public String getMassAirflowRate()
    {
        return massAirflowRate;
    }

    public void setMassAirflowRate(String massAirflowRate)
    {
        this.massAirflowRate = massAirflowRate;
    }
}
