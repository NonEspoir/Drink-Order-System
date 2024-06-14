package com.example.drink_order_system;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{

    private final Context mContext = this;
    private EditText ET_username;
    private  EditText ET_password;

    public void handlePermission() {
        // 检查是否开启 Manifest.permission.xxx
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请求权限成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "正在请求权限",Toast.LENGTH_SHORT).show();
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handlePermission();
        ET_username = findViewById(R.id.et_username);
        ET_password = findViewById(R.id.et_password);
    }

    public void BT_signUp_onClick(View v)
    {
        String username = ET_username.getText().toString();
        System.out.println(username);
        String password = ET_password.getText().toString();

        if(username.equals("")||password.equals(""))
        {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 8)
        {
            Toast.makeText(this, "密码至少8位！", Toast.LENGTH_SHORT).show();
        }
        else if(username.contains("\\")||username.contains("/")||username.contains(":")||username.contains("*")
        || username.contains("?")||username.contains("\"")||username.contains("<")||username.contains(">")
        || username.contains("|"))
        {
            Toast.makeText(this, "用户名中请勿包含\n \\ / : * ? \" < > |等特殊字符(!_!)", Toast.LENGTH_SHORT).show();
        }
        else{
            Account temp = new Account(username, password, mContext);
            Intent intent;
            if(temp.saveAccount())
            {
                Toast.makeText(this, "注册成功！\n 为您直接登录", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, RootActivity.class);
                intent.putExtra("userName", username);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "注册失败。\n该用户名已存在，请尝试其他用户名", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void BT_logIn_onClick(View v)
    {
        String username = ET_username.getText().toString();
        System.out.println(username);
        String password = ET_password.getText().toString();
        if(username.equals("")||password.equals(""))
        {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
        }
        else {
            Account temp = new Account(username, password, mContext);
            Intent intent;
            if (temp.exist()) {
                //System.out.println("success");
                intent = new Intent(MainActivity.this, RootActivity.class);
                intent.putExtra("userName", username);
                startActivity(intent);
            } else {
                //System.out.println("fail");
                Toast.makeText(this, "登录失败。\n用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}