package com.jshsoft.inspectvehicleapp.OBD;

/**
 * Created by Administrator on 2016/4/25.
 */
public class TrafficDataModel
{

    private String name;

    private String units;

    public TrafficDataModel(String name,String units  )
    {
        this.name  = name;
        this.units = units;
        
    }

    public String getName()
    {
        return name;
    }

    public String getUnits()
    {
        return units;
    }

}
