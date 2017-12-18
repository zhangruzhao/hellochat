package com.example.a14158.hellochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private EditText editText_1,editText_2;
    private ProgressBar progressBar;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass,autoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_1 = (EditText)findViewById(R.id.edit_username);
        editText_2 = (EditText)findViewById(R.id.edit_password);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass  = (CheckBox)findViewById(R.id.remember_pass);
        autoLogin = (CheckBox)findViewById(R.id.auto_login);
        int progress = progressBar.getProgress();
        progress = 0;
        progressBar.setProgress(progress);
        Button login = (Button)findViewById(R.id.login_button);
        //editText_1.setText(load());

        //从sharedPreferences对象pref中提取上一次设定的登录信息
        boolean isRemember = pref.getBoolean("remember_pass",false);//记住密码选项
        boolean auto_Login = pref.getBoolean("auto_login",false);//自动登录选项
        editText_1.setText(pref.getString("name",""));//登录账户默认为上一次登录成功的账户
        if (isRemember){//如果上一次选择了记住密码，则自动填充密码，并保持上一次记住密码的选择
            editText_2.setText(pref.getString("password",""));
            rememberPass.setChecked(true);
            if (auto_Login){//如果也选择了自动登录，则自动跳转到进入主页
                autoLogin.setChecked(true);
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText_1.getText().toString();//获取用户名的输入信息
                String password = editText_2.getText().toString();//获取密码的输入信息
                if (username.length() != 0 && password.length() != 0 ){ //当用户名和密码输入非空时
                    //save(username);
                    //editor = pref.edit();//打开编辑器
                    //editor.putString("name",username);
                    if (username.equals(pref.getString("name",""))&&password.equals(pref.getString("password",""))) {
                        if (rememberPass.isChecked()) {//如果用户选择记住密码，勾选记住密码选项和保存密码
                            editor = pref.edit();
                            editor.putBoolean("remember_pass", true);
                            editor.putString("password",password);
                            if (autoLogin.isChecked()) {//判断用户时候选择自动登录
                                editor.putBoolean("auto_login", true);
                            } else {
                                editor.putBoolean("auto_login", false);
                            }
                        } else {
                            editor.clear();//clear()只会清楚上一次提交的内容，同一次提交中的操作，
                            // clear()会先执行再执行其他操作，与顺序无关
                        }
                        editor.apply();
                        final int[] lenth = new int[5];
                        int i;
                        for(i = 0,lenth[0]=20;i < 4;i++)
                            lenth[i+1] = lenth[i] + 20;//设置进度条的值，一共5个进度条值，递增值为20
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{//每等待1秒钟增加20进度值
                                    Thread.sleep(1000);
                                    progressBar.setProgress(lenth[0]);
                                    Thread.sleep(1000);
                                    progressBar.setProgress(lenth[1]);
                                    Thread.sleep(1000);
                                    progressBar.setProgress(lenth[2]);
                                    Thread.sleep(1000);
                                    progressBar.setProgress(lenth[3]);
                                    Thread.sleep(1000);
                                    progressBar.setProgress(lenth[4]);
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);//进度为100时启动登录后的界面
                                    startActivity(intent);//启动登陆后界面
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else
                        Toast.makeText(MainActivity.this,"用户不存在或密码不正确",Toast.LENGTH_SHORT).show();

                }
                else //当用户名和密码输入为空时
                    Toast.makeText(MainActivity.this,"please input username and password",Toast.LENGTH_SHORT).show();
            }

        });
        Button register =(Button)findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);//启动注册界面
            }
        });
        intentFilter = new IntentFilter();
        networkChangeReceiver = new NetworkChangeReceiver();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver,intentFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver {//接收系统网络变化广播
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                int type = networkInfo.getType();
                switch (type){
                    case ConnectivityManager.TYPE_MOBILE:
                        Toast.makeText(context,"mobile network is available",Toast.LENGTH_SHORT).show();
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        Toast.makeText(context,"WIFI network is available",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context,"network is unavailable",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    public void save(String data){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("file_data",Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String load(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try{
            in = openFileInput("file_data");
            reader = new BufferedReader(new InputStreamReader(in));
            builder.append(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }*/
}
