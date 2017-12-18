package com.example.a14158.hellochat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by 14158 on 2017/11/28.
 */

public class MsgDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private final static String CREATE_MSG = "create table MSG ("
            +"Msg_Type integer,"//消息的类型，分为图片和文字消息
            +"content text,"//文字消息的内容
            +"photo_path text,"//图片的路径
            +"imageId integer,"//发送消息的人的头像id
            +"name text,"//聊天窗口头部显示的联系人昵称
            +"type integer)";//消息的发送或接收的两个类型
    public MsgDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
