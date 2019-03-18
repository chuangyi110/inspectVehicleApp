package com.jshsoft.inspectvehicleapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mmm.detectorsdk.DetectorUtility;

/**
 * 逆反射模块
 */
public class BackReflectionActivity extends Activity{
    private DetectorUtility detectorUtility;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.back_reflection_activity);
        /*创建检测仪对象*/
        detectorUtility=new DetectorUtility();

        /*初始化，并注册回调函数
         * backgroudHandler 负责所有全局事件
         * caliberationHandler 处理校准事件
         * testHandler 测试结果上报
         */
        if(DetectorUtility.DETECTOR_SUCCESS != detectorUtility.init(backgroudHandler, caliberationHandler, testHandler))
            Toast.makeText(this, "设备连接失败", Toast.LENGTH_SHORT).show();


        /*
         * 如果配对了多台蓝牙设备，需要进行选择
         * 如果只配对了1台蓝牙设备，则系统会自动进行连接
         */
        if(detectorUtility.mBTStatus == DetectorUtility.BTStatus.BT_DEVEICE_SELECT)
            selectDevice();
    }

    ProgressDialog pd;//链接处理的弹出框
    /*获取所有配对的蓝牙设备，并根据用户的选择进行连接*/
    private void selectDevice(){
        new AlertDialog.Builder(BackReflectionActivity.this)
                .setTitle("选择蓝牙设备")
                .setItems(detectorUtility.getDeviceNameList(), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        detectorUtility.connectDevice(which);
                        pd = ProgressDialog.show(BackReflectionActivity.this, null, "连接中...");
                        return;
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * 读取当前档位位置，结果会在backgroudHandler中上报
     */
    public void readSwitchPos(View view)
    {
        detectorUtility.readSwitchPositoin();

    }

    /*
     * 校准黑板 结果会在caliberationHandler中上报
     */
    public void caliberateBlack(View view)
    {
        detectorUtility.calibBlack();

    }

    /*
     * 校准标准板  结果会在caliberationHandler中上报
     * 根据当前的档位不同，显示的文字会在backgroudHandler中进行修改
     */
    public void caliberateStd(View view)
    {
        int ra = Integer.parseInt(((EditText)findViewById(R.id.txt_ra)).getText().toString());
        detectorUtility.calibWhite(ra);

    }

    /*
     * 重新连接检测仪
     */
    public void reConnect(View view){
        detectorUtility.reConnect();
        /*
         * 如果配对了多台蓝牙设备，需要进行选择
         * 如果只配对了1台蓝牙设备，则系统会自动进行连接
         */
        if(detectorUtility.mBTStatus == DetectorUtility.BTStatus.BT_DEVEICE_SELECT)
            selectDevice();
        else
            pd = ProgressDialog.show(BackReflectionActivity.this, null, "连接中...");
    }

    @SuppressLint("HandlerLeak")
    protected final Handler backgroudHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {


            switch(msg.what)
            {
                case DetectorUtility.EVENT_CONNECTION_FAILED:
                    if(pd!=null)
                        pd.dismiss();
                    Toast.makeText(getBaseContext(), "设备连接失败", Toast.LENGTH_SHORT).show();
                    ((TextView)findViewById(R.id.txt_status)).setText("连接失败");
                    break;
                case DetectorUtility.EVENT_CONNECTION_SUCCESS:
                    if(pd!=null)
                        pd.dismiss();
                    ((TextView)findViewById(R.id.txt_status)).setText("连接成功");
                    ((TextView)findViewById(R.id.txt_serial)).setText("序列号："+(String)msg.obj);
                    break;
                case DetectorUtility.EVENT_CALIBERATION_DATE:
                    ((TextView)findViewById(R.id.txt_calibDate)).setText(String.format("上次校准时间： %1$s天 %2$s小时", (Long.parseLong((String)msg.obj))/24, (Long.parseLong((String)msg.obj))%24));

                    break;
                case DetectorUtility.EVENT_RATE_UPDATED:
//
                    break;
                case DetectorUtility.EVENT_DEVICE_UNAUTHORIZED:
                    ((TextView)findViewById(R.id.txt_status)).setText("产品未授权");
                    break;

                case DetectorUtility.EVENT_DEVICE_SWITCH_POS:
                    if(msg.arg1 == 0){
                        ((TextView)findViewById(R.id.txt_switch_pos)).setText("当前档位为标识档");
                        ((Button)findViewById(R.id.cliberate_std)).setText("校准白板或红板");
                    }
                    else{
                        ((TextView)findViewById(R.id.txt_switch_pos)).setText("当前档位为尾板档");
                        ((Button)findViewById(R.id.cliberate_std)).setText("校准黄板或红板");
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak") protected final Handler caliberationHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case DetectorUtility.EVENT_CALIBERATION_BLACK_COMPLETE:
                    ((TextView)findViewById(R.id.txt_caliberation_result)).setText("黑板校准完成");
                    break;
                case DetectorUtility.EVENT_CALIBERATION_WHITE_COMPLETE:
                    ((TextView)findViewById(R.id.txt_caliberation_result)).setText("标准板板校准完成");

                    break;
                case DetectorUtility.EVENT_CONNECTION_FAILED:
                    ((TextView)findViewById(R.id.txt_caliberation_result)).setText("连接失败");
                    break;
                case DetectorUtility.EVENT_CALIBERATION_FAILED:
                    ((TextView)findViewById(R.id.txt_caliberation_result)).setText("校准失败");
                    break;
                default:
                    break;
            }
            //btUtil.caliberationHandler = null;
            super.handleMessage(msg);
        }
    };


    @SuppressLint("HandlerLeak") protected final Handler testHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            TextView reslutView = (TextView)findViewById(R.id.txt_test_result);
            switch(msg.what)
            {
                case DetectorUtility.EVENT_LABLE_TEST_RESULT:
                    reslutView.setText("反光标识："+msg.arg2);
                    break;
                case DetectorUtility.EVENT_BOARD_TEST_RESULT:
                    reslutView.setText("尾板："+msg.arg2);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


}
