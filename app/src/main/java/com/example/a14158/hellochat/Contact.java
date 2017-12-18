package com.example.a14158.hellochat;

import org.litepal.crud.DataSupport;

/**
 * Created by 14158 on 2017/10/31.
 */

public class Contact extends DataSupport{
    private String name;
    private int imageId;
    public Contact(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public void setName(String name){
        this.name = name;
    }
    public  void setImageId(int imageId){
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
