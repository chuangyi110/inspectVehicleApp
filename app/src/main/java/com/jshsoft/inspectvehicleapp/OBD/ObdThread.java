package com.jshsoft.inspectvehicleapp.OBD;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2016/8/10.
 */
public class ObdThread extends Thread
{
    private LinkedBlockingQueue<String> commandQueues = new LinkedBlockingQueue<>();

    private OnRequestDataListener onRequestDataListener;

    private BluetoothSocket bluetoothSocket;

    private boolean execute = false;

    public ObdThread(OnRequestDataListener requestDataListener,BluetoothSocket bluetoothSocket)
    {
        this.onRequestDataListener = requestDataListener;
        this.bluetoothSocket = bluetoothSocket;
    }

    public void put(String command)
    {
        try
        {
            commandQueues.put(command);
            execute =true;

        } catch (InterruptedException e)
        {

            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            InputStream inputStream = bluetoothSocket.getInputStream();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        while (execute)
        {
            if(commandQueues.size()>0)
            {
                try
                {
                    String command = commandQueues.take();

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
