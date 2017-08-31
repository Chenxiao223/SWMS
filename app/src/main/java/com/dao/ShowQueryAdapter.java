package com.dao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zjfd.chenxiao.DHL.R;

import java.util.ArrayList;
import java.util.List;


public class ShowQueryAdapter extends BaseAdapter {

	private Context mcontext;
	private List<String> mList = new ArrayList<String>();
	private LayoutInflater minflater;

	
	public ShowQueryAdapter(Context context, List<String> List) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHold  viewHold;
		if(convertView == null)
		{
			viewHold = new ViewHold();
			convertView = minflater.inflate(R.layout.show_query, null);
			viewHold.tv_EpcCode =(TextView) convertView.findViewById(R.id.tv_EpcCode);
			viewHold.btn_Query =(Button) convertView.findViewById(R.id.btn_Query);
//			viewHold.iamgegap =(TextView) convertView.findViewById(R.id.image_gap);
			convertView.setTag(viewHold);
		}
		else{
			viewHold = (ViewHold) convertView.getTag();
			
		}
		viewHold.tv_EpcCode.setText(mList.get(position));
		viewHold.btn_Query.setTag(mList.get(position));
		
		
		viewHold.btn_Query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				showCheckResult(MainQueryActivity.setting.getProxyServer(),MainQueryActivity.setting.getDBIP(),MainQueryActivity.setting.getPort(),MainQueryActivity.setting.getExternalCode(), mList.get(position));
			}
		});
		
		return convertView;
	}
	
	private final static class ViewHold{
//		TextView iamgegap;
		TextView tv_EpcCode;
		Button btn_Query;
		
	}
	
	
	private void showCheckResult(String proxyserver,String url,String port,String externalcode,String epccode )
	{
		
//		if(MainQueryActivity.btn_Query_scan.getText().toString().equals("停止扫描"))
//		{
//			Toast.makeText(mcontext, "请停止扫描", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		Intent intent = new Intent();
//		intent.setClass(mcontext,ResultActivity.class);
//		intent.putExtra("proxyserver", proxyserver);
//		intent.putExtra("url", url);
//		intent.putExtra("port", port);
//		intent.putExtra("externalcode", externalcode);
//		intent.putExtra("epccode", epccode);
//		mcontext.startActivity(intent);
		 

	}
	
	



}
