package com.example.bustimetable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static RequestQueue requestQueue;
    Spinner spinner;
    List<String> spinnerArray;
    ArrayAdapter<String> adapter;
    String arrive1;
    String arrive2;
    String lineID;
    BusLineDetail busLineDetail;
    ArrayList<BusLineDetail> busLineArrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.rgb(0,0,165));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        busLineArrList = new ArrayList<>();

        spinnerArray = new ArrayList<>();
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, spinnerArray
        );
        Button button = findViewById(R.id.button5);
        Button checkButton = findViewById(R.id.button6);
        Button nextButton = findViewById(R.id.button);
        Button infoButton = findViewById(R.id.button3);
        infoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("안내").setMessage("본 프로그램은 광주광역시에서 실시간으로 제공하는 공공 오픈 API를 이용하여 제작하였습니다. 프로그램 사용시에 인터넷 연결은 필수적입니다. \n\n프로그램 제작자: 전남대학교 공과대학 전자컴퓨터공학부 이기준");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(spinnerArray.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("안내").setMessage("버스 노선을 먼저 선택해주세요.");
                    builder.setCancelable(true);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    int position = spinner.getSelectedItemPosition();
                    Intent intent = new Intent(getApplicationContext(), timeTableActivity.class);
                    intent.putExtra("종점1", busLineArrList.get(position).DIR_DOWN_NAME);
                    intent.putExtra("종점2", busLineArrList.get(position).DIR_UP_NAME);
                    intent.putExtra("노선이름", busLineArrList.get(position).LINE_ID);
                    intent.putExtra("버스번호", busLineArrList.get(position).LINE_NAME);
                    startActivity(intent);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("안내").setMessage("해당 기능은 프로그램 개발시 테스트로 사용했던 기능입니다");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //       showTimeTable(spinner.getSelectedItemPosition()); 요청값 받기
            }
        });

        spinner = findViewById(R.id.spinner);

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void showTimeTable(int num){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("요청 값").setMessage("버스번호: " + busLineArrList.get(num).LINE_NAME + "\n노선 번호: "+ busLineArrList.get(num).LINE_ID + "\n종점1: " + busLineArrList.get(num).DIR_UP_NAME + "\n종점2: " + busLineArrList.get(num).DIR_DOWN_NAME);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void makeRequest() {
        String url = "http://api.gwangju.go.kr/json/lineInfo";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("완료").setMessage("서버로부터 송신이 정상적으로 완료되었습니다.");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();*/

                        processResponse(response);

                    }
                    },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("오류").setMessage("서버와 통신 중 에러가 발생하였습니다.\n네트워크 연결을 확인해주십시오.");
                        builder.setCancelable(true);
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);


    }
    public void processResponse(String response){
        Gson gson = new Gson();
        BusLineIDList busLineIDList = gson.fromJson(response, BusLineIDList.class);
        for(int i = 0; i< busLineIDList.LINE_LIST.size(); i++){
            busLineDetail = busLineIDList.LINE_LIST.get(i);
            Log.d("버스 노선 이름: ", busLineDetail.LINE_NAME);
            busLineArrList.add(busLineDetail);
            spinnerArray.add(busLineDetail.LINE_NAME.toString());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}