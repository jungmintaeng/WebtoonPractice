package com.jungmin.realwebtoonprototype;

import android.graphics.Bitmap;

/**
 * Created by 신정민 on 2017-01-01.
 */
public class ToonList {
    private String subTitle;//소제목
    private Bitmap thumbnail;//썸네일
    private double star;//평점
    private String updateDate;//업데이트 날짜
    private String viewerURL;//만화 뷰어 주소

    public ToonList(String subTitle, String viewerURL,Bitmap thumbnail, double star, String updateDate){
        this.subTitle = subTitle;
        this.viewerURL = viewerURL;
        this.thumbnail = thumbnail;
        this.star = star;
        this.updateDate = updateDate;
    }

    public String getViewerURL(){return viewerURL;}
    public String getSubTitle(){return subTitle;}
    public String getUpdateDate(){return updateDate;}
    public Bitmap getThumbnail(){return thumbnail;}
    public double getStar(){return star;}
}
