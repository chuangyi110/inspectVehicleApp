package com.jshsoft.inspectvehicleapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jshsoft.inspectvehicleapp.intercepter.RetryIntercepter;
import com.jshsoft.inspectvehicleapp.util.LogUtil;
import com.jshsoft.inspectvehicleapp.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignActivity extends BaseActivity implements OnClickListener{
    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int CROP_PHOTO = 2;
    public static final int ALBUM_RESULT_CODE = 3;
    public static final String IMAGE_UNSPECIFIED = "image/*";//任意图片类型
    private static final String TAG = "Activity";
    private ImageView picture;
    private Button bt_camera,bt_upload;
    private String plateNumber;
    public static File tempFile;
    private Uri imageUri;
    private EditText search;
    private Bitmap bitmap;
    private Canvas mCanvas;
    private LoadingDialog mLoadingDialog;
    public void setTAG(){
        super.setTAG("SignActivity");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_activity);
        initViews();
        setupEvents();
        initData();
    }



    private void initViews() {
        setTAG();
        bt_camera = (Button)findViewById(R.id.bt_camera);
        bt_upload = (Button)findViewById(R.id.bt_upload);
        search = (EditText)findViewById(R.id.et_search);
        picture = findViewById(R.id.picture);
        mTv = (TextView) findViewById(R.id.warning);

    }
    private void setupEvents() {
        bt_camera.setOnClickListener(this);
        bt_upload.setOnClickListener(this);
    }
    private void initData() {
        checkNetWork();
        Intent intent = getIntent();
        plateNumber = (intent.getStringExtra("plateNumber")).toString();
        //System.out.println(plateNumber.trim().isEmpty());
        boolean b  =plateNumber!=null&&!plateNumber.trim().isEmpty();
        if(b){
            search.setText(plateNumber);
        }else{
            showToast("请输入要查询车牌号");
        }
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     *   开启摄像头
     */
    public void openCamera(Activity activity) {

        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this,"请开启存储权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }else{
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(this.getFilesDir() + File.separator +"myimages/",
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this,"请开启存储权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }
    /**
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    /**
     *   回调显示图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("red","resultCode:"+resultCode+",requestCode:"+requestCode);
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    //拍照后调用剪辑
                    //goToCrop(imageUri);
                    //实时缓存
                    MediaScannerConnection.scanFile(this, new String[] { tempFile.getAbsolutePath() }, null, null);
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri)).copy(Bitmap.Config.ARGB_8888, true);
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90);
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//                                matrix, true);
                        picture.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(resultCode==RESULT_CANCELED){
                    System.out.println("返回");
                    //直接返回并删除无效photo
                    this.getContentResolver().delete(imageUri, null, null);//cerma返回时删除
                    imageUri = null;

                }
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_camera:
                plateNumber = search.getText().toString().trim();
                if(plateNumber.isEmpty()){
                    showToast("请输入要查询车牌号");
                    break;
                }
                openCamera(this);
                break;
            case R.id.bt_upload:
                if(imageUri==null){
                    showToast("请选择或拍摄图片后上传");
                    break;
                }
                uploadImg();
                break;


        }
    }
    private void uploadImg(){
//        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Log.e(TAG,tempFile.getPath());
//        File file = tempFile;
//        Request request = new Request.Builder()
//                .url("https://vehicle.jshsoft.com:8080/f/辽D66B67")
//                .post(RequestBody.create(mediaType, file))
//                .build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "onFailure: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
//                Headers headers = response.headers();
//                for (int i = 0; i < headers.size(); i++) {
//                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
//                }
//                Log.d(TAG, "onResponse: " + response.body().string());
//            }
//        });
//
        File file = tempFile;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .addInterceptor(new RetryIntercepter(2,SignActivity.this))
                .build();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url("https://vehicle.jshsoft.com:8080/f/"+plateNumber)
                .post(multipartBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e instanceof ConnectException){
                    showToast("网络异常！请确认网络情况");
                }else{
                    showToast(e.getMessage());
                }
                LogUtil.i(TAG, "++++++++++++++++++上传失败:错误原因" + e.getMessage() + "++++++++++++++++++");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String req = response.body().string();
                Map map = (Map) JSONObject.parse(req);
                LogUtil.i(TAG, "++++++++++++++++++上传成功" + map.get("msg") + "++++++++++++++++++");
                showToast(map.get("msg").toString());

            }
        });
    }
}
