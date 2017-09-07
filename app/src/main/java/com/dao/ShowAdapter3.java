package com.dao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjfd.chenxiao.DHL.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowAdapter3 extends BaseAdapter {

    private Context mcontext;
    private List<HashMap<String,String>> mList = new ArrayList<HashMap<String,String>>();
    private LayoutInflater minflater;


    public ShowAdapter3(Context context, List<HashMap<String, String>> List) {
        this.mcontext = context;
        minflater = LayoutInflater.from(context);
        this.mList = List;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (null != mList) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (null != mList) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHold viewHold;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = minflater.inflate(R.layout.show3, null);
            viewHold.tv_barCode = (TextView) convertView.findViewById(R.id.tv_barCode);//条码
            viewHold.tv_epc = (TextView) convertView.findViewById(R.id.tv_epc);//epc

            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();

        }
        viewHold.tv_barCode.setText(mList.get(position).get("content1"));
        viewHold.tv_epc.setText(mList.get(position).get("content2"));
        return convertView;
    }

    private final static class ViewHold {
        TextView tv_barCode;
        TextView tv_epc;
    }

}
