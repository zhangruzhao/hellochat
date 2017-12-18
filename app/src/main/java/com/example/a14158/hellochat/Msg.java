package com.example.a14158.hellochat;
/**
 * Created by 14158 on 2017/10/31.
 */
public class Msg {
    private int MSG_TYPE;
    private int type;
    private String content;
    private String photo_path;
    private int imgId;
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENDED = 1;
    public static final int TEXT_MSG = 0;
    public static final int PIC_MSG = 1;
    public Msg(int MSG_TYPE,String content,String photo_path,int type,int imgId){
        this.MSG_TYPE = MSG_TYPE;
        this.content = content;
        this.photo_path = photo_path;
        this.type = type;
        this.imgId = imgId;
    }
    public int getImgId(){
        return imgId;
    }
    public int getType(){
        return type;
    }
    public String getContent(){
        return content;
    }
    public int getMSG_TYPE(){return MSG_TYPE;}
    public String getPhoto_path(){return photo_path;}
}


