package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.Entity.Login;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/1 0001.
 */
public class LoginActivity extends Activity {
    public static LoginActivity loginActivity;
    private EditText edit_username,edit_password;
    public Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //
        loginActivity=this;
        initView();
    }

    private void initView() {
        edit_password= (EditText) findViewById(R.id.edit_password);
        edit_username= (EditText) findViewById(R.id.edit_username);
    }

    public void back(View view){
        finish();
    }

    public void login(View view){
        RequestParams params=new RequestParams();
        params.put("index","2");
        params.put("tablename","user");
        params.put("parameter","username");
        params.put("parameter1",edit_username.getText().toString());
        HttpNetworkRequest.get("query", params, new BaseHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                try {
                    JSONArray jsonArray=new JSONArray(rawResponse);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String password= (String) jsonObject.get("password");
                    String username=(String) jsonObject.get("username");
                    String state=(String) jsonObject.get("state");
                    login=new Login();
                    login.setPassword(password);
                    login.setUsername(username);
                    login.setState(state);
                    if (edit_password.getText().toString().equals(password)){
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        HomeActivity.loginState="1";//更改登录的状态
                        startActivity(new Intent(LoginActivity.this, LoginStateActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
