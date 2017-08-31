package com.zjfd.chenxiao.DHL.UI;

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

import com.dao.Setting;
import com.zjfd.chenxiao.DHL.R;

//import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import java.util.Timer;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by Administrator on 2017/4/19.
 */
public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    Button start_spot, open_flashlight, close_flashlight;
    private static final String TAG = "ScanActivity";
    Timer timer = new Timer();
    public int recLen;
    private boolean star_stop=true;
    private ImageView iv_back;
    Setting setting;
    public String epc;

    private QRCodeView mQRCodeView;
    private TextView tv_value, tv_jishi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        setting = (Setting) this.getIntent().getSerializableExtra("Scan");

        recLen=Integer.parseInt(setting.getScanInterval());
        initView();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();

//        HomeActivity.showInfoList.clear();//进来的时候清空主页数据

    }

    private void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        tv_jishi = (TextView) findViewById(R.id.tv_jishi);
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
        epc=result;
//        HomeActivity.addShowInfoToList(result);//往HomeActivity添加数据
        recLen = Integer.parseInt(setting.getScanInterval());
        tv_jishi.setVisibility(View.VISIBLE);
        tv_value.setVisibility(View.VISIBLE);
        tv_value.setText(result);
        vibrate();
        mQRCodeView.stopSpot();//关闭扫描
        Message message = handler.obtainMessage(1);     // Message
        handler.sendMessageDelayed(message, 1000);
    }

    final Handler handler = new Handler() {

        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    tv_jishi.setText("扫描倒计时：" + recLen);

                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        tv_jishi.setVisibility(View.GONE);
                        tv_value.setVisibility(View.GONE);
                        mQRCodeView.startCamera();
                        mQRCodeView.startSpot();
                    }
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                if (star_stop){
                    start_spot.setText("开始扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.stopSpot();
                    star_stop=false;
                }else{
                    start_spot.setText("停止扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.startSpot();
                    star_stop=true;
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
}
