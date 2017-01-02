package com.jungmin.realwebtoonprototype;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 신정민 on 2017-01-01.
 */
public class ToonListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ProgressBar loadingProgress;
    private ArrayList<ToonList> toonLists;
    private ListView toonListView;
    private ToonListAdapter toonListAdapter;
    private Context mContext;
    private GetToons toons = new GetToons();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toonlist);

        toonLists = new ArrayList<ToonList>();
        mContext = this;

        Intent getIntent = getIntent();
        String title = getIntent.getStringExtra("title");
        String url = getIntent.getStringExtra("url");// + "&page=" + pageNumber;
        //intent를 통해 제목과 리스트의 url을 받아옴

        Toast.makeText(this, url, Toast.LENGTH_LONG).show();

        //findView
        TextView titleTextView = (TextView) findViewById(R.id.toonlist_ToonNameTextView);
        toonListView = (ListView) findViewById(R.id.toonlist_ListView);
        toonListView.setOnItemClickListener(this);
        loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);

        //제목 설정
        titleTextView.setText(title);
        toons.execute(url);
        //toons.execute("http://m.comic.naver.com/webtoon/list.nhn?titleId=25455&week=tue");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToonList toonList = (ToonList)toonListAdapter.getItem(position);
        Intent gotoViewerIntent = new Intent(this, ToonViewerActivity.class);
        gotoViewerIntent.putExtra("viewerURL", toonList.getViewerURL());
        startActivity(gotoViewerIntent);
    }

    @Override
    protected void onDestroy() {
        try{
            toons.cancel(true);
        }catch (Exception e){}
        super.onDestroy();
    }

    private class GetToons extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();    //인자로 들어온 url document에 담음
                Elements toons = doc.select(".lst");//각 웹툰은 lst라는 클래스 안에 담겨 있고, 하나의 lst Element 내에서 각 웹툰의 정보를 얻어올 것이다.

                String lastURL = toons.first().select(".im_inbr > img").first().attr("src");
                String[] urlParse = lastURL.split("/");
                int lastToonNumber = Integer.parseInt(urlParse[urlParse.length - 2]);
                Log.i("jungmin", "the last toonnumber : " + lastToonNumber);

                for (int i = 1; i <= lastToonNumber / 10 + 1; i++) {
                    doc = Jsoup.connect(urls[0] + "&amp;page=" + i + "#").get();    //인자로 들어온 url document에 담음
                    Log.i("jungmin", urls[0] + "&amp;page=" + i + "#");
/*
                    인터넷 소스 받아서 파일로 내보내 저장하는 코드
                    String content = "";
                    HttpURLConnection conn = (HttpURLConnection) new URL("http://comic.naver.com/webtoon/list.nhn?titleId=183559&week=mon").openConnection();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = "";
                    while ((line = buffer.readLine()) != null) {
                        content += line + "\r\n";
                    }

                    FileOutputStream fos = mContext.openFileOutput("asd" + i + ".txt", Context.MODE_PRIVATE);
                    fos.write(content.getBytes());
                    fos.close();*/
                    //각 웹툰은 lst라는 클래스 안에 담겨 있고, 하나의 lst Element 내에서 각 웹툰의 정보를 얻어올 것이다.
                    for (Element toon : toons) {
                        String title = toon.select(".toon_name").first().text();
                        String thumbURL = toon.select(".im_inbr > img").first().attr("src");
                        String star = toon.select(".if1.st_r").first().text();
                        String updateDate = toon.select(".if1").last().text();
                        String viewerURL = "http://comic.naver.com" + toon.select("a").first().attr("href");
                        //생성자 String subTitle,Bitmap thumbnail, double star, String updateDate
                        try {
                            toonLists.add(new ToonList(title, viewerURL, BitmapFactory.decodeStream(new URL(thumbURL).openConnection().getInputStream()), Double.parseDouble(star), updateDate));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                //성공했을 때 행동
                Toast.makeText(ToonListActivity.this, "Load Complete 로드 개수 : " + String.valueOf(toonLists.size()), Toast.LENGTH_SHORT).show();
                toonListAdapter = new ToonListAdapter(ToonListActivity.this, toonLists);
                toonListView.setAdapter(toonListAdapter);
                loadingProgress.setVisibility(View.GONE);
            } else {
                Toast.makeText(ToonListActivity.this, "3G/LTE/WiFi 연결 상태를 확인하세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
