package com.example.bustimetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class timeTableActivity extends AppCompatActivity {
    String arrive1;
    String arrive2;
    String lineID;
    String busNum;
    String url;
    TableLayout tableLayout;
    String textViewId = "textView";
    Integer counter = 2;
    TableRow tableRow;
    int cnt = 0;
    String today;
    boolean isClicked;

    static RequestQueue requestQueue;


    @Override
    protected void onCreate(@Nullable Bundle saveInstanceState) {
        getWindow().setStatusBarColor(Color.rgb(0,0,165));
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_timetable);

        Intent intent = getIntent();
        Button button = findViewById(R.id.button2);
        tableLayout = findViewById(R.id.tableLayout1);
        TextView tv5 = findViewById(R.id.textView5);
        isClicked = false;

        arrive1 = intent.getStringExtra("종점1");
        arrive2 = intent.getStringExtra("종점2");
        lineID = intent.getStringExtra("노선이름");
        busNum = intent.getStringExtra("버스번호");


        TextView textView8 = findViewById(R.id.textView8);
        TextView textView4 = findViewById(R.id.textView4);
        textView8.setText(arrive1);
        textView4.setText(arrive2);


        Log.d("종점1: ", arrive1);
        Log.d("종점2: ", arrive2);
        Log.d("노선이름: ", lineID);
        Log.d("버스번호: ", busNum);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(busNum + " 노선 버스 시간표");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        today = sdf.format(c1.getTime());

        url = "http://bus.gwangju.go.kr/guide/bustime/busTimeList?filterscount=0&groupscount=0&pagenum=0&pagesize=10&recordstartindex=0&recordendindex=18&LINE_ID="+lineID+"&BASE_DATE="+today;

        sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        c1 = Calendar.getInstance();
        String today2 = sdf.format(c1.getTime());

        tv5.setText(today2 + " 기준 버스 시간표입니다.");

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!isClicked){
                    isClicked = true;
                    makeRequest();
                }
            }
        });

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }
    public void makeRequest() {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(timeTableActivity.this);

                        builder.setTitle("알림").setMessage("현재 선택하신 노선은 운행이 중단되었거나 광주 버스가 아닙니다.");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        } else {
                            processResponse(response);
                        }
                    }

                    public void processResponse(String response){
                        Gson gson = new Gson();
                        TimeTableResult timeTableResult = gson.fromJson(response, TimeTableResult.class);
                        if(timeTableResult.list.size() == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(timeTableActivity.this);

                            builder.setTitle("알림").setMessage("현재 선택하신 노선은 운행이 중단되었거나 광주 버스가 아닙니다.");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                        tableRow = createTableRow();
                        Log.d("테스트용: ", response);
                        cnt = 0;
                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        int size = Math.round(1 * dm.density);

                        for(int j=0; j<timeTableResult.list.size()*2; j++){

                            TimeTableEntity timeTableEntity = timeTableResult.list.get(j/2);

                            /*
                            Log.d("cnt의 값: ", Integer.toString(cnt));
                            TextView newText2 = createTextView();
                            newText2.setText(timeTableEntity.SCH_TIME_FROM);
                            TableRow tr = createTableRow();
                            tr.addView(newText2);
                            TextView newText3 = createTextView();
                            newText3.setText(timeTableEntity.SCH_TIME_TO);
                            tr.addView(newText3);
                            tr.setPadding(size,size,size,size);
                            tableLayout.addView(tr);

                             */

                            //Log.d("테스트용: ", timeTableEntity.SCH_TIME_TO);
                            if(cnt == 0) {
                                TextView textText3 = createTextView();
                                if(timeTableEntity.SCH_TIME_FROM == null){
                                    textText3.setText("");
                                    tableRow.setPadding(size,size,size,size);
                                    tableRow.addView(textText3);
                                    cnt++;
                                    continue;
                                } else {
                                    textText3.setText(timeTableEntity.SCH_TIME_FROM);
                                    tableRow.setPadding(size,size,size,size);
                                    tableRow.addView(textText3);
                                    Log.d("표텟 0: ", timeTableEntity.SCH_TIME_FROM);
                                    cnt++;
                                    continue;
                                }
                            } else if(cnt == 1) {
                                TextView textText3 = createTextView();
                                textText3.setText("");
                                if(timeTableEntity.SCH_TIME_TO == null){
                                    textText3.setText("");
                                    tableRow.setPadding(size,size,size,size);
                                    tableRow.addView(textText3);
                                    cnt++;
                                    continue;
                                }else {
                                    textText3.setText(timeTableEntity.SCH_TIME_TO);
                                    tableRow.addView(textText3);
                                    tableRow.setPadding(size,size,size,size);
                                    cnt++;
                                    Log.d("표텟 1: ", timeTableEntity.SCH_TIME_TO);
                                    continue;
                                }
                            } else if(cnt == 2){
                                cnt = 0;
                                tableRow.setPadding(size,size,size,size);
                                tableLayout.addView(tableRow);
                                tableRow = createTableRow();
                                j--;
                                Log.d("표텟 2: ", "생성");

                                continue;
                            }
                        }
                            tableRow.setPadding(size,size,size,size);
                            tableLayout.addView(tableRow);
                            tableRow = createTableRow();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private TableRow createTableRow() {
        // 1. TableRow 객체 생성
        TableRow tableRowNm = new TableRow(getApplicationContext());
        tableRowNm.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tableRowNm.setBackgroundColor(Color.parseColor("#DFDFDF"));

        //tableLayout.addView(tableRowNm);
        return tableRowNm;
    }

    private TextView createTextView() {

        // 1. 텍스트뷰 객체 생성
        TextView textViewNm = new TextView(getApplicationContext());

        // 2. 텍스트뷰에 들어갈 문자 설정.
        textViewNm.setText("");

        // 3. 텍스트뷰 글자크기 설정
        textViewNm.setTextSize(12);

        // 4. 텍스트뷰 글자타입 설정
        textViewNm.setTypeface(null, Typeface.BOLD);

        // 5. 텍스트뷰 ID 설정
        textViewNm.setId(0);

        textViewNm.setGravity(Gravity.CENTER);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(1 * dm.density);

        textViewNm.setPadding(size,size,size,size);
        // 6. 레이아웃 설정
        /*

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(1 * dm.density);
        param.setMarginEnd(size);
        param.setMarginStart(size);
        param.setMargins(size,size,size,size);

        // 7. 설정한 레이아웃 텍스트뷰에 적용
        textViewNm.setLayoutParams(param);


         */
        // 8. 텍스트뷰 백그라운드색상 설정
        textViewNm.setBackgroundColor(Color.rgb(255,255,255));

        //tableLayout.addView(textViewNm);

        return textViewNm;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}

