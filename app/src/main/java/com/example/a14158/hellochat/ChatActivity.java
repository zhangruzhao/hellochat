package com.example.a14158.hellochat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.a14158.hellochat.PhotoActivity.PHOTO_OK;
//import java.util.jar.Manifest;

public class ChatActivity extends BaseActivity {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send,add;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private MsgDatabaseHelper dbHelper;
    private String c_name;
    private String outputImage_path;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        c_name = getIntent().getStringExtra("name");//获得联系人的昵称
        TextView name_title = (TextView)findViewById(R.id.hello_chat);
        name_title.setText(c_name);//设置聊天界面头部昵称显示
        dbHelper = new MsgDatabaseHelper(this,"MSG_RECORD.db",null,1);
        initMsgs();

        inputText = (EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send_button);
        add = (Button)findViewById(R.id.add_button);

        msgRecyclerView = (RecyclerView)findViewById(R.id.mag_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(this);
        add.setOnClickListener(this);

        if (getIntent().getIntExtra("photo_ok",0) == PHOTO_OK){//如果相等说明在PhotoActivity中点击了确认发送
            //更新界面的消息列表。显示最新消息
            String photo_path = getIntent().getStringExtra("photo_path");
            Msg msg = new Msg(Msg.PIC_MSG,"",photo_path,Msg.TYPE_SENDED, getImageId("凌晨%七点半"));
            msgList.add(msg);
            adapter.notifyItemInserted(msgList.size()-1);
            msgRecyclerView.scrollToPosition(msgList.size()-1);

            insertMsg(c_name,Msg.PIC_MSG,null,photo_path,getImageId("凌晨%七点半"),Msg.TYPE_SENDED);//在数据库中插入最新的图片消息
        }
    }

