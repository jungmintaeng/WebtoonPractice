package com.jungmin.realwebtoonprototype;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by 신정민 on 2017-01-03.
 */
public class ToonViewerAdapter extends BaseAdapter {
    ArrayList<String> bitmaps;
    AppCompatActivity mContext;
    LayoutInflater inflater = null;
    ViewHolder holder = null;
    int width;

    private class ViewHolder{
        ImageView oneImage;
    }

    public ToonViewerAdapter(AppCompatActivity mContext, ArrayList<String> bitmaps){
        this.bitmaps = bitmaps;
        bitmaps.remove(bitmaps.size()-1);//모든 웹툰 마지막 컷은 비어있는 컷인 것 같음 마지막 컷 제거
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        width = mContext.getWindowManager().getDefaultDisplay().getWidth();
        //디바이스의 width는 디바이스마다 다를 수 있기 때문에 width를 따로 저장
        //width에 맞춰서 CustomImageView에서 Height를 조절할 것.
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){//재활용 뷰가 아니라면 생성해줘야 하므로
            view = inflater.inflate(R.layout.oneimage, null);//view를 inflate하고
            holder = new ViewHolder();//뷰 홀더를 통해 최적화
            holder.oneImage = (CustomImageView)view.findViewById(R.id.customImageView);
            view.setTag(holder);
        }else//재활용 뷰라면 해당 이미지뷰를 그냥 받아옴
            holder = (ViewHolder)view.getTag();

        //이미지 뷰에 Glide를 사용해서 url 이미지 뿌리는 부분
        Glide.with(mContext)
                .load(bitmaps.get(position))
                .asBitmap()
                .override(width, Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.placeholder)
                .into(holder.oneImage);
        return view;
    }
}
