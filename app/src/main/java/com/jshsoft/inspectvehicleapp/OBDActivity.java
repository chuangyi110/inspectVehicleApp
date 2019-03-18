package com.jshsoft.inspectvehicleapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jshsoft.inspectvehicleapp.OBD.BlueStatus;
import com.jshsoft.inspectvehicleapp.OBD.Bluetooth;
import com.jshsoft.inspectvehicleapp.OBD.ObdResult;
import com.jshsoft.inspectvehicleapp.OBD.OnBluetoothSearchListener;
import com.jshsoft.inspectvehicleapp.OBD.OnTrafficDataListener;
import com.jshsoft.inspectvehicleapp.OBD.TrafficData;
import com.jshsoft.inspectvehicleapp.OBD.TrafficDataModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class OBDActivity extends AppCompatActivity {

    private static String TAG = "OBDActivity";


    public static Map<String, TrafficDataModel> getModel() {
        Map<String, TrafficDataModel> map = new HashMap<>();

        map.put("speed", new TrafficDataModel("车速", "Km/h"));
        map.put("rotaSpeed", new TrafficDataModel("发动机转数", "Rpm"));
        map.put("batteryVoltage", new TrafficDataModel("电池电压", "V"));
        map.put("ect", new TrafficDataModel("冷却液温度", "℃"));
        map.put("intakeTemperature", new TrafficDataModel("进气温度", "℃"));
        map.put("airVolumeIntake", new TrafficDataModel("进气量", "g/s"));
        map.put("intakeManifold", new TrafficDataModel("进气岐管压力", "KPa"));
        map.put("throttlePosition", new TrafficDataModel("进气门位置", "%"));
        map.put("cylinderIgnitionAdvanceAngle", new TrafficDataModel("气缸点火提前角", "°"));
        map.put("engineLoad", new TrafficDataModel("发动机负荷计算值", "%"));
        map.put("oilTemperature", new TrafficDataModel("发动机机油温度", "℃"));
        map.put("MTBF", new TrafficDataModel("带故障运行时间", "分钟"));
        map.put("clearMTBEMileage", new TrafficDataModel("清除故障码运行的里程", "Km"));
        map.put("engineTickCount", new TrafficDataModel("发动机运行时长", "Seconds"));
        map.put("engineFuelRate", new TrafficDataModel("发动机耗油率", "L/H"));
        map.put("fuelAbsPressure", new TrafficDataModel("燃油绝对压力", "kPa"));
        map.put("instantFuel", new TrafficDataModel("瞬时蚝油", "L/KM"));
        map.put("totalBurnoff", new TrafficDataModel("本次耗油量", "L"));
        map.put("remainingFuel", new TrafficDataModel("剩余油量", "%"));
        map.put("TOTALODO", new TrafficDataModel("总里程", "Km"));
        map.put("TRIPKM", new TrafficDataModel("本次行驶里程", "Km"));
        map.put("faultCodeCount", new TrafficDataModel("故障码数量", "个"));

        return map;

    }

    private Map<String, TextView> viewMap = new HashMap<>();
    private TextView title;
    private TextView vin;
    private ImageView icTitle;
    private Bluetooth bluetooth;
    private Handler uiHandler;
    private ObdResult obdResult;
    private Button search;
    //private boolean IsToool = false;
    private String apiKey="com.jshsoft.vin";//45221346f74c4d5aa295d30818c7f220

    private boolean exported() {
        return (apiKey != null && !apiKey.equals("com.jshsoft.vin"));
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: PackageName" +getPackageName());
        setContentView(R.layout.obd_activity);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        obdResult = new ObdResult();
        //exported = intent.getBooleanExtra("exported",false);
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        setSupportActionBar(toolbar);
                        title = findViewById(R.id.title);
                        title.setClickable(false);
                        vin = findViewById(R.id.vin);
                        icTitle = findViewById(R.id.ictitle);
                        search = findViewById(R.id.search_button);
                        final TableLayout table = findViewById(R.id.table);
                        //final LinearLayout ttbale= (LinearLayout)findViewById(R.id.ttt);
                        Map<String, TrafficDataModel> models = getModel();
                        TrafficData data = TrafficData.StringFormat("$OBD+RTD?=0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
                        try {
                            int i = 0;
                            Field[] fields = data.getClass().getDeclaredFields();
                            for (Field fd : fields) {
                                fd.setAccessible(true);
                                Log.i(TAG, "createMdeol: "+fd.getName()+" : " +fd.get(data));
                                if (!models.containsKey(fd.getName())) {
                                    continue;
                                }
                                TableRow row2 = null;
                                if (i % 2 == 0) {
                                    row2 = (TableRow) getLayoutInflater().inflate(R.layout.apptablerow2, null);
                                } else {
                                    row2 = (TableRow) getLayoutInflater().inflate(R.layout.apptablerow, null);
                                }

                                TextView f2 = row2.findViewById(R.id.first);
                                TextView c2 = row2.findViewById(R.id.center);
                                TextView e2 = row2.findViewById(R.id.end);
                                viewMap.put(fd.getName(), c2);
                                //Log.i(TAG, "onCreate: "+fd.getName());
                                f2.setText(models.get(fd.getName() + "").getName());
                                c2.setText(fd.get(data) + "");
                                e2.setText(models.get(fd.getName() + "").getUnits());
                                table.addView(row2);
                                //ttbale.addView(row2);
                                i++;
                            }
                        } catch (IllegalAccessException ex) {
                            Log.i(TAG, "onCreate: " + ex.toString());
                            ex.printStackTrace();
                        }

                        bluetooth = new Bluetooth(OBDActivity.this, getResources().getString(R.string.uuid));
                        bluetooth.setOnBluetoothSearchListener(new OnBluetoothSearchListener() {
                            @Override
                            public void onSearch(final BluetoothDevice remoteBluetoothDevice) {
                                Log.i(TAG, "onSearch: " + remoteBluetoothDevice.getAddress());
                                if (remoteBluetoothDevice.getName() != null && remoteBluetoothDevice.getName().contains("OBD")) {
                                    final Map<String, Object> p = new HashMap<>();
                                    p.put("mac", remoteBluetoothDevice.getAddress());
                                    //p.put("token", TokenManager.getManager().getToken());
                                    Log.i(TAG, "onSearch: Mac ：" + remoteBluetoothDevice.getAddress());
                                    //这里验证OBD模块是不是自己的。
                                    //runOnUiThread(() -> vin.setText(remoteBluetoothDevice.getAddress()));
                                    new Thread(() -> {
                                        try {
                                            bluetooth.destroy();
                                            runOnUiThread(() -> title.setText("验证模块"));
//                                            String res = HttpClient.net( Address.address + "/Services/VerifyObd", p, "GET");
//                                            Log.i(TAG, "onSearch: " + res);
//                                            if (res.contains("\"Status\":true"))
//                                            {
                                                bluetooth.connect(remoteBluetoothDevice);
//                                            } else
//                                            {
//                                                runOnUiThread(() -> {
//                                                    title.setText("非法模块,点击重试");
//                                                    title.setClickable(true);
//                                                    title.setOnClickListener(v -> {
//                                                        bluetooth.searchDrivces();
//                                                        title.setClickable(false);
//                                                    });
//                                                    icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_connect_fail_24dp));
//                                                });
//                                            }

                                        } catch (Exception e1) {
                                            runOnUiThread(() -> {
                                                title.setText("模块验证失败,点击重试");
                                                title.setClickable(true);
                                                title.setOnClickListener(v -> {
                                                    bluetooth.searchDrivces();
                                                    title.setClickable(false);
                                                });
                                                //icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_connect_fail_24dp));
                                            });
                                            e1.printStackTrace();
                                        }
                                    }).start();

                                }
                            }

                            @Override
                            public void onUnSearch() {
                                Log.i(TAG, "onUnSearch: 未搜索到蓝牙设备");
                            }

                            @Override
                            public void onStatusChange(final BlueStatus status, final String note) {
                                runOnUiThread(() -> {
                                    if (title == null) {
                                        return;
                                    }
                                    search.setVisibility(View.GONE);
                                    switch (status) {
                                        case Connect:

                                            title.setText("已连接：" + note);
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_conncet_24dp));
                                            break;

                                        case Searching:

                                            title.setText("搜索中");
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_24dp));
                                            break;

                                        case Searched:
                                            Log.i(TAG, "Searched: note" + note);
                                            String str = "";
                                            if (note == null) {
                                                str = "非OBD设备";
                                            } else {
                                                str = note.contains("OBD") ? "" : "非OBD设备";
                                            }

                                            title.setText("搜索到" + str + ":" + note);
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_24dp));
                                            break;
                                        case Connecting:
                                            Log.i(TAG, "run: Connecting");
                                            title.setText("连接" + note + "中");
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_connecting_24dp));
                                            break;
                                        case ConnectFail:
                                            title.setText("连接失败，点击重试");
                                            title.setClickable(true);
                                            title.setOnClickListener(v -> {
                                                if (bluetooth != null)
                                                {
                                                    bluetooth.reTryConnect();
                                                }
                                                runOnUiThread(() -> title.setClickable(false));
                                            });
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_connect_fail_24dp));
                                            break;
                                        case UnSearch:
                                            title.setText("未搜到设备，点击重试");
                                            icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_unsearch_24dp));
                                            title.setClickable(true);
                                            title.setOnClickListener(v -> runOnUiThread(() -> {
                                                title.setClickable(false);
                                                if (bluetooth != null) {
                                                    bluetooth.searchDrivces();
                                                }
                                            }));
                                            break;
                                        case ReadVin:
                                            vin.setText("读取中...");
                                            break;
                                        case ConnectingECU:
                                            Log.i(TAG, "run: 尝试连接ECU");
                                            runOnUiThread(() -> title.setText("尝试连接ECU:" + note));
                                            break;
                                        case UncnnectECU:
                                            runOnUiThread(() -> title.setText("无法连接ECU:" + note));
                                            break;
                                        case UnConnect:
                                            runOnUiThread(() -> {
                                                title.setText("设备断开连接");
                                                icTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_connect_fail_24dp));
                                                vin.setText("*****************");
                                            });
                                            break;
                                    }
                                });
                            }
                        });

                        bluetooth.setOnTrafficDataListener(new OnTrafficDataListener() {
                            @Override
                            public void onStatusChange(String s) { }

                            @Override
                            public void onRTDData(final TrafficData data) {
                                Field[] fields = data.getClass().getDeclaredFields();
                                for (final Field fd : fields) {
                                    fd.setAccessible(true);
                                    if (viewMap.containsKey(fd.getName())) {
                                        //Log.i(TAG, "onRTDData: "+fd.getName());
                                        runOnUiThread(() -> {
                                            try {
                                                viewMap.get(fd.getName()).setText(fd.get(data) + "");
                                            } catch (IllegalAccessException e1) {
                                                e1.printStackTrace();
                                            }
                                        });

                                    }
                                }

                                if(exported()) {
                                    obdResult.setEngineCoolantTemperature(data.getEct());
                                    obdResult.setEngineLoadValue(data.getEngineLoad());
                                    obdResult.setEngineRPM(data.getRotaSpeed());
                                    obdResult.setFuelLevelInput(data.getRemainingFuel());
                                    obdResult.setIntakeABSPressure(data.getIntakeManifold());
                                    obdResult.setIntakeAirTemperature(data.getIntakeTemperature());
                                    obdResult.setMassAirflowRate(data.getAirVolumeIntake());
                                    obdResult.setSpeed(data.getSpeed());
                                    obdResult.setThrottlePosition(data.getThrottlePosition());
                                    obdResult.setTimingAdvance(data.getCylinderIgnitionAdvanceAngle());

                                    //Intent i = new Intent();
                                    //JSON.toJSON(obdResult);
                                    //i.putExtra("obdData",JSON.toJSONString(obdResult));
                                    //setResult(RESULT_OK,i);
                                    //finish();
                                }
                            }

                            @Override
                            public void onVin(final String vin) {
                                search.setOnClickListener(v -> {
                                    Intent i = new Intent();
                                    i.setClass(OBDActivity.this, IndexActivity.class);
                                    i.putExtra("content", vin);
                                    i.putExtra("title", vin);
                                    i.putExtra("obd", true);
                                    startActivity(i);
                                });
                                obdResult.setVin(vin);
                                runOnUiThread(() -> {
                                    search.setVisibility(View.VISIBLE);
                                    OBDActivity.this.vin.setClickable(true);
                                    OBDActivity.this.vin.setText(vin);
                                });
                            }
                        });

                        break;
                    case 3:
                    case 2:
                        break;
                }
            }
        };
        if (exported()) {
            ViewGroup.LayoutParams layout = toolbar.getLayoutParams();
            layout.height += 25;
            toolbar.setLayoutParams(layout);
            SharedPreferences tokenConfig = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            final String token = tokenConfig.getString("TOKEN", null);

            Log.i(TAG, "onCreate: TTTTTTTTTTTTTTTToken"+token);
//            new Token(token, this, (result, message) -> {
//
//                if (result)
//                {
//                    if(apiKey == null||!apiKey.equals(Token.apiKey))
//                    {
//                        runOnUiThread(() -> new AlertDialog.Builder(OBDActivity.this,5).setMessage("无效的ApiKey").setPositiveButton("确定", (dialog, which) -> {
//                            setResult(RESULT_FIRST_USER);
//                            finish();
//                        }).show());
//                    }
//
//                    Message msg = new Message();
//                    msg.what =1;
//                    uiHandler.sendMessage(msg);
//
//                } else
//                {
//                    runOnUiThread(() -> new AlertDialog.Builder(OBDActivity.this,5).setMessage(message).setPositiveButton("确定", (dialog, which) -> {
//                        setResult(RESULT_FIRST_USER);
//                        finish();
//                    }).show());
//                }
//            });
        } else if( apiKey.equals("com.jshsoft.vin")) {
            Message msg = new Message();
            msg.what =1;
            uiHandler.sendMessage(msg);

        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy(){

        if (bluetooth != null) {
            bluetooth.destroy();
        }
        super.onDestroy();
        //Log.i(TAG, "onDestroy: ====================>");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.i(TAG, "onCreateOptionsMenu: " + getIsToolModel());
        if (exported()) {
            getMenuInflater().inflate(R.menu.obd_menu, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_obd_cancel) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_obd_ok) {
            Intent i = new Intent();
            //JSON.toJSON(obdResult);
            i.putExtra("obdData",JSON.toJSONString(obdResult));
            setResult(RESULT_OK,i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
