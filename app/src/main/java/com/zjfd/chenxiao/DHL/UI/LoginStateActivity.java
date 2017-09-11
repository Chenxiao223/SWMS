package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;

import org.apache.http.Header;

/**
 * Created by Administrator on 2017/9/1 0001.
 */
public class LoginStateActivity extends Activity {
    private TextView loginstate,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_state);
        //
        initView();
    }

    private void initView() {
        username= (TextView) findViewById(R.id.username);
        loginstate= (TextView) findViewById(R.id.loginstate);
        username.setText(LoginActivity.loginActivity.login.getUsername());
        if (HomeActivity.loginState.equals("1")) {
            loginstate.setText("在线状态");
        }
    }

    public void back(View view){
        finish();
    }

    public void backLogin(View view){
        HomeActivity.loginState="0";
        finish();
    }

}
