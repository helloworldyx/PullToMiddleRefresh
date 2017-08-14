package com.nope.hyx.pulltomiddlerefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by huangyuxiang on 2017/8/14.
 */

public class MyAdapter extends BaseAdapter {
    private Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 80;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item , null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(String.valueOf(position));
        return convertView;
    }

    private class ViewHolder{
        TextView tv;
        ViewHolder(View view){
            tv = (TextView) view.findViewById(R.id.tv);
        }
    }
}
