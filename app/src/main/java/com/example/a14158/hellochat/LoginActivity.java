package com.example.a14158.hellochat;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private AccessibilityService context;
    private List<Contact> contactList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        TextView title = (TextView)findViewById(R.id.hello_chat);
        title.setText("Hello Chat");

        initContacts();
        ContactAdapter adapter = new ContactAdapter(LoginActivity.this,R.layout.contacts,contactList);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactList.get(position);//获取所点击好友的对象
                String name = contact.getName();//获取昵称
                int img_con_head = contact.getImageId();//获取头像
                Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("img_con_head",img_con_head);//将头像和昵称通过intent传给下一个活动chatActivity
                startActivity(intent);
            }
        });
    }
    @SuppressWarnings("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initContacts(){
        Contact con_1 = new Contact("0℃的浪漫",R.drawable.im_01);
        contactList.add(con_1);
        Contact con_2 = new Contact("蓝色生死恋",R.drawable.im_02);
        contactList.add(con_2);
        Contact con_3 = new Contact("阳光男孩",R.drawable.im_03);
        contactList.add(con_3);
        Contact con_4 = new Contact("只对你有感觉",R.drawable.im_04);
        contactList.add(con_4);
        Contact con_5 = new Contact("天天开心",R.drawable.im_05);
        contactList.add(con_5);
        Contact con_6 = new Contact("Cool_Boy",R.drawable.im_06);
        contactList.add(con_6);
        Contact con_7 = new Contact("追风少女",R.drawable.im_07);
        contactList.add(con_7);
        Contact con_8 = new Contact("抬头仰望·星空",R.drawable.im_08);
        contactList.add(con_8);
        Contact con_9 = new Contact("爺~",R.drawable.im_09);
        contactList.add(con_9);
        Contact con_10 = new Contact("追风男孩",R.drawable.im_10);
        contactList.add(con_10);
        Contact con_11 = new Contact("~漏芯",R.drawable.im_11);
        contactList.add(con_11);
        Contact con_12 = new Contact("寒夜~孤心",R.drawable.im_12);
        contactList.add(con_12);
        Contact con_13 = new Contact("Steven Chen",R.drawable.im_13);
        contactList.add(con_13);
        Contact con_14 = new Contact("轻舞飞扬",R.drawable.im_14);
        contactList.add(con_14);
        Contact con_15 = new Contact("凌晨%七点半",R.drawable.im_15);
        contactList.add(con_15);

        for (Contact contact:contactList){
            /*
            InputStream is = getResources().openRawResource(R.drawable.login);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),contact.getImageId());
            File file = new File("/sdcard/hellochat/"+ contact.getName() + ".png");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                if (!file.exists()){
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                }else
                    Log.d("LoginActivity",file.getPath());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.reset();
            } catch (IOException e) {
            }
            */
            //String c_head_imagePath = file.getPath();
            insertContact(contact.getName(),contact.getImageId());
        }
    }
    private void insertContact(String c_name,int imageId){
        Contact contact_db = new Contact(c_name,imageId);
        contact_db.setName(c_name);
        contact_db.setImageId(imageId);
        contact_db.save();
    }

}
