package com.example.a14158.hellochat;

import org.litepal.crud.DataSupport;

/**
 * Created by 14158 on 2017/10/31.
 */

public class Contact extends DataSupport{
    private String name;
    private int imageId;
    private int IsChatting;
    private String group;
    public static int Chatting = 1;
    public static int notChatting = 0;

    public void setGroup(String group){this.group = group;}
    public void setIsChatting(int status){this.IsChatting = status;}
    public void setName(String name){
        this.name = name;
    }
    public  void setImageId(int imageId){
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public String getGroup(){return group;}
    public int getImageId(){
        return imageId;
    }
    public int getIsChatting(){return IsChatting;}

}
