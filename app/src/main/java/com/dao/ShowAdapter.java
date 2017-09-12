package com.dao;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zjfd.chenxiao.DHL.R;

import java.util.ArrayList;
import java.util.List;

public class ShowAdapter extends BaseAdapter {

	private Context mcontext;
	private List<ShowInfo> mList = new ArrayList<ShowInfo>();
	private LayoutInflater minflater;
	private int flag=-1;
	
	
	public ShowAdapter(Context context, List<ShowInfo> List) {
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
		ViewHold  viewHold;
		if(convertView == null)
		{
			viewHold = new ViewHold();
			convertView = minflater.inflate(R.layout.show, null);
			viewHold.tvfullcode =(TextView) convertView.findViewById(R.id.tv_showfullcode);//全码
			viewHold.tvtime =(TextView) convertView.findViewById(R.id.tv_showscantime);//时间
			viewHold.tvfullcode2 =(TextView) convertView.findViewById(R.id.tv_showfullcode2);//全码
			viewHold.tvtime2 =(TextView) convertView.findViewById(R.id.tv_showscantime2);//时间
			if (position==flag){
				viewHold.tvfullcode2.setTextColor(Color.parseColor("#7ac671"));
				viewHold.tvtime2.setTextColor(Color.parseColor("#7ac671"));
			}
			convertView.setTag(viewHold);
		}
		else{
			viewHold = (ViewHold) convertView.getTag();
		}
		viewHold.tvfullcode.setText(mList.get(position).getEpc());
		viewHold.tvtime.setText(mList.get(position).getTime());
		viewHold.tvfullcode2.setText(mList.get(position).getEpc2());
		viewHold.tvtime2.setText(mList.get(position).getTime2());
		if (mList.get(position).getFlag().equals("1")){
			viewHold.tvfullcode2.setTextColor(Color.parseColor("#7ac671"));
			viewHold.tvtime2.setTextColor(Color.parseColor("#7ac671"));
		}
		return convertView;
	}
	
	private final static class ViewHold{
		TextView tvfullcode;
		TextView tvtime;
		TextView tvfullcode2;
		TextView tvtime2;
	}

	public void changeColor(int i){
		flag=i;
	}

}
