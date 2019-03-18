package com.jshsoft.inspectvehicleapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    SharedPreferences sharedPreferences;

    public SharedPreferencesUtils(Context context,String fileName) {
        //第一个参数是文件的名称
        //第二个参数是存储的模式,一般都是使用私有方式：Context.MODE_PRIVATE;
        sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
    }
    /**
     * 存储数据
     * 只能存储简单的几种数据
     * 这里使用的是自定义的ContextValue类，来进行多个数据的处理
     */
    //创建一个内部类使用，里面有Key value这两个值
    public static class ContextValue{
        String key;
        Object value;
        //通过构造方法来传入key和value

        public ContextValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
    //一次可以传入多个ContextValue对象的值
    public void putValues(ContextValue... contextValues){
        //获取ShsrePreferences对象的编辑对象，才能进行数据的存储
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //数据分类存储
        for(ContextValue contextValue:contextValues){
            //如果是字符型
            if(contextValue.value instanceof String){
                editor.putString(contextValue.key,contextValue.value.toString()).commit();
            }
            //如果是int类型
            if(contextValue.value instanceof Integer){
                editor.putInt(contextValue.key,Integer.parseInt(contextValue.value.toString())).commit();
            }
            //如果是Long类型
            if(contextValue.value instanceof Long){
                editor.putLong(contextValue.key,Long.parseLong(contextValue.value.toString())).commit();
            }
            //如果是Boolean类型
            if(contextValue.value instanceof Boolean){
                editor.putBoolean(contextValue.key,Boolean.parseBoolean(contextValue.value.toString())).commit();
            }
        }
    }
    //获取数据的方法
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    public boolean getBoolean(String key,Boolean b){
        return sharedPreferences.getBoolean(key,b);
    }
    public int getInt(String key){
        return sharedPreferences.getInt(key,-1);
    }
    public long getLong(String key){
        return sharedPreferences.getLong(key,-1);
    }
    //清除当前文件的所有的数据
    public void clear(){
        sharedPreferences.edit().clear().commit();
    }


}
