package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.AgentOperation;
import com.dao.DBOperation;
import com.dao.Setting;
import com.zjfd.chenxiao.DHL.R;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9 0009.
 */
public class SettingActivity extends Activity {
    ImageView imageView;
    Setting setting;

    private int resultCode;
    TextView tv_Company_info;
    TextView tv_Company_click;
    TextView tv_Node_info;
    TextView tv_Node_click;
    TextView tv_WorkName_info;
    TextView tv_WorkName_click;
    TextView tv_Url_info;
    TextView tv_Url_click;
    TextView tv_Telephone_info;
    TextView tv_Telephone_click;
    TextView tv_Lat_Lon_info;
    TextView tv_Lat_Lon_click;
    TextView tv_NewData_info;
    TextView tv_NewData_click;
    TextView tv_power_info;
    TextView tv_power_click;
    TextView tv_LogDeadline_info;
    TextView tv_LogDeadline_click;
    TextView tv_CCTime_info;
    TextView tv_CCTime_click;
    TextView tv_ScanStrategy_info;
    TextView tv_ScanStrategy_click;
    TextView tv_AgainDeadline_info;
    TextView tv_AgainDeadline_click;
    TextView tv_VersionNo_info;
    TextView tv_VersionNo_click;
    TextView tv_ScanInterval_info;
    TextView tv_ScanInterval_click;

    private List<String> list_ProductName = new ArrayList<String>();

    private String[] items1;

    private class setHandler extends Handler {

        WeakReference<Activity> mActivityRef;

        setHandler(Activity activity) {

            mActivityRef = new WeakReference<Activity>(activity);
        }

        public void handleMessage(android.os.Message msg) {

            Activity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Toast.makeText(SettingActivity.this, val, Toast.LENGTH_SHORT).show();
//            if (val.equals("已连接产品库，请选择产品")) {
//                new AlertDialog.Builder(SettingActivity.this).setTitle("产品名称").setItems(items1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // which 下标从0开始
//                        // ...To-do
//                        tv_product_info.setText(items1[which]);
//                    }
//                }).show();
//            }
        }

        ;
    }

    ;

    private setHandler handler = new setHandler(SettingActivity.this);

    Runnable networkTask1 = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();
            Bundle data = new Bundle();
            if (!AgentOperation.NetworkIsConnected(getApplicationContext())) {

                data.putString("value", "网络无法连接,无法获取产品库");
                msg.setData(data);
                handler.sendMessage(msg);

            } else {
//                AgentOperation.setConfig(setting);

                list_ProductName.clear();
                try {
//                    list_ProductName = AgentOperation.getProductName();
                    if (list_ProductName.size() > 0) {
                        items1 = new String[list_ProductName.size()];

                        list_ProductName.toArray(items1);

                        data.putString("value", "已连接产品库，请选择产品");

                        msg.setData(data);

                        handler.sendMessage(msg);


                    } else {

                        data.putString("value", "已连接产品库，无其他产品");
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    data.putString("value", "连接产品库异常");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }


            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);

        setting = (Setting) this.getIntent().getSerializableExtra("Setting");
        initView();

        setSettingToMenu(setting);

        tv_Company_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("公司", tv_Company_info.getText().toString(), tv_Company_info);
                //setting.setCompany(tv_Company_info.getText().toString());
            }
        });

        tv_WorkName_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("人员", tv_WorkName_info.getText().toString(), tv_WorkName_info);
                //setting.setWorkName(tv_WorkName_info.getText().toString());
            }
        });

        tv_Node_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("节点", tv_Node_info.getText().toString(), tv_Node_info);
                //setting.setNode(tv_Node_info.getText().toString());
            }
        });

        tv_Url_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("网址", tv_Url_info.getText().toString(), tv_Url_info);
                //setting.setDBIP(tv_ip_address_info.getText().toString());
            }
        });

        tv_Telephone_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("电话", tv_Telephone_info.getText().toString(), tv_Telephone_info);
                //setting.setPort(tv_port_address_info.getText().toString());
            }
        });

        tv_Lat_Lon_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("经纬度", tv_Lat_Lon_info.getText().toString(), tv_Lat_Lon_info);
            }
        });

        tv_NewData_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("备注", tv_NewData_info.getText().toString(), tv_NewData_info);
            }
        });

        tv_power_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

