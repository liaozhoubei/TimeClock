package com.example.bert.timeclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bert on 2/11/2016.
 */
public class TimeAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mLayoutInflater;
    private ArrayList<Record> mList;

    private String[] mNames = {"Alice", "peter"};

    public TimeAdapter(LayoutInflater inflater, ArrayList<Record> list) {
        mLayoutInflater = inflater;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_time_record, null);
            viewHolder = new ViewHolder();
            viewHolder.num = (TextView) convertView.findViewById(R.id.item_time_id);
            viewHolder.timeRecord = (TextView) convertView.findViewById(R.id.item_time_record);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Record record = mList.get(position);
        String id = "#"+ record.id + "    ";
        viewHolder.num.setText(id);
        viewHolder.timeRecord.setText(record.recordTime);

        return convertView;
    }

    public class ViewHolder {
        public TextView num;
        public TextView timeRecord;
    }
}
