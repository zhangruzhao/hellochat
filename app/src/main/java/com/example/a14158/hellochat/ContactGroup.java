package com.example.a14158.hellochat;

import org.litepal.crud.DataSupport;

/**
 * Created by 14158 on 2017/12/21.
 */

public class ContactGroup extends DataSupport {
    private String name;
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
