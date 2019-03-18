package com.jshsoft.inspectvehicleapp.OBD;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Administrator on 2016/4/21.
 */
public class Bluetooth
{
    private static String TAG ="Bluetooth";
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private UUID mUuid = null ;
    private BluetoothSocket blueSocket;
    //private String stopCommand = "OBD=0";
    private OutputStream outputStream;
    private InputStream inputStream;

    private BluetoothDevice remoteBluetoothDevice;
    private boolean enable;
    /**
     * @return蓝牙是否开启
     */
    public boolean isEnable() {
        return enable;
    }


    /**
     * 是否搜索到设备
     */
    private boolean search;

    private String vinCode ;

    private boolean update = false;

    public boolean isSearch() {
        return search;
    }

    private boolean searching;

    public boolean isSearching() {
        return searching;
    }

    private boolean read;

    private OnTrafficDataListener onTrafficDataListener;


    public void setOnTrafficDataListener(OnTrafficDataListener onTrafficDataListener)
    {
        this.onTrafficDataListener = onTrafficDataListener;
    }

    private OnBluetoothSearchListener onBluetoothSearchListener;

    public void setOnBluetoothSearchListener(OnBluetoothSearchListener onBluetoothSearchListener) {
        this.onBluetoothSearchListener = onBluetoothSearchListener;
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            BluetoothDevice tmpdevice;
            switch (action)
            {
                case BluetoothDevice.ACTION_FOUND:

                    tmpdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if(onBluetoothSearchListener!= null)
                    {
                        onBluetoothSearchListener.onSearch(tmpdevice);
                        onBluetoothSearchListener.onStatusChange(BlueStatus.Searched,tmpdevice.getName());
                    }

                    //searching =false;

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:

                    if(searching)
                    {
                        if(onBluetoothSearchListener!= null)
                        {
                            onBluetoothSearchListener.onUnSearch();
                            onBluetoothSearchListener.onStatusChange(BlueStatus.UnSearch,"");
                        }
                        destroy();

                    }
                    break;
            }

        }
    };


    public Bluetooth(Context context, String uuid)
    {
        this.context = context;
        this.mUuid = UUID.fromString(uuid);
        init();
    }

    private void init()
    {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null)
        {
            Log.d(TAG,"获取不到本地蓝牙适配器");
        }
        else
        {
            if(bluetoothAdapter!= null && !bluetoothAdapter.isEnabled())
            {
                Log.d(TAG, "init: 开启蓝牙");
                bluetoothAdapter.enable();
            }
            enable = bluetoothAdapter.isEnabled();
            searchDrivces();
        }

    }

    public void reTryConnect()
    {
        if(remoteBluetoothDevice!= null)
        {
            connect(remoteBluetoothDevice);
        }
    }


    public void connect(final BluetoothDevice device)
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    remoteBluetoothDevice = device;
                    blueSocket = device.createRfcommSocketToServiceRecord(mUuid);
                    Log.d(TAG, "接口已创建");
                    assert blueSocket != null;
                    Log.d(TAG, "开始连接："+blueSocket.getRemoteDevice().getName());
                    if(onBluetoothSearchListener!= null)
                    {
                        onBluetoothSearchListener.onStatusChange(BlueStatus.Connecting,blueSocket.getRemoteDevice().getName());
                    }
                    Thread.sleep(1000);
                    blueSocket.connect();
                    Log.d(TAG, "已连接："+blueSocket.getRemoteDevice().getName());

                    Thread.sleep(1000);
                    if(onBluetoothSearchListener!= null)
                    {
                        onBluetoothSearchListener.onStatusChange(BlueStatus.Connect,blueSocket.getRemoteDevice().getName());
                    }
                    //clear();
                    int connectCount = 0;
                    while (!executeCommand("ECU?"))
                    {
                        connectCount++;
                        onBluetoothSearchListener.onStatusChange(BlueStatus.ConnectingECU,connectCount+"");
                        Thread.sleep(5000);
                        if(connectCount==5)
                        {
                            onBluetoothSearchListener.onStatusChange(BlueStatus.UncnnectECU,blueSocket.getRemoteDevice().getName());
                            return;
                        }
                    }

                    //execute("VER?");
                    Thread.sleep(600);

                    //String id = "JSH945B394";

                    //Log.d(TAG, "run: mid :" + execute("READID"));
