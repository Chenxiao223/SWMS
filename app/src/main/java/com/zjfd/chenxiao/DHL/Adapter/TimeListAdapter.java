package com.zjfd.chenxiao.DHL.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjfd.chenxiao.DHL.Entity.Timebean;
import com.zjfd.chenxiao.DHL.R;

import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */
public class TimeListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Timebean>timebeans;

    public TimeListAdapter(Context context,  List<Timebean> timebeans) {
        this.context = context;
        this.timebeans = timebeans;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return timebeans.size();
    }

    @Override
    public Object getItem(int position) {
        return timebeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, null);
            viewHold = new ViewHolder();
            viewHold.CJtime= (TextView) convertView.findViewById(R.id.CJtime);
            viewHold.SCtime= (TextView) convertView.findViewById(R.id.SCtime);
            convertView.setTag(viewHold);
        }else {
            viewHold= (ViewHolder) convertView.getTag();
        }
        Timebean mTimebean=timebeans.get(position);
        viewHold.CJtime.setText(mTimebean.getCJtime());
        viewHold.SCtime.setText(mTimebean.getSCtime());
        return convertView;
    }

    public  static class ViewHolder{
    private TextView CJtime,SCtime;
    }

}