//				showSeekbarDialog("功率",tv_power_info.getText().toString(),tv_power_info);
                showEditDialog("功率", tv_power_info.getText().toString(), tv_power_info);
                //setting.setPower(tv_power_info.getText().toString());
            }
        });


        tv_LogDeadline_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("日志期限", tv_LogDeadline_info.getText().toString(), tv_LogDeadline_info);
                //setting.setExternalCode(tv_ExternalCode_info.getText().toString());
            }
        });

        tv_CCTime_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("重传次数", tv_CCTime_info.getText().toString(), tv_CCTime_info);
                //setting.setLon(tv_Lon_info.getText().toString());
            }
        });

        tv_ScanStrategy_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("扫描策略", tv_ScanStrategy_info.getText().toString(), tv_ScanStrategy_info);
                //setting.setLat(tv_Lat_info.getText().toString());
            }
        });


        tv_AgainDeadline_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("重复限制", tv_AgainDeadline_info.getText().toString(), tv_AgainDeadline_info);
                //setting.setNewData(tv_NewData_info.getText().toString());
            }
        });

        tv_ScanInterval_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("扫描间隔",tv_ScanInterval_info.getText().toString(),tv_ScanInterval_info);
            }
        });


        tv_VersionNo_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showTxtDialog("软件版本", tv_VersionNo_info.getText().toString());
            }
        });


        //返回主页面
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setMenuToSetting();
                insertDataBase();
                resultCode = 9;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Setting", (Serializable) setting);
                SettingActivity.this.setResult(resultCode, resultIntent);
                SettingActivity.this.finish();
            }


        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setMenuToSetting();
            insertDataBase();
            resultCode = 9;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("Setting", (Serializable) setting);
            SettingActivity.this.setResult(resultCode, resultIntent);
            SettingActivity.this.finish();

            return super.onKeyDown(keyCode, event);

        } else {

            return false;
        }

    }


    public void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        tv_Company_info = (TextView) findViewById(R.id.tv_Company_info);
        tv_Company_click = (TextView) findViewById(R.id.tv_Company_click);
        tv_Node_info = (TextView) findViewById(R.id.tv_Node_info);
        tv_Node_click = (TextView) findViewById(R.id.tv_Node_click);
        tv_WorkName_info = (TextView) findViewById(R.id.tv_WorkName_info);
        tv_WorkName_click = (TextView) findViewById(R.id.tv_WorkName_click);
        tv_Url_info = (TextView) findViewById(R.id.tv_Url_info);
        tv_Url_click = (TextView) findViewById(R.id.tv_Url_click);
        tv_Telephone_info = (TextView) findViewById(R.id.tv_Telephone_info);
        tv_Telephone_click = (TextView) findViewById(R.id.tv_Telephone_click);
        tv_Lat_Lon_info = (TextView) findViewById(R.id.tv_Lat_Lon_info);
        tv_Lat_Lon_click = (TextView) findViewById(R.id.tv_Lat_Lon_click);
        tv_NewData_info = (TextView) findViewById(R.id.tv_NewData_info);
        tv_NewData_click = (TextView) findViewById(R.id.tv_NewData_click);
        tv_power_info = (TextView) findViewById(R.id.tv_power_info);
        tv_power_click = (TextView) findViewById(R.id.tv_power_click);
        tv_LogDeadline_info = (TextView) findViewById(R.id.tv_LogDeadline_info);
        tv_LogDeadline_click = (TextView) findViewById(R.id.tv_LogDeadline_click);
        tv_CCTime_info = (TextView) findViewById(R.id.tv_CCTime_info);
        tv_CCTime_click = (TextView) findViewById(R.id.tv_CCTime_click);
        tv_ScanStrategy_info = (TextView) findViewById(R.id.tv_ScanStrategy_info);
        tv_ScanStrategy_click = (TextView) findViewById(R.id.tv_ScanStrategy_click);
        tv_AgainDeadline_info = (TextView) findViewById(R.id.tv_AgainDeadline_info);
        tv_AgainDeadline_click = (TextView) findViewById(R.id.tv_AgainDeadline_click);
        tv_VersionNo_info = (TextView) findViewById(R.id.tv_VersionNo_info);
        tv_VersionNo_click = (TextView) findViewById(R.id.tv_VersionNo_click);
        tv_ScanInterval_info= (TextView) findViewById(R.id.tv_ScanInterval_info);
        tv_ScanInterval_click= (TextView) findViewById(R.id.tv_ScanInterval_click);
    }


    public void setSettingToMenu(Setting newset) {
        tv_Company_info.setText(newset.getCompany());
        tv_Node_info.setText(newset.getNode());
        tv_WorkName_info.setText(newset.getWorkName());
        tv_Url_info.setText(newset.getUrl());
        tv_Telephone_info.setText(newset.getTelephone());
        tv_Lat_Lon_info.setText(newset.getLat_Lon());
        tv_NewData_info.setText(newset.getNewData());
        tv_power_info.setText(newset.getPower());
        tv_LogDeadline_info.setText(newset.getLogDeadline());
        tv_CCTime_info.setText(newset.getCCTime());
        tv_ScanStrategy_info.setText(newset.getScanStrategy());
        tv_AgainDeadline_info.setText(newset.getAgainDeadline());
        tv_VersionNo_info.setText(newset.getVesion());
    }

    public void showSeekbarDialog(String title, String edit, final View v) {

        final SeekBar sb = new SeekBar(SettingActivity.this);
        int i = 0;
        try {

            i = Integer.parseInt(edit);

        } catch (Exception e) {
            // TODO: handle exception
            i = 30;
        }

        sb.setProgress(i);
        sb.setMax(30);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub

            }
        });

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title)
                        // 添加输入的文本框
                .setView(sb)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //	((TextView) v).setText(Integer.toString(sb.getProgress()));

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }


    public void showEditDialog(String title, String edit, final View v) {
        final EditText et = new EditText(this);

        et.setText(edit);

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title).
                // 添加输入的文本框
                        setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        ((TextView) v).setText(et.getText());

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }

    public void showTxtDialog(String title, String edit) {

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title).setMessage(edit)
                // 添加输入的文本
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        arg0.dismiss();
                    }
                })
                .create().show();

    }


    public void setMenuToSetting() {
        setting.setCompany(tv_Company_info.getText().toString());
        setting.setNode(tv_Node_info.getText().toString());
        setting.setWorkName(tv_WorkName_info.getText().toString());
        setting.setUrl(tv_Url_info.getText().toString());
        setting.setTelephone(tv_Telephone_info.getText().toString());
        setting.setLat_Lon(tv_Lat_Lon_info.getText().toString());
        setting.setNewData(tv_NewData_info.getText().toString());

        setting.setPower(tv_power_info.getText().toString());
        setting.setLogDeadline(tv_LogDeadline_info.getText().toString());
        setting.setCCTime(tv_CCTime_info.getText().toString());
        setting.setScanStrategy(tv_ScanStrategy_info.getText().toString());
        setting.setAgainDeadline(tv_AgainDeadline_info.getText().toString());
        setting.setVesion(tv_VersionNo_info.getText().toString());
        setting.setScanInterval(tv_ScanInterval_info.getText().toString());

        System.out.println(setting.getCompany()+","+setting.getNode()+","+setting.getWorkName()+","+setting.getUrl()+","+setting.getTelephone()
        +","+setting.getLat_Lon()+","+setting.getNewData()+","+setting.getPower()+","+setting.getLogDeadline()+","+setting.getCCTime()+","+setting.getScanStrategy()
        +","+setting.getAgainDeadline()+","+setting.getScanInterval());
    }

    public void insertDataBase() {
        DBOperation dboperation = new DBOperation(this);
        dboperation.deleteSet();
//        this.deleteDatabase("goss.db");
        dboperation.insertSet(setting);
    }

    //点击回退按钮
//    public void back(View view){
//        setMenuToSetting();
//        insertDataBase();
//        resultCode= 9;
//
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("Setting", (Serializable) setting);
//        SettingActivity.this.setResult(resultCode, resultIntent);
//        finish();
//    }

}
