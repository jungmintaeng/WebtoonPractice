package com.jungmin.realwebtoonprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 신정민 on 2017-01-01.
 */
public class ToonListAdapter extends BaseAdapter {
    private ArrayList<ToonList> toonLists;
    private Context mContext;
    private LayoutInflater inflater;

    public ToonListAdapter(Context mContext, ArrayList<ToonList> toonLists){
        this.toonLists = toonLists;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    private class ViewHolder{ImageView image; TextView subTitle, star, updateDate;}

    @Override
    public int getCount() {
        return toonLists.size();
    }

    @Override
    public Object getItem(int position) {
        return toonLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if(view == null){
            view = inflater.inflate(R.layout.toonlist_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView)view.findViewById(R.id.toonlist_thumb);//썸네일
            holder.subTitle = (TextView)view.findViewById(R.id.toonlist_subtitle);//제목
            holder.updateDate = (TextView)view.findViewById(R.id.toonlist_updateDate);//작가
            holder.star = (TextView)view.findViewById(R.id.toonlist_star);//평점
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        try{
            ToonList toonList = toonLists.get(position);
            holder.image.setImageBitmap(toonList.getThumbnail());
            holder.subTitle.setText(toonList.getSubTitle());
            holder.star.setText(String.valueOf(toonList.getStar()));
            holder.updateDate.setText(toonList.getUpdateDate());
        }catch (IndexOutOfBoundsException e){
        }
        return view;
    }
}
