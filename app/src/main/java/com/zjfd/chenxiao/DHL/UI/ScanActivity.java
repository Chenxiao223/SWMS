package com.zjfd.chenxiao.DHL.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Setting;
import com.dao.ShowInfo;
import com.zjfd.chenxiao.DHL.Fragment.RukuFragment;
import com.zjfd.chenxiao.DHL.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

//import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

/**
 * Created by Administrator on 2017/4/19.
 */
public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    Button start_spot, open_flashlight, close_flashlight;
    private static final String TAG = "ScanActivity";
    Timer timer = new Timer();
    private boolean star_stop = true;
    private ImageView iv_back;
    Setting setting;
    public String epc;
    private int flag;
//    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;
    private TextView tv_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        setting = (Setting) this.getIntent().getSerializableExtra("Scan");
        Intent intent = getIntent();
        flag = intent.getIntExtra("search", 0);
        initView();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_value = (TextView) findViewById(R.id.tv_value);
        start_spot = (Button) findViewById(R.id.start_spot);
        open_flashlight = (Button) findViewById(R.id.open_flashlight);
        close_flashlight = (Button) findViewById(R.id.close_flashlight);
        open_flashlight.setOnClickListener(this);
        start_spot.setOnClickListener(this);
        close_flashlight.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
//        epc = result;
        epc="2LCN100176+46000000";
        vibrate();
        mQRCodeView.stopSpot();//关闭扫描
        if (flag == 1) {
            addSearch(epc);
        } else {
            addRuku(epc);
        }
    }

    public void addRuku(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        RukuFragment.rukuFragment.showinfo = new ShowInfo();
        RukuFragment.rukuFragment.showinfo.setEpc(epc);
        RukuFragment.rukuFragment.showinfo.setTime(time);
        String rfid=RukuFragment.rukuFragment.getRfid();
        if (!RukuFragment.rukuFragment.isLetterDigitOrChinese(rfid.substring(0,4))) {//判断rfid不是货架标签
            RukuFragment.rukuFragment.addShowInfoToList(rfid);
        }else{
            Toast.makeText(ScanActivity.this, "请扫描货物标签", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void addSearch(String str) {
        SearchActivity.searchActivity.hashMap = new HashMap<>();
        SearchActivity.searchActivity.hashMap.put("content1", str);
        SearchActivity.searchActivity.addShowInfoToList(SearchActivity.searchActivity.getRfid());
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                if (star_stop) {
                    start_spot.setText("开始扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.stopSpot();
                    star_stop = false;
                } else {
                    start_spot.setText("停止扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.startSpot();
                    star_stop = true;
                }

                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.iv_back:
//                HomeActivity.showadapter.notifyDataSetChanged();
//                HomeActivity.tv_readCount.setText("" + HomeActivity.showInfoList.size());
//                HomeActivity.homeActivity.upData(epc);//上传数据
                finish();
                break;
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }
}
