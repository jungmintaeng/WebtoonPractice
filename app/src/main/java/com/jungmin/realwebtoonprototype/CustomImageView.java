package com.jungmin.realwebtoonprototype;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by 신정민 on 2016-12-31.
 */
public class CustomImageView extends ImageView {
    public CustomImageView(Context mContext){
        super(mContext);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try{
            Drawable resource = getDrawable();
            if(resource == null)
                setMeasuredDimension(0, 0);
            else{
                int outWidth = MeasureSpec.getSize(widthMeasureSpec);
                int height = outWidth * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                setMeasuredDimension(outWidth, height);
            }
        }catch (Exception e){super.onMeasure(widthMeasureSpec, heightMeasureSpec);}
    }
}