////
                    if(onTrafficDataListener!=null)
                    {
                        if(onBluetoothSearchListener!=null)
                        {
                            onBluetoothSearchListener.onStatusChange(BlueStatus.ReadVin,"");
                        }
                        String vin =execute("VIN?");
                        //Log.d(TAG, "run: onVin" +vin + "length: "+vin.length());
                        onTrafficDataListener.onVin(vin);
                        vinCode =vin;
                    }
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            read = true;
                            while (read)
                            {
                                try
                                {
                                    Thread.sleep(600);
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                final String rtd = execute("RTD?");
                                if(rtd == null)
                                {
                                    read =false;
                                    return;
                                }
                                if(onTrafficDataListener!=null/*&& (rtd.startsWith("$OBD+RTD?=")||rtd.startsWith("$AT+RTD?="))*/)
                                {
                                    //Log.d(TAG, "onRTDData: "+rtd);
                                    onTrafficDataListener.onRTDData(TrafficData.StringFormat(rtd));
                                }
                                if(!update)
                                {
                                    Log.d(TAG, "run: ObdUpdate");
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
//                                            String url = Address.address +"/Services/UpObdData";
//                                            Map<String,Object> paps = new HashMap<String, Object>();
//                                            paps.put("vin",vinCode);
//                                            paps.put("token", TokenManager.getManager().getToken() );
//                                            paps.put("mac",getRemoteBluetoothDevice().getAddress());
//                                            paps.put("data",rtd);
//                                            try
//                                            {
//                                                String res = HttpClient.net(url,paps,"GET");
//                                                UpObdDataModel model = JSON.parseObject(res,UpObdDataModel.class);
//                                                update = model.Status;
//                                            } catch (Exception e)
//                                            {
//                                                e.printStackTrace();
//                                            }
                                            update= true;
                                        }
                                    }).start();
                                }
                            }
                        }
                    }).start();


                } catch (IOException e)
                {
                    if(onBluetoothSearchListener!= null)
                    {
                        Log.d(TAG, "run() return: " + e.toString());
                        onBluetoothSearchListener.onStatusChange(BlueStatus.ConnectFail,blueSocket.getRemoteDevice().getName());
                    }

                    e.printStackTrace();

                } catch (InterruptedException e)
                {
                    Log.d(TAG, "run() returned: " + e.toString());
                    e.printStackTrace();
                }

            }
        }).start();


    }


    public boolean clear() throws IOException
    {

        if(blueSocket.isConnected())
        {
            inputStream = blueSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int p = inputStream.read(buffer);
            return true;
        }
        return false;
    }

//    public void RequstData() throws IOException
//    {
//        inputStream = blueSocket.getInputStream();
//        byte[] buffer = new byte[1024];
//        int p = inputStream.read(buffer);
//        String readDate = "";
//        if(p!=-1)
//        {
//            String stream = new String(buffer,0,p);
//            readDate += stream;
//            if(readDate.endsWith("\r\n"))
//            {
//                Log.d(TAG, String.format("返回值: %s", readDate.replace("\r\n", "").replace("=", "")));
//                //return  readDate.replace("\r\n","").replace("=","");
//            }
//        }
//    }

    public String execute(String command)
    {

        try
        {
            if (blueSocket.isConnected())
            {

                String commandHead = "AT+" + command;
                command = "AT+" + command + "\r\n";
                outputStream = blueSocket.getOutputStream();
                Log.d(TAG, "run: 发送：" + commandHead + " 指令");
                outputStream.write(command.getBytes());
                outputStream.flush();
                inputStream = blueSocket.getInputStream();
                byte[] buffer = new byte[1024];
                String readDate = "";

                int p = inputStream.read(buffer);

                if (p != -1)
                {
                    String stream = new String(buffer, 0, p);
                    readDate += stream;
                    if (readDate.endsWith("\r\n"))
                    {
                        Log.d(TAG, "execute: " + readDate.replace("\r\n", ""));
                        //Log.d(TAG, String.format("返回值: %s", readDate.replace("\r\n", "").replace(commandHead + "=", "")));
                        return readDate.replace("\r\n", "").replace(commandHead + "=", "");
                    }
                }
                return null;
            }

        } catch (IOException e)
        {
            onBluetoothSearchListener.onStatusChange(BlueStatus.UnConnect, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    private boolean executeCommand(String command)
    {
        if(blueSocket.isConnected())
        {
            //blueSocket.getConnectionType()
            try
            {
                String commandHead ="AT+"+command;
                command = "AT+"+command+"\r\n";
                outputStream = blueSocket.getOutputStream();
                Log.d(TAG, "run: 发送：" +commandHead+" 指令");
                outputStream.write(command.getBytes());
                outputStream.flush();
                inputStream = blueSocket.getInputStream();
                byte[] buffer = new byte[1024];

                int p ;
                p= inputStream.read(buffer);

                while (p!=-1)
                {
                    String stream = new String(buffer,0,p);
                    Log.d(TAG, "返回值: " + stream.replace("\r\n",""));

                    if(stream.replace("\r\n","").equals(commandHead+"=OK")||stream.replace("\r\n","").equals(commandHead+"=1"))
                    {
                        return true;
                    }
                    if(stream.replace("\r\n","").contains("Error")||stream.replace("\r\n","").equals(command.replace("\r\n","")+"=0"))
                    {
                        return false;
                    }

                    p =inputStream.read(buffer);
                    stream = "";

                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }


    public void destroy()
    {
        if(bluetoothAdapter != null&& searching)
        {
            Log.d(TAG, "destroy: 停止搜索");
            bluetoothAdapter.cancelDiscovery();
            context.unregisterReceiver(broadcastReceiver);
            searching = false;
            //bluetoothAdapter.disable();
        }
        if(blueSocket!=null&& blueSocket.isConnected()&&!search)
        {
            try
            {
                read = false;
                Thread.sleep(2000);
                blueSocket.close();
                Log.d(TAG, "destroy: close socket");
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void searchDrivces()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if(context!= null&&bluetoothAdapter!=null)
                {
                    context.registerReceiver(broadcastReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
                    context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
                    bluetoothAdapter.startDiscovery();
                    Log.d(TAG, "开始搜索设备......");
                    searching =true;
                    if(onBluetoothSearchListener!= null)
                    {
                        onBluetoothSearchListener.onStatusChange(BlueStatus.Searching,"");
                    }
                }else {
                    Log.d(TAG, "run: context null or bluetoothAdapter");
                }

            }

        }).start();
    }


    public BluetoothDevice getRemoteBluetoothDevice()
    {
        return remoteBluetoothDevice;
    }

    //BlockingDeque<String> commandQueues =
    LinkedBlockingDeque<String> commandQueues = new LinkedBlockingDeque<>();


}
