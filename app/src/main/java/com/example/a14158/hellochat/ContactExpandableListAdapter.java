package com.example.a14158.hellochat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by 14158 on 2017/12/20.
 */

public class ContactExpandableListAdapter extends BaseExpandableListAdapter {
    private Map<String,List<Contact>> mMap;
    private Context mContext;
    private List<String> mParents;
    public ContactExpandableListAdapter(Context context,Map<String,List<Contact>> map,List<String> parents){
        mContext = context;
        mMap = map;
        mParents = parents;
    }

    @Override
    public int getGroupCount() {
        return mParents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String key = mParents.get(groupPosition);
        int size = mMap.get(key).size();
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        String key = mParents.get(groupPosition);
        return mMap.get(key);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = mParents.get(groupPosition);
        return mMap.get(key).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String parent_name = mParents.get(groupPosition);
        View view = LayoutInflater.from(mContext).inflate(R.layout.group,null);
        TextView group_name = (TextView)view.findViewById(R.id.group_name);
        ImageView group_ic = (ImageView)view.findViewById(R.id.group_ic);
        group_name.setText(parent_name);
        if (isExpanded){
            group_ic.setImageResource(R.mipmap.isexpanding);
        }else
            group_ic.setImageResource(R.mipmap.notexpanding);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Contact contact = mMap.get(mParents.get(groupPosition)).get(childPosition);
        View view = LayoutInflater.from(mContext).inflate(R.layout.child,null);
        ImageView child_ic = (ImageView)view.findViewById(R.id.child_ic);
        TextView child_name = (TextView)view.findViewById(R.id.child_name);
        child_ic.setImageResource(contact.getImageId());
        child_name.setText(contact.getName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
