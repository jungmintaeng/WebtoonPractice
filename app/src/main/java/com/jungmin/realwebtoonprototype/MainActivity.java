package com.jungmin.realwebtoonprototype;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ArrayList<Toon> toonList; //웹툰 정보 저장할 ArrayList
    String day = "";    //요일
    String order = "ViewCount";  //sorting order
    String url = null;  //요일과 sorting order를 추가한 query가 완성된 url
    ListView toonListView;//웹툰을 표시할 리스트뷰
    Context mContext;//어댑터에 넘겨줄 Context
    MainActivityAdapter mainAdapter;// 리스트뷰에 적용될 Adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        toonListView = (ListView)findViewById(R.id.day_webtoonlist);
        toonListView.setOnItemClickListener(this);
        toonList = new ArrayList<Toon>();
        mainAdapter = new MainActivityAdapter(mContext, toonList);

        //초기 sorting order를 조회순으로 설정
        RadioButton btn = (RadioButton)findViewById(R.id.radio_ViewCount);
        btn.setChecked(true);

        RadioGroup dayGroup = (RadioGroup)findViewById(R.id.dayGroup);
        //요일을 선택할 수 있는 라디오그룹
        dayGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                GetToons getToons = new GetToons();
                switch (id){
                    case R.id.mon:
                        day = "mon";
                        break;
                    case R.id.tue:
                        day = "tue";
                        break;
                    case R.id.wed:
                        day = "wed";
                        break;
                    case R.id.thu:
                        day = "thu";
                        break;
                    case R.id.fri:
                        day = "fri";
                        break;
                    case R.id.sat:
                        day = "sat";
                        break;
                    case R.id.sun:
                        day = "sun";
                        break;
                }
                initializeURL();
                getToons.execute(url);
            }
        });

        RadioGroup sortOrderGroup = (RadioGroup)findViewById(R.id.sortOrder);
        //정렬 순서를 나타내는 라디오그룹
        sortOrderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                GetToons getToons = new GetToons();
                switch (id){
                    case R.id.radio_ViewCount://조회순
                        order = "ViewCount";
                        break;
                    case R.id.radio_Update://업데이트순
                        order = "Update";
                        break;
                    case R.id.radio_StarScore://별점순
                        order = "StarScore";
                        break;
                    case R.id.radio_TitleName://제목순
                        order = "TitleName";
                        break;
                }
                initializeURL();
                getToons.execute(url);
            }
        });

        /*
        앱을 막 실행했을 때에는 현재 요일에 맞추어 해당 요일의 웹툰을 보여줘야 함
        요일 받아오기 : Calendar 이용
        요일(1~7, 1:일요일): 2
         */
        Calendar today = Calendar.getInstance();
        switch (today.get(Calendar.DAY_OF_WEEK)){
            case 1://일
                btn = (RadioButton)findViewById(R.id.sun);
                break;
            case 2://월
                btn = (RadioButton)findViewById(R.id.mon);
                break;
            case 3://화
                btn = (RadioButton)findViewById(R.id.tue);
                break;
            case 4://수
                btn = (RadioButton)findViewById(R.id.wed);
                break;
            case 5://목
                btn = (RadioButton)findViewById(R.id.thu);
                break;
            case 6://금
                btn = (RadioButton)findViewById(R.id.fri);
                break;
            case 7://토
                btn = (RadioButton)findViewById(R.id.sat);
                break;
        }
        btn.setChecked(true);//OnCheckedChange에서 URL Parsing하는 부분 실행하므로 오늘 요일을 체크
    }

    private void initializeURL(){   //사용자가 요일, 정렬 순서를 선택함에 따라 바뀐 url을 반영하는 메소드
        url = "http://comic.naver.com/webtoon/weekdayList.nhn?week=" + day + "&order=" + order + "&view=image";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * 웹툰 하나를 클릭했을 때, 회차 정보 Activity로 넘어갈 수 있도록 하는 코드가 들어가야 함
         */
        Toon itemClicked = (Toon)mainAdapter.getItem(position);
        Intent toListIntent = new Intent(this, ToonListActivity.class);
        toListIntent.putExtra("title", itemClicked.getTitle());//웹툰 제목
        toListIntent.putExtra("url", itemClicked.getListURL());//만화 리스트 url
        toListIntent.putExtra("smartToon", itemClicked.isSmartToon());
        startActivity(toListIntent);
    }

    private class GetToons extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            toonList.clear();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();    //인자로 들어온 url document에 담음
                Elements toons = doc.select(".lst");//각 웹툰은 lst라는 클래스 안에 담겨 있고, 하나의 lst Element 내에서 각 웹툰의 정보를 얻어올 것이다.

                for(Element toon : toons){ //toon : 각 lst에 담겨있는 웹툰 하나의 정보가 통으로 담겨있음
                    String title = toon.select(".toon_name").first().text();
                    String thumbURL = toon.select(".im_inbr > img").first().attr("src");
                    String star = toon.select(".if1.st_r").first().text();
                    String lastUpdateDate = toon.select(".if1").last().text();
                    String author = toon.select(".sub_info").first().text();
                    String listURL = toon.select("a").first().attr("href");
                    boolean smartToon;
                    try{
                        if(toon.select(".ico_cut").first().text().equals("컷툰"))
                            smartToon = true;
                        else
                            smartToon = false;
                    }catch (Exception e){smartToon = false;}
                    //생성자 : String title, String thumbnail, String star, String lastUpdateDate, String author, String listURL, boolean smartToon
                    toonList.add(new Toon(title, thumbURL, Double.parseDouble(star), lastUpdateDate, author, listURL, smartToon));
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
                Toast.makeText(MainActivity.this, "Load Complete", Toast.LENGTH_SHORT).show();
                mainAdapter.setToons(toonList);
                toonListView.setAdapter(mainAdapter);
                //모든 정보들을 받아왔을 때 리스트뷰의 어댑터를 바꾸어준다.
            } else {
                Toast.makeText(MainActivity.this, "3G/LTE/WiFi 연결 상태를 확인하세요", Toast.LENGTH_SHORT).show();
                //URL 로드에 실패한 경우. 인터넷 연결이 안되었을 확률이 높다
            }
        }
    }
}