    private int getImageId(String c_name) {
        List<Contact> contacts = Contact.where("name == ? ",c_name).find(Contact.class);
        Log.d("ChatActivity","contacts.get(0).getImageId()");
        return contacts.get(0).getImageId();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.send_button:
                        String input = inputText.getText().toString();
                        if(!"".equals(input)){
                            Msg msg = new Msg(Msg.TEXT_MSG,input,"",Msg.TYPE_SENDED, getImageId("凌晨%七点半"));
                            msgList.add(msg);
                            adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时，刷新RecyclerView中的显示
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
                            inputText.setText("");

                            insertMsg(c_name,Msg.TEXT_MSG,input,null,getImageId("凌晨%七点半"),Msg.TYPE_SENDED);
                        }
                        break;
            case R.id.add_button:
                bottomDialog();
                break;
            case R.id.camera_button:
                takephoto();
                break;
            case R.id.picture_button:
                photoAlbum();
                break;
            default:
                break;
        }
    }
    private void bottomDialog(){//显示底部菜单
        Dialog dialog = new Dialog(this,R.style.Theme_AppCompat);
        LinearLayout BottomDialog = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog,null);
        dialog.setContentView(BottomDialog);
        BottomDialog.findViewById(R.id.camera_button).setOnClickListener((View.OnClickListener) this);
        BottomDialog.findViewById(R.id.picture_button).setOnClickListener((View.OnClickListener) this);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度设置为屏幕的宽度
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;//高度匹配底部菜单LinearLayout的高度
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
    private void takephoto(){//启动照相机
        File outputImage = new File(getExternalCacheDir(),"output_image" + date() + ".jpg");
        outputImage_path = outputImage.getPath();//获取照相机所拍照片的存放路径，方便存放到数据库中
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){//将路径转换为URI
            imageUri = FileProvider.getUriForFile(this,"com.example.cameraalbumtest.fileprovider",outputImage);
        }else imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//将uri作为相机拍照结果的存放路径
        startActivityForResult(intent,TAKE_PHOTO);
    }
    private void photoAlbum(){
        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ChatActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent,CHOOSE_PHOTO);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,CHOOSE_PHOTO);
                }else
                    Toast.makeText(ChatActivity.this,"YOU DENIED THE PERMISSION",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = adapter.getPosition();
        switch (item.getItemId()){
            case R.id.save_picture:
                Msg msg = msgList.get(position);
                String imagePath = msg.getPhoto_path();
                saveBitmap(imagePath);
                break;
            case R.id.delete:
                deleteMsg(position);
                msgList.remove(position);
                adapter.notifyDataSetChanged();
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//调用相机和相册的结果
        switch (requestCode){
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK){
                        Intent intent = new Intent(ChatActivity.this,PhotoActivity.class);
                        intent.putExtra("imageUri",imageUri.toString());//将Uri转为String，传递
                        intent.putExtra("outputImage_path",outputImage_path);
                        intent.putExtra("flag",TAKE_PHOTO);
                        intent.putExtra("name",c_name);
                        startActivity(intent);
                        finish();
                    }
                    break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else
                        handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri  = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private void displayImage(String imagePath) {
        if (imagePath != null){
            Intent intent = new Intent(ChatActivity.this,PhotoActivity.class);
            intent.putExtra("imagePath",imagePath);
            intent.putExtra("name",c_name);
            intent.putExtra("flag",CHOOSE_PHOTO);
            startActivity(intent);
            finish();
        }else
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private  void initMsgs(){//初始化消息记录
        Msg msg1 = new Msg(Msg.TEXT_MSG,"你好","",Msg.TYPE_RECEIVED,getImageId(c_name));
        msgList.add(msg1);
        Msg msg2 = new Msg(Msg.TEXT_MSG,"你好……","",Msg.TYPE_SENDED, getImageId("凌晨%七点半"));
        msgList.add(msg2);
        //insertMsg(c_name,Msg.TEXT_MSG,"你好，GG？","",getImageId(c_name),Msg.TYPE_RECEIVED);
        //insertMsg(c_name,Msg.TEXT_MSG,"是的，你呢？","",getImageId("凌晨%七点半"),Msg.TYPE_SENDED);
        //insertMsg(c_name,Msg.TEXT_MSG,"MM……","",getImageId(c_name),Msg.TYPE_RECEIVED);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询name列的值为c_name的数据，cursor中的数据是返回所有满足条件的数据
        Cursor cursor = db.query("MSG",null,"name = ?",new String[]{ c_name },null,null,null);
        if (cursor.moveToFirst()){//遍历每一条满足条件的数据，并添加到msglist消息队列中
            do {
                int Msg_Type = cursor.getInt(cursor.getColumnIndex("Msg_Type"));//消息类型图片或者文本
                String content = cursor.getString(cursor.getColumnIndex("content"));//文本内容
                String photo_path = cursor.getString(cursor.getColumnIndex("photo_path"));//图片路径
                int type = cursor.getInt(cursor.getColumnIndex("type"));//消息类型发送或者接收
                int imageId  = cursor.getInt(cursor.getColumnIndex("imageId"));//联系人的头像
                Msg msg0 = new Msg(Msg_Type,content,photo_path,type,imageId);
                msgList.add(msg0);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
    private void deleteMsg(int position){
        Msg msg = msgList.get(position);//position为当前聊天窗口的recyclerview中的item位置
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (msg.getMSG_TYPE() == Msg.PIC_MSG){//如果是图片，选择图片路径作为删除条件
            db.delete("MSG","photo_path = ?",new String[]{msg.getPhoto_path()});
        }else if(msg.getMSG_TYPE() == Msg.TEXT_MSG){//如果是文本，选择文本内容为删除条件
            db.delete("MSG","content = ?",new String[]{msg.getContent()});
        }

        /*msgList.remove(position);
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);*/
    }
    private void insertMsg(String name,int Msg_Type,String content,String photo_path,int imageId,int type){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("Msg_Type",Msg_Type);
        values.put("content",content);
        values.put("photo_path",photo_path);
        values.put("imageId",imageId);
        values.put("type",type);
        db.insert("MSG",null,values);
        values.clear();
    }
    private String date(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());
        return date;
    }
    private void saveBitmap(String imagePath){
        File dir = null;
        File file = null;
        FileOutputStream outputStream = null;
        //dir = getExternalFilesDir(null);
        //file = new File(dir,date());
        file = new File("/sdcard/DCIM/"+ date() + ".jpeg");
        /*if (!file.getParentFile().exists()){
            if (file.getParentFile().mkdirs()){
                try {
                    if (file.createNewFile()){
                        Toast.makeText(this,"created",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
                Toast.makeText(this,"created failed",Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this,"existed",Toast.LENGTH_SHORT).show();*/
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this,"saved",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
