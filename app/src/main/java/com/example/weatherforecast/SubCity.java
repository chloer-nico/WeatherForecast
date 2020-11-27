package com.example.weatherforecast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dhx
 * 显示和添加关注的城市
 */
public class SubCity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ListView listView;
    List<City> citys=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_city);
        Button btnAddCity=(Button)findViewById(R.id.btnAddCity);
        Button btnBack=(Button)findViewById(R.id.btnBack);
        listView=(ListView)findViewById(R.id.listView);

        CityAdapter adapter=null;
        dbHelper=new MyDatabaseHelper(this,"weather.db",null,1);
        db=dbHelper.getWritableDatabase();


        //查询数据库中已存在的订阅城市,显示在列表中
        query(adapter,listView);


        /**
         * 新增订阅城市
         * */
        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转AddCity
                Intent intent=new Intent(SubCity.this,AddCity.class);
                //有返回结果的跳转
                startActivityForResult(intent,1);
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

        /**
         * listView的点击事件
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的城市的adcode
                City city=citys.get(position);
                String adcode=city.getAdcode();
                //将点击的城市adcode返回给主活动
                Intent intent = new Intent(SubCity.this, MainActivity.class);
                intent.putExtra("adcode",adcode);
                setResult(1,intent);
                finish();
            }
        });
    }



    /**
     * 查看订阅城市表，显示在listView中
     * */
    public void query(CityAdapter cityAdapter,ListView listView){
        Cursor cursor=db.query("subcity",null,null,null,null,null,null);
        citys.clear();

        Log.i("总共的订阅城市num","——————————————————————"+cursor.getCount());
        while (cursor.moveToNext()){
            String province=cursor.getString(cursor.getColumnIndex("province"));
            String city=cursor.getString(cursor.getColumnIndex("city"));
            String adcode=cursor.getString(cursor.getColumnIndex("adcode"));
            City city1=new City(province,city,adcode);
            citys.add(city1);
        }
        cursor.close();

        //显示在listView中
        cityAdapter=new CityAdapter(SubCity.this,R.layout.city_item,citys);
        listView.setAdapter(cityAdapter);
    }


    /**
     * 处理返回的result结果
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                assert data != null;
                String pro = data.getStringExtra("province");
                String city = data.getStringExtra("city");
                String adcode = data.getStringExtra("adcode");
                //首先查询数据库中是否存在该城市
                Cursor cursor = null;
                cursor=db.rawQuery("select * from subcity where adcode=?",new String[]{adcode});
                //未查询到结果
                if(cursor.getCount()==0){
                    //将新的城市插入数据库
                    ContentValues values = new ContentValues();
                    values.put("province", pro);
                    values.put("city", city);
                    values.put("adcode", adcode);
                    db.insert("subcity", null, values);
                    Toast.makeText(SubCity.this,"订阅成功！",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SubCity.this,"已订阅该城市！",Toast.LENGTH_SHORT).show();
                }

                CityAdapter adapter=null;
                ListView listView=(ListView)findViewById(R.id.listView);
                cursor.close();
                query(adapter,listView);
            }
        }
    }
}