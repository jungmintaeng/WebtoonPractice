package com.jungmin.realwebtoonprototype;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 신정민 on 2017-01-10.
 */
public class CutToonViewerActivity extends AppCompatActivity {
    private ArrayList<String> imageURLs;//imageURL 담을 ArrayList
    private ViewFlipper flipper;//이미지를 넘기면서 볼 수 있는 뷰
    private int width;//디바이스의 가로 길이를 알아내기 위해
    private int currentIndex = 1;//현재 보고 있는 컷이 몇 번째 컷인가?
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuttoonviewer);
        flipper = (ViewFlipper)findViewById(R.id.cutFlipper);
        flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex < imageURLs.size()) {
                    flipper.showNext();
                    currentIndex++;//이미지 개수가 url의 개수보다 작으면 클릭했을 때 다음으로 넘어감
                }
            }
        });
        width = this.getWindowManager().getDefaultDisplay().getWidth();//디바이스 width 받아옴
        imageURLs = new ArrayList<String>();
        ImageUrlParser parser = new ImageUrlParser();
        parser.execute(getIntent().getStringExtra("viewerURL"));
    }

    private class ImageUrlParser extends AsyncTask<String, String, Boolean>{
        //javascript 부분을 파싱하는 라이브러리가 없는 것 같음... 파싱하는 클래스
        private String searchImageString = "var aImageList = [";//imageurl들이 aImageList라는 var 내에 들어 있음
/*      이와 같은 형태로 들어 있음
        var aImageList = [
        {sImageUrl :'http://imgcomic.naver.com/mobilewebimg/679519/64/b2809f4ebe4faf067b853a58b429861e_001.jpg',
                sWatermarkUrl : 'http://shared.comic.naver.net/watermark/679519/64/5f67d18d95d93c53f0e5f6e15082f7b7.jpg',
                nWatermarkHeight : '840',
                nWatermarkWidth : '690',
                nIndex:'1'}.......................*/
        private String[] images;
        @Override
        protected Boolean doInBackground(String... urls) {
            try{
                String content = "";
                HttpURLConnection conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = "";
                while ((line = buffer.readLine()) != null) {
                    content += line + "\r\n";
                }//일단 웹페이지 전체 소스를 받아옴

                int startIndex = content.indexOf(searchImageString);
                content = content.substring(startIndex + searchImageString.length());
                //var aImageList = [의 위치를 찾아서 그 전 부분들은 모두 없애버림
                int endIndex = content.indexOf(";");
                content = content.substring(0, endIndex - 1).trim();
                //aImageList의 선언이 끝나는 세미콜론 부분을 찾아서 세미콜론 뒤도 다 날려버림

                images = content.split("[{}]");
 /*
 중괄호로 자르게 되면 현재 문자열의 상태는

 sImageUrl :'http://imgcomic.naver.com/mobilewebimg/679519/64/b2809f4ebe4faf067b853a58b429861e_003.jpg',
 sWatermarkUrl : 'http://shared.comic.naver.net/watermark/679519/64/10fea4e14d06ac6ae893ac8bc1bff80f.jpg',
 nWatermarkHeight : '840',
 nWatermarkWidth : '690',
	nIndex:'3'

	이와 같은 형태임

	우리가 원하는 부분은 sImageUrl부분.
	위의 문자열을 작은 따옴표 '로 문자열을 잘라서 [1]번째 인덱스를 쓰면 될 것 같다
  */
                for(int i = 0 ; i < images.length; i++){
                    if(images.length > 0){
                        String[] split = images[i].split("\'");
                        try{
                            imageURLs.add(split[1]);
                        }catch (ArrayIndexOutOfBoundsException ex){
                            //split은 빈 문자열도 String[] split에 넣기 때문에
                            //빈 문자열.split을 할 경우 예외가 남
                            //비워두어 그냥 넘어가도록 하자
                        }
                    }
                }
            }catch (Exception e){e.printStackTrace();return false;}
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                for(String url : imageURLs){//성공적으로 url들이 로드 되었을 때
                    ImageView imageView = new CustomImageView(CutToonViewerActivity.this);
                    Glide.with(CutToonViewerActivity.this)
                            .load(url)
                            .asBitmap()
                            .override(width, Target.SIZE_ORIGINAL)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);//이미지뷰를 하나 만들어서
                    flipper.addView(imageView);//flipper에 추가
                }
            }else{
                Toast.makeText(CutToonViewerActivity.this, "Parse ERR", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
