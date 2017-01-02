package com.jungmin.realwebtoonprototype;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by 신정민 on 2017-01-03.
 */
public class ToonViewerActivity extends AppCompatActivity {
    private ArrayList<String> imageURLs;
    //웹툰 한 화에 있는 이미지 url을 순서대로 담을 ArrayList
    private ListView viewerListView;
    //url들을 통해 로드된 이미지들이 놓일 ListView
    private AppCompatActivity mContext;
    //AdapterView에 넘겨줄 Context(AppCompatActivity로 넘기는데 딱히 이유없음..)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);

        imageURLs = new ArrayList<String>();
        viewerListView = (ListView)findViewById(R.id.viewer);
        mContext = this;

        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("viewerURL");

        LoadImage loadImage = new LoadImage();
        loadImage.execute(url);
    }

    private class LoadImage extends AsyncTask<String, Bitmap, Boolean> {
        private int intvalue = 1;
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
/*              //웹 소스 파일로 저장하는 코드, 웹에서 소스보기한 코드랑 실제 안드로이드에서 읽어오는 코드랑 달라서 사용한것
                String content = "";
                HttpURLConnection conn = (HttpURLConnection)new URL("http://comic.naver.com/webtoon/list.nhn?titleId=183559&week=mon").openConnection();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                String line = "";
                while((line = buffer.readLine()) != null){
                    content += line + "\r\n";
                }

                FileOutputStream fos = mContext.openFileOutput("asd.txt", Context.MODE_PRIVATE);
                fos.write(content.getBytes());
                fos.close();*/

                Document doc = Jsoup.connect(urls[0]).get();
                Element img = doc.select(".toon_view_lst > ul > li > p > img").first();
                String url = img.attr("src");
                imageURLs.add(url);
                Elements imgs = doc.select(".toon_view_lst > ul > li > p > img");
                for (Element image : imgs) {
                    if (intvalue == 1) {
                        intvalue++;
                        continue;
                    }
                    url = image.attr("data-lazy-src");
                    try {
                        Log.i("jungmin", url);
                        if (url != null) {
                            //bmp = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                            //bitmaps.add(bmp);
                            imageURLs.add(url);
                            intvalue++;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(mContext, "URL 로드 완료", Toast.LENGTH_SHORT).show();
                viewerListView.setAdapter(new ToonViewerAdapter(mContext, imageURLs));
            }
            else
                Toast.makeText(mContext, "URL 로드 에러", Toast.LENGTH_SHORT).show();
        }
    }
}
