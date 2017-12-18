package com.example.a14158.hellochat;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
/**
 * Created by 14158 on 2017/10/31.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    private int resourceId;
    public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView contact_head = (ImageView)view.findViewById(R.id.contact_head);
        TextView contact_name = (TextView)view.findViewById(R.id.contact_name);
        contact_head.setImageResource(contact.getImageId());
        contact_name.setText(contact.getName());
        return view;
    }
}
