package com.example.a14158.hellochat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {

    private EditText editText_1,editText_2,editText_3;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar_register = (Toolbar)findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_register.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText_1 = (EditText)findViewById(R.id.edit_username_1);
        editText_2 = (EditText)findViewById(R.id.edit_password_1);
        editText_3 = (EditText)findViewById(R.id.confirm_pass_edit);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        Button confirm = (Button)findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText_1.getText().toString();//获取用户名的输入信息
                String password = editText_2.getText().toString();//获取密码的输入信息
                String confirm_password = editText_3.getText().toString();
                if (username.length() != 0 && password.length() != 0 && confirm_password.length() != 0){//当用户名和密码输入非空时
                    if (password.equals(confirm_password)){ // 比较两次密码输入是否相同，如果相同则注册登录

                        editor = pref.edit();//打开编辑器
                        editor.putString("name",username);
                        editor.putString("password",password);
                        editor.apply();

                        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setTitle("Registering");
                        progressDialog.setMessage("正在注册并登录……");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    Thread.sleep(5000);//显示ProgressDialog 5 秒后关闭并且打开登陆后的界面
                                    // cancel和dismiss方法本质都是一样的，都是从屏幕中删除Dialog,唯一的区别是
                                    // 调用cancel方法会回调DialogInterface.OnCancelListener如果注册的话,dismiss方法不会回掉
                                    progressDialog.dismiss();
                                    //progressDialog.cancel();
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);//启动登陆后界面
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    else{  //如果两次输入不相同则提示重新输入
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                        dialog.setTitle("ERROR!");
                        dialog.setMessage("两次密码不同，请重新输入！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }
                }
                else//当用户名和密码输入为空时
                {
                    //Toast.makeText(RegisterActivity.this,"please input username and password!",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                    dialog.setTitle("EMPTY EARNING!");
                    dialog.setMessage("please enter username and password !");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
            }
        });
        Button cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);//启动主界面
            }
        });
    }

}
