package com.zjfd.chenxiao.DHL.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Operation;
import com.dao.ShowAdapter;
import com.dao.ShowInfo;
import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.UI.HomeActivity;
import com.zjfd.chenxiao.DHL.UI.ScanActivity;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;
import com.zjfd.chenxiao.DHL.util.MediaUtil;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class RukuFragment extends Fragment {
    public static RukuFragment rukuFragment;
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private static List<ShowInfo> showInfoList = new ArrayList<ShowInfo>();
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter showadapter;
    public static ListView list_view;
    private ImageView iv_scan;
    public static ShowInfo showinfo;
    private int num = 1;//记录上传成功的次数
    MediaUtil mediaUtil = new MediaUtil(HomeActivity.homeActivity);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ruku, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rukuFragment = this;
        initView();
    }

    public void initView() {
        iv_scan = (ImageView) getView().findViewById(R.id.iv_scan);
        tv_readCount = (TextView) getView().findViewById(R.id.tv_readCount);
        tv_uploadCount = (TextView) getView().findViewById(R.id.tv_uploadCount);
        list_view = (ListView) getView().findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter(getActivity(), showInfoList);
        list_view.setAdapter(showadapter);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addShowInfoToList(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        if (TextUtils.isEmpty(epc)) {//为空
            addShowInfoToList(RukuFragment.rukuFragment.getRfid());
        } else {
            writeEpc(epc);
            showinfo.setEpc2(epc);
            showinfo.setTime2(time);
            showinfo.setFlag("0");
            showinfo.setUploadFlag(false);
            upData(showinfo);
            showInfoList.add(showinfo);
            showadapter.notifyDataSetChanged();
            tv_readCount.setText("" + showInfoList.size());
        }
    }

    public void writeEpc(String epc) {
        if (!isLetterDigitOrChinese(epc)) {
            com.dao.Result result = Operation.WriteUnGivenEpc(epc);
        }
    }

    //如果是字母则返回true
    public static boolean isLetterDigitOrChinese(String str) {
        return str.matches("[a-zA-Z]+");
    }

    //扫描获取rfid
    public String getRfid() {
        com.dao.Result result = Operation.readUnGivenEpc((short) 2, (short) 6);
        return result.getReadInfo().toString();
    }

    //数据上传成功
    public void upData(final ShowInfo showinfo) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String time = df.format(new Date());
            RequestParams params = new RequestParams();
            params.put("index", "2");
            params.put("tablename", "warehouse");
            params.put("parameter", showinfo.getEpc());//包裹条码
            params.put("parameter1", time);//包裹rfid
            params.put("parameter2", showinfo.getEpc2());//包裹rfid
            Log.i("rfid", showinfo.getEpc() + "," + showinfo.getEpc2());
            params.put("parameter3", time);//入库时间
            params.put("parameter4", "0");
            HttpNetworkRequest.get("add", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    if (rawResponse.equals("ok")) {
                        showinfo.setFlag("1");
                        showadapter = new ShowAdapter(getActivity(), showInfoList);
                        list_view.setAdapter(showadapter);
                        showadapter.notifyDataSetChanged();
                        tv_uploadCount.setText("" + num++);
                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                        mediaUtil.music(R.raw.success);//语音提示
                    } else {
                        Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                        mediaUtil.music(R.raw.failure);//语音提示
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
        }
    }


}
