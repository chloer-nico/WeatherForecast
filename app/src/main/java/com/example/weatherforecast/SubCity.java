package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dhx
 * 显示和添加关注的城市
 */
public class SubCity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    List<City> citys=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_city);
        Button btnAddCity=(Button)findViewById(R.id.btnAddCity);
        Button btnBack=(Button)findViewById(R.id.btnBack);
        ListView listView=(ListView)findViewById(R.id.listView);

        CityAdapter adapter=null;
        dbHelper=new MyDatabaseHelper(this,"weather.db",null,1);
        db=dbHelper.getWritableDatabase();


        //查询数据库中已存在的订阅城市
        Cursor cursor=db.query("subcity",null,null,null,null,null,null);
        citys.clear();

        while (cursor.moveToNext()){
            String province=cursor.getString(cursor.getColumnIndex("province"));
            String city=cursor.getString(cursor.getColumnIndex("city"));
            City city1=new City(province,city);
            citys.add(city1);
        }
        cursor.close();

        //显示在listView中
        adapter=new CityAdapter(SubCity.this,R.layout.city_item,citys);
//        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        /**
         * 新增订阅城市
         * */
        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转AddCity
                Intent intent=new Intent(SubCity.this,AddCity.class);
                startActivity(intent);
            }
        });

        /**
         * 返回主活动
         * */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SubCity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * listView的点击事件
     * */

}