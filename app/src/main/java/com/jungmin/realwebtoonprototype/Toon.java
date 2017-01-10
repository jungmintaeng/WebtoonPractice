package com.jungmin.realwebtoonprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

/**
 * Created by 신정민 on 2016-12-31.
 */
public class Toon {
    private String title;   //제목
    private String thumbnail;   //썸네일 비트맵
    private double star;    //평점
    private String lastUpdateDate;  //최근 업데이트 날짜
    private String author;  //작가
    private String listURL; //만화 목록 URL
    private boolean smartToon;  //스마트툰(컷툰) 여부

    /*
     * 생성자
     */
    public Toon(String title, final String thumbnailURL, double star, String lastUpdateDate, String author, String listURL, boolean smartToon) {
        this.title = title;
        this.thumbnail = thumbnailURL;
/*        new Thread() {
            public void run() {
                try {
                    thumbnail = BitmapFactory.decodeStream(new URL(thumbnailURL).openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();*/

        this.star = star;
        this.lastUpdateDate = lastUpdateDate;
        this.author = author;
        this.listURL = "http://comic.naver.com" + listURL;
        this.smartToon = smartToon;
    }

    /*
     * getter methods
     */
    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public double getStar() {
        return star;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getListURL() {
        return listURL;
    }

    public boolean isSmartToon() {
        return smartToon;
    }
}
