package com.jshsoft.inspectvehicleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;

import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.jshsoft.inspectvehicleapp.moel.Item;
import com.jshsoft.inspectvehicleapp.moel.ViolationInformationEntity;
import com.jshsoft.inspectvehicleapp.util.LogUtil;
import com.jshsoft.inspectvehicleapp.util.SharedPreferencesUtils;
import com.jshsoft.inspectvehicleapp.widget.LoadingDialog;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  历史查验记录
 */
public class HistoricalActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "HistoricalActivity";
    private Button more;
    private Button search_button;
    private String historicalTime;
    private LoadingDialog mLoadingDialog;
    private String plateNumber;
    private EditText search;
    private SmartTable<Item> table;
    Column<Boolean> operation;
    Column<String> id;
    Column<String> project;
    Column<String> type;
    Column<String> particulars;
    List<String> name_selected = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historical_activity);
        initViews();
        setupEvents();

        initData();
        if(plateNumber!=null&&!plateNumber.trim().isEmpty()){
            getData();
        }
    }

    private void initViews() {
        more = (Button) this.findViewById(R.id.more);
        search = (EditText) this.findViewById(R.id.et_search);
        search_button = (Button)findViewById(R.id.search_button);
        table = (SmartTable<Item>)findViewById(R.id.table);

    }
    private void setupEvents() {
        more.setOnClickListener(this);
        search_button.setOnClickListener(this);
    }
    private void initData(){
        Intent intent = getIntent();
        plateNumber = (intent.getStringExtra("plateNumber")).toString();
        boolean b  =plateNumber!=null&&!plateNumber.trim().isEmpty();
        if(b){
            search.setText(plateNumber);
        }else{
            showToast("请输入要查询车牌号");
        }
    }
    private void getData(){
        if(plateNumber.isEmpty()){
            showToast("请输入要查询车牌号");
            return;
        }
        showLoading();//显示加载框
        Thread historicalRunable = new Thread(){
            @Override
            public void run() {
                super.run();
                setSearchBtnClickable(false);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("plateNumber", plateNumber)
                        .build();
                Request request = new Request.Builder()
                        .url("https://vehicle.jshsoft.com:8080/h")
                        .post(requestBody)
                        .build();
                LogUtil.i(TAG,"++++++++++++++++++进行查询,查询参数:"+plateNumber+"++++++++++++++++++");
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e instanceof ConnectException){
                            showToast("网络异常！请确认网络情况");
                        }else{
                            showToast(e.getMessage());
                        }
                        LogUtil.i(TAG,"++++++++++++++++++查询失败:错误原因"+e.getMessage()+"++++++++++++++++++");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
                        Headers headers = response.headers();
//                        for (int i = 0; i < headers.size(); i++) {
//                            Log.d(TAG, headers.name(i) + ":" + headers.value(i));
//                        }
                        String req = response.body().string();
                        LogUtil.i(TAG, "++++++++++++++++++查询成功:返回数据为："+req+"++++++++++++++++++");
                        try{
                            Map map = (Map) JSONObject.parse(req);
                            if(Integer.parseInt(map.get("code").toString())==0){
                                String data = map.get("obj").toString();
                                List<Item> list = JSON.parseArray(data,Item.class);
                                processingData(list);
                            }else {
                                showToast(map.get("msg").toString());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
//                //TODO 数据
//                List<Item> codeList = new ArrayList<Item>();
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                codeList.add(new Item("1","润滑油页面标准","合格","合乎标准",false));
//                processingData(codeList);
                setSearchBtnClickable(true);
                hideLoding();
            }
        };
        historicalRunable.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.init(this);
        LogUtil.i(TAG,"++++++++++++++++++进入历史查询页面++++++++++++++++++");
    }

    /**
     * 处理数据将数据落入Table
     * @param codeList
     */
    private void  processingData(List codeList){
        id = new Column<>("序号", "id");
        id.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String bool, int position) {
                Toast.makeText(HistoricalActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();

                table.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                table.invalidate();
            }
        });

        project = new Column<>("查验项目", "project");
        project.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String bool, int position) {
                Toast.makeText(HistoricalActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();
                ts(operation,position);
                table.refreshDrawableState();
                table.invalidate();
            }
        });
        type = new Column<String>("判定","type");
        type.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                Toast.makeText(HistoricalActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();
                ts(operation,position);
                table.refreshDrawableState();
                table.invalidate();
            }
        });
        particulars = new Column<String>("详细情况","particulars");
        particulars.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                Toast.makeText(HistoricalActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();
                ts(operation,position);
                table.refreshDrawableState();
                table.invalidate();
            }
        });

        int size = DensityUtils.dp2px(HistoricalActivity.this,30);

        operation = new Column<>("勾选", "operation", new ImageResDrawFormat<Boolean>(size,size) {    //设置"操作"这一列以图标显示 true、false 的状态
            @Override
            protected Context getContext() {
                return HistoricalActivity.this;
            }
            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.drawable.check;      //将图标提前放入 app/res/mipmap 目录下
                }
                return R.drawable.unselect_check;
            }
        });
        operation.setComputeWidth(40);
        operation.setOnColumnItemClickListener(new OnColumnItemClickListener<Boolean>() {
            @Override
            public void onClick(Column<Boolean> column, String value, Boolean bool, int position) {
                ts(operation,position);
                table.refreshDrawableState();
                table.invalidate();
            }
        });

        final TableData<Item> tableData = new TableData<>("测试标题",codeList, operation, id, project,type,particulars);
        table.getConfig().setShowTableTitle(false);

        table.setTableData(tableData);
        table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));
        table.getConfig().setMinTableWidth(1024);       //设置表格最小宽度
        FontStyle style = new FontStyle();
        style.setTextSize(30);
        table.getConfig().setContentStyle(style);       //设置表格主题字体样式
        table.getConfig().setColumnTitleStyle(style);   //设置表格标题字体样式
        table.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(HistoricalActivity.this, R.color.tableBackground);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
        table.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }
    /**
     * 切换table选中未选中
     */

    private void ts(Column<Boolean> column,int position){
        if(column.getDatas().get(position)){
            showName(position, false);
            column.getDatas().set(position,false);
        }else{
            showName(position, true);
            column.getDatas().set(position,true);
        }
    }
    private void showLoading() {
        if(mLoadingDialog ==null){
            mLoadingDialog = new LoadingDialog(this,getString(R.string.loading),false);
        }
        mLoadingDialog.show();
    }
    private void hideLoding() {
        if(mLoadingDialog!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });
        }
    }
    private String getPlateNumber(){
        String plateNumberSearch = search.getText().toString().trim();
        if(plateNumberSearch!="请输入要查询车牌号"&&plateNumberSearch!=null&&!plateNumberSearch.trim().isEmpty()) {
            plateNumber = plateNumberSearch;
        }else{
            plateNumberSearch = null;
        }
        return plateNumberSearch;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_button:
                getPlateNumber();
                getData();
                break;
            case R.id.more:
                Intent intent = new Intent();

                startActivity(new Intent(HistoricalActivity.this,HistoricalMenuPop.class));
                break;
        }
    }
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HistoricalActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 按钮锁定
     */
    public void setSearchBtnClickable(boolean clickable){
        search_button.setClickable(clickable);
    }

    //重新激活时触发
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtils helper =new SharedPreferencesUtils(this,"setting");
        String historicalTime = helper.getString("historicalTime");
        System.err.println(historicalTime);
    }
    /**
     *  监听回退键
     */
    @Override
    public void onBackPressed() {
        if(mLoadingDialog!=null){
            if(mLoadingDialog.isShowing()){
                mLoadingDialog.cancel();
            }else {
                finish();
            }
        }else{
            finish();
        }
    }
    /**
     * 获取点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
                view.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏
     */
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 收集所有被勾选的姓名记录到 name_selected 集合中，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    public void showName(int position, boolean selectedState){
        List<String> rotorIdList = id.getDatas();
        if(position >-1){
            String rotorTemp = rotorIdList.get(position);
            if(selectedState && !name_selected.contains(rotorTemp)){            //当前操作是选中，并且“所有选中的姓名的集合”中没有该记录，则添加进去
                name_selected.add(rotorTemp);
            }else if(!selectedState && name_selected.contains(rotorTemp)){     // 当前操作是取消选中，并且“所有选中姓名的集合”总含有该记录，则删除该记录
                name_selected.remove(rotorTemp);
            }
        }
        for(String s:name_selected){
            System.out.print(s + " -- ");
        }
    }

}
