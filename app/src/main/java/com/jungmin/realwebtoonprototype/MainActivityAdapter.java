package com.jungmin.realwebtoonprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by 신정민 on 2016-12-31.
 */
public class MainActivityAdapter extends BaseAdapter {
    private ArrayList<Toon> toons;
    private Context mContext;
    private LayoutInflater inflater;
    private int count = 0;

    public MainActivityAdapter(Context mContext, ArrayList<Toon> toons){
        this.mContext = mContext;
        this.toons = toons;
        this.inflater = LayoutInflater.from(mContext);
        this.count = toons.size();
    }

    private class ViewHolder{ImageView image; TextView title, author, star, isCutToon;}

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return toons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if(view == null){
            view = inflater.inflate(R.layout.mainactivity_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView)view.findViewById(R.id.list_thumb);//썸네일
            holder.title = (TextView)view.findViewById(R.id.list_title);//제목
            holder.author = (TextView)view.findViewById(R.id.list_author);//작가
            holder.star = (TextView)view.findViewById(R.id.list_star);//평점
            holder.isCutToon = (TextView)view.findViewById(R.id.list_iscuttoon);//컷툰
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
            holder.isCutToon.setVisibility(View.GONE);//컷툰 글자 일단 안보이게
        }
        try{
            Toon toon = toons.get(position);
            //holder.image.setImageBitmap(toon.getThumbnail());
            Glide.with(mContext)
                    .load(toon.getThumbnail())
                    .placeholder(R.drawable.thumbholder)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.image);
            holder.title.setText(toon.getTitle());
            holder.author.setText(toon.getAuthor());
            holder.star.setText(String.valueOf(toon.getStar()));
            if(toon.isSmartToon())
                holder.isCutToon.setVisibility(View.VISIBLE);   //컷툰이면 컷툰 글자 보이게
        }catch (IndexOutOfBoundsException e){
        }
        return view;
    }

    public void setToons(ArrayList<Toon> toons){
        this.toons = toons;
        count = toons.size();
        notifyDataSetChanged();
    }
}
