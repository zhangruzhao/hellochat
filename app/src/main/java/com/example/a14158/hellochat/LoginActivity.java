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

import org.litepal.crud.DataSupport;

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
        TextView title = (TextView) findViewById(R.id.hello_chat);
        title.setText("Hello Chat");

        initContacts();
        ContactAdapter adapter = new ContactAdapter(LoginActivity.this, R.layout.contacts, contactList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactList.get(position);//获取所点击好友的对象
                String name = contact.getName();//获取昵称
                ChangeChatting(name);
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
    //更新CONTACT表中状态列
    private void ChangeChatting(String name) {
        Contact contact = new Contact();
        contact.setIsChatting(Contact.Chatting);
        contact.updateAll("name = ?", name);
        Contact contact1 = new Contact();
        contact1.setIsChatting(Contact.notChatting);
        contact1.updateAll("name != ?", name);
    }

    @SuppressWarnings("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initContacts() {
        List<Contact> contacts = DataSupport.findAll(Contact.class);
        if (contacts.isEmpty()) {
            insertContact("0℃的浪漫", R.drawable.im_01,"同事");
            insertContact("蓝色生死恋", R.drawable.im_02,"同事");
            insertContact("阳光男孩", R.drawable.im_03,"同事");
            insertContact("只对你有感觉", R.drawable.im_04,"同事");
            insertContact("天天开心", R.drawable.im_05,"同事");
            insertContact("Cool_Boy", R.drawable.im_06,"家人");
            insertContact("追风少女", R.drawable.im_07,"家人");
            insertContact("抬头仰望·星空", R.drawable.im_08,"家人");
            insertContact("爺~", R.drawable.im_09,"家人");
            insertContact("追风男孩", R.drawable.im_10,"家人");
            insertContact("~漏芯", R.drawable.im_11,"朋友");
            insertContact("寒夜~孤心", R.drawable.im_12,"朋友");
            insertContact("Steven Chen", R.drawable.im_13,"朋友");
            insertContact("轻舞飞扬", R.drawable.im_14,"朋友");
        }
        for (Contact contact : contacts) {
            contactList.add(contact);
        }
    }
    private void insertContact(String c_name, int imageId, String group) {
        Contact contact_db = new Contact();
        contact_db.setName(c_name);
        contact_db.setImageId(imageId);
        contact_db.setGroup(group);
        contact_db.setIsChatting(Contact.notChatting);
        contact_db.save();
    }
}