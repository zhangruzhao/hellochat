package com.example.a14158.hellochat;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private AccessibilityService context;
    private Map<String,List<Contact>> map;
    private List<String> parents = new ArrayList<>();
    private ContactExpandableListAdapter adapter;
    private final static int GROUP_RENAME = 0;
    private final static int GROUP_DELETE = 1;
    private final static int GROUP_ADD = 2;
    private final static int CHILD_DELETE = 3;
    private final static int CHILD_MOVETO = 4;

    private int which;
    private void setwhich(int which){
        this.which = which;
    }
    private int getwhich(){
        return which;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar_login = (Toolbar)findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_login.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initContacts();
        final ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);
        adapter = new ContactExpandableListAdapter(this,map,parents);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Contact contact = map.get(parents.get(groupPosition)).get(childPosition);
                String name = contact.getName();//获取昵称
                Log.d("LoginActivity",name);
                ChangeChatting(name);
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                startActivity(intent);
                return false;
            }
        });
        //注册上下文菜单
        registerForContextMenu(expandableListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
            menu.add(0,GROUP_RENAME,0,"重命名");
            menu.add(0,GROUP_DELETE,0,"删除");
            menu.add(0,GROUP_ADD,0,"添加分组");
            menu.setHeaderTitle("分组管理");
        }else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
            menu.add(0,CHILD_DELETE,0,"删除");
            menu.add(0,CHILD_MOVETO,0,"移动");
            menu.setHeaderTitle("联系人管理");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case GROUP_DELETE:
                ExpandableListView.ExpandableListContextMenuInfo info_1 = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
                int group_position_1 = ExpandableListView.getPackedPositionGroup(info_1.packedPosition);
                if (map.get(parents.get(group_position_1)).isEmpty()) {
                    //删除数据库中的分组
                    DataSupport.deleteAll(ContactGroup.class,"name = ?",parents.get(group_position_1));

                    parents.remove(group_position_1);
                    adapter.notifyDataSetChanged();

                }else
                    Toast.makeText(LoginActivity.this,"分组不为空不能删除",Toast.LENGTH_SHORT).show();
                break;
            case GROUP_RENAME:
                ExpandableListView.ExpandableListContextMenuInfo info_0 = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
                final int group_position_0 = ExpandableListView.getPackedPositionGroup(info_0.packedPosition);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("重命名");
                final EditText editText = new EditText(this);
                alertDialog.setView(editText);
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_name = editText.getText().toString();
                        parents.set(group_position_0,new_name);
                        adapter.notifyDataSetChanged();
                        //更改数据库的分组名
                        ContactGroup contactGroup = new ContactGroup();
                        contactGroup.setName(new_name);
                        contactGroup.updateAll("name = ?",parents.get(group_position_0));
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                break;
            case GROUP_ADD:
                AlertDialog.Builder dialog_add = new AlertDialog.Builder(this);
                dialog_add.setTitle("新建分组");
                dialog_add.setCancelable(true);
                final EditText editText_add = new EditText(this);
                dialog_add.setView(editText_add);
                dialog_add.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_group = editText_add.getText().toString();
                        parents.add(new_group);
                        map.put(parents.get(parents.size()-1),new ArrayList<Contact>());
                        adapter.notifyDataSetChanged();
                        //在数据库中添加分组
                        ContactGroup contactGroup = new ContactGroup();
                        contactGroup.setName(new_group);
                        contactGroup.save();
                    }
                });
                dialog_add.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog_add.show();
                break;
            case CHILD_DELETE:
                ExpandableListView.ExpandableListContextMenuInfo info_delete_contact = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
                final int child_position = ExpandableListView.getPackedPositionChild(info_delete_contact.packedPosition);
                final int group_position = ExpandableListView.getPackedPositionGroup(info_delete_contact.packedPosition);
                //在数据库中删除联系人
                final String c_name_del = map.get(parents.get(group_position)).get(child_position).getName();
                DataSupport.deleteAll(Contact.class,"name = ?",c_name_del);

                map.get(parents.get(group_position)).remove(child_position);
                adapter.notifyDataSetChanged();

                break;
            case CHILD_MOVETO:
                ExpandableListView.ExpandableListContextMenuInfo info_move_contact = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
                final int child_position_move= ExpandableListView.getPackedPositionChild(info_move_contact.packedPosition);
                final int group_position_move = ExpandableListView.getPackedPositionGroup(info_move_contact.packedPosition);
                final AlertDialog.Builder dialog_move_child = new AlertDialog.Builder(this);
                //final boolean[] dialog_move_child_selected_list = new boolean[parents.size()];
                final Contact con = map.get(parents.get(group_position_move)).get(child_position_move);
                dialog_move_child.setTitle("移动到分组");
                dialog_move_child.setSingleChoiceItems(parents.toArray(new String[parents.size()]),-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setwhich(which);
                    }
                });
                dialog_move_child.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = getwhich();
                        Contact contact = new Contact();
                        contact.setGroup(parents.get(position));
                        contact.updateAll("name = ?",con.getName());
                        map.get(parents.get(position)).add(con);
                        map.get(parents.get(group_position_move)).remove(child_position_move);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog_move_child.setCancelable(true);
                /*dialog_move_child.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Contact con = map.get(parents.get(group_position_move)).get(child_position_move);
                        for (int i = 0 ;i < dialog_move_child_selected_list.length ; i++){
                            if (dialog_move_child_selected_list[i] == true){
                                map.get(parents.get(i)).add(con);
                                //数据库中修改
                                Contact contact = new Contact();
                                contact.setName(con.getName());
                                contact.setGroup(parents.get(i));
                                contact.setImageId(con.getImageId());
                                contact.setIsChatting(con.getIsChatting());
                                contact.save();
                            }
                        }
                        map.get(parents.get(group_position_move)).remove(child_position_move);
                        adapter.notifyDataSetChanged();

                        DataSupport.deleteAll(Contact.class,"name = ? and group = ?",con.getName(),con.getGroup());
                    }
                });*/
                dialog_move_child.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog_move_child.show();
        }
        return super.onContextItemSelected(item);
    }

    //更新CONTACT表中状态列
    private void ChangeChatting(String name) {
        Contact contact1 = new Contact();
        //记住恢复默认值需要settodefult
        contact1.setToDefault("IsChatting");
        contact1.updateAll();

        Contact contact = new Contact();
        contact.setIsChatting(1);
        contact.updateAll("name = ?", name);

    }

    @SuppressWarnings("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initContacts() {
        //List<ContactGroup> contactGroups = DataSupport.findAll(ContactGroup.class);
        while (DataSupport.findAll(ContactGroup.class).isEmpty()) {
            ContactGroup contactGroup = new ContactGroup();
            contactGroup.setName("同事");
            contactGroup.save();
            ContactGroup contactGroup1 = new ContactGroup();
            contactGroup1.setName("家人");
            contactGroup1.save();
            ContactGroup contactGroup2 = new ContactGroup();
            contactGroup2.setName("朋友");
            contactGroup2.save();
        };
        List<ContactGroup> groups1 = DataSupport.findAll(ContactGroup.class);
        //parents.add("同事");
        //parents.add("家人");
        //parents.add("朋友");
        List<List<Contact>> lists = new ArrayList<List<Contact>>();
        //List<Contact> list2 = new ArrayList<Contact>();
        //List<Contact> list3 = new ArrayList<Contact>();
        map = new HashMap<String, List<Contact>>();
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
        List<Contact> contacts1 = DataSupport.findAll(Contact.class);
        for (int i = 0 ;i < groups1.size(); i++){
            List<Contact> list = new ArrayList<Contact>();
            for (Contact contact : contacts1) {
                if (contact.getGroup().equals(groups1.get(i).getName())){
                    list.add(contact);
                }
            }
            lists.add(i,list);
            //if (contact.getGroup().equals("同事")){
            //   list1.add(contact);
            //}else if (contact.getGroup().equals("家人")){
            //    list2.add(contact);
            //}else if (contact.getGroup().equals("朋友")){
            //    list3.add(contact);
            //}else
            //    Toast.makeText(LoginActivity.this,"找不到对应的组",Toast.LENGTH_SHORT).show();
            //map.put(parents.get(0),list1);
            //map.put(parents.get(1),list2);
            //map.put(parents.get(2),list3);
        }
        for (int i = 0 ;i < groups1.size(); i++) {
            map.put(groups1.get(i).getName(), lists.get(i));
            parents.add(i, groups1.get(i).getName());

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