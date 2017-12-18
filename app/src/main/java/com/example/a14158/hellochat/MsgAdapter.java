package com.example.a14158.hellochat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 14158 on 2017/11/1.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;
    private int position;
    private Context mContext;
    //可以在ChatActivity中获得被长按的消息的position
    public int getPosition(){
        return position;
    }
    //设置position的值
    public void setPosition(int position){
        this.position = position;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //设置上下文菜单的监听器
        holder.msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //获得被长按的消息的position
                int position = holder.getAdapterPosition();
                setPosition(position);
                Msg msg = mMsgList.get(position);
                //添加上下文菜单的子项
                if (msg.getMSG_TYPE() == Msg.PIC_MSG){
                    menu.add(0,R.id.save_picture,0,"保存图片");
                    menu.add(0,R.id.delete,0,"删除");
                }else if (msg.getMSG_TYPE() == Msg.TEXT_MSG){
                    menu.add(0,R.id.delete,0,"删除");
                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getMSG_TYPE() == Msg.TEXT_MSG){//如果是文本消息，隐藏图片的布局
            holder.pic_leftLayout.setVisibility(View.GONE);
            holder.pic_rightLayout.setVisibility(View.GONE);
            if (msg.getType() == Msg.TYPE_RECEIVED){//接收
                holder.text_leftLayout.setVisibility(View.VISIBLE);
                holder.text_rightLayout.setVisibility(View.GONE);
                holder.leftMsg.setText(msg.getContent());
                holder.text_leftImg.setImageResource(msg.getImgId());
                //Bitmap bitmap = BitmapFactory.decodeFile(msg.getImgId_path());
                //holder.text_leftImg.setImageBitmap(bitmap);
                //holder.text_leftImg.setImageBitmap(getImageHead(msg.getImgId_path()));
            }else if (msg.getType() == Msg.TYPE_SENDED){//发送
                holder.text_leftLayout.setVisibility(View.GONE);
                holder.text_rightLayout.setVisibility(View.VISIBLE);
                holder.rightMsg.setText(msg.getContent());
                holder.text_rightImg.setImageResource(msg.getImgId());
                //holder.text_rightImg.setImageBitmap(getImageHead(msg.getImgId_path()));
            }
        }else if (msg.getMSG_TYPE() == Msg.PIC_MSG){//如果是图片消息，隐藏文本消息的布局
            holder.text_rightLayout.setVisibility(View.GONE);
            holder.text_leftLayout.setVisibility(View.GONE);
            if (msg.getType() == Msg.TYPE_RECEIVED){//接收
                holder.pic_leftLayout.setVisibility(View.VISIBLE);
                holder.pic_rightLayout.setVisibility(View.GONE);
                holder.pic_leftImg.setImageResource(msg.getImgId());
                //holder.pic_leftImg.setImageBitmap(getImageHead(msg.getImgId_path()));
                //显示图片
                holder.leftPic.setImageBitmap(getImageHead(msg.getPhoto_path()));
            }else if (msg.getType() == Msg.TYPE_SENDED){//发送
                holder.pic_leftLayout.setVisibility(View.GONE);
                holder.pic_rightLayout.setVisibility(View.VISIBLE);
                holder.pic_rightImg.setImageResource(msg.getImgId());
                //holder.pic_rightImg.setImageBitmap(getImageHead(msg.getImgId_path()));
                holder.rightPic.setImageBitmap(getImageHead(msg.getPhoto_path()));

            }
        }
    }
    private Bitmap getImageHead(String imageId_path){
        Bitmap bitmap = BitmapFactory.decodeFile(imageId_path);
        return bitmap;
    }
    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View msg_view;
        LinearLayout text_leftLayout;
        LinearLayout text_rightLayout;
        LinearLayout pic_leftLayout;
        LinearLayout pic_rightLayout;
        ImageView leftPic;
        ImageView rightPic;
        TextView leftMsg;
        TextView rightMsg;
        ImageView text_leftImg;
        ImageView text_rightImg;
        ImageView pic_leftImg;
        ImageView pic_rightImg;
        public ViewHolder(View itemView) {
            super(itemView);
            msg_view = itemView;
            text_leftLayout = (LinearLayout)itemView.findViewById(R.id.left_layout_text);
            text_rightLayout = (LinearLayout)itemView.findViewById(R.id.right_layout_text);
            pic_leftLayout = (LinearLayout)itemView.findViewById(R.id.left_layout_picture);
            pic_rightLayout = (LinearLayout)itemView.findViewById(R.id.right_layout_picture);
            leftPic = (ImageView)itemView.findViewById(R.id.left_picture);
            rightPic = (ImageView)itemView.findViewById(R.id.right_picture);
            leftMsg = (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg = (TextView)itemView.findViewById(R.id.right_msg);
            text_leftImg = (ImageView)itemView.findViewById(R.id.img_left_head_text);
            text_rightImg = (ImageView)itemView.findViewById(R.id.img_right_head_text);
            pic_leftImg = (ImageView)itemView.findViewById(R.id.img_left_head_picture);
            pic_rightImg = (ImageView)itemView.findViewById(R.id.img_right_head_picture);
        }
    }
    public MsgAdapter (List<Msg> msglist){
        mMsgList = msglist;
    }
}
