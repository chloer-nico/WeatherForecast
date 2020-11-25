package com.example.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ResponseCache;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    Button btnUpdate,btnQuery;
    EditText adcode;
    TextView province,city,weather,temperature,humidity,reporttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdate=(Button)findViewById(R.id.update);
        btnQuery=(Button)findViewById(R.id.query);
        adcode=(EditText)findViewById(R.id.adcode);
        province=(TextView)findViewById(R.id.province);
        city=(TextView)findViewById(R.id.city);
        weather=(TextView)findViewById(R.id.weather);
        temperature=(TextView)findViewById(R.id.temperature);
        humidity=(TextView)findViewById(R.id.humidity);
        reporttime=(TextView)findViewById(R.id.reporttime);
        //打开数据库
        dbHelper=new MyDatabaseHelper(this,"weather.db",null,1);
        db=dbHelper.getWritableDatabase();

        //暂时插入一条数据
//        ContentValues values=new ContentValues();
//        values.put("province","北京");
//        values.put("city","朝阳区");
//        values.put("adcode","110105");
//        values.put("weather","阴");
//        values.put("temperature","3");
//        values.put("humidity","53");
//        values.put("reporttime","2020-11-24 22:05:00");
//        db.insert("weather",null,values);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode=adcode.getText().toString();
                if(inputCode.length()!=6){
                    Toast.makeText(MainActivity.this,"adcode必须为6位",Toast.LENGTH_SHORT).show();
                }
                else {
                    //首先从数据库中里面读取，没有再去读取在线API
                    Cursor cursor= queryDB(inputCode);
                    //没有数据就是0
                    if(cursor.getCount()!=0){
                        cursor.moveToNext();
                        if(cursor.getString(cursor.getColumnIndex("province"))!=null){
                            province.setText(cursor.getString(cursor.getColumnIndex("province")));
                            city.setText(cursor.getString(cursor.getColumnIndex("city")));
                            weather.setText(cursor.getString(cursor.getColumnIndex("weather")));
                            temperature.setText(cursor.getString(cursor.getColumnIndex("temperature")));
                            humidity.setText(cursor.getString(cursor.getColumnIndex("humidity")));
                            reporttime.setText(cursor.getString(cursor.getColumnIndex("reporttime")));
                        }
                    }

                    //在线API读取天气
                    else {
                        getDataAsync(inputCode);
                    }
                }
                
            }
        });

    }
    /**
     * 从数据库中读取是否存在当前城市的天气记录
     * */
    public Cursor queryDB(String adcode){

//        Cursor cursor=db.query("weather",null,
//                "where adcode=?",new String[]{adcode}, null,null,null);
        Cursor cursor=db.rawQuery("select * from weather where adcode=?",new String[]{adcode});
        return  cursor;
    }

    /**
     * 菜单显示
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //分配菜单资源文件
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /**
     * 菜单响应事件
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.subCity:
                Intent intent=new Intent(MainActivity.this,SubCity.class);
                startActivity(intent);
                break;
            default:break;
        }
        return true;
    }
    /**
     * 通过异步的方式获取数据
     * 参数：待查询的城市的adcode
     * */
    public void getDataAsync(String adcode) {
        //1.创建客户端
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8,TimeUnit.SECONDS)
                .build();

        //2.创建请求
        Request request=new Request.Builder()
                .url("https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=a9948278454bf695b041c30914e29a03")
                .build();
        //3.call对象,4.以异步的方式执行
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("okhttp","————————获取数据失败—————————");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //用回调的方式在子线程中执行
                if(response.isSuccessful()){
                    Log.i("okhttp","————————获取数据成功———————");
                    String responseData=response.body().string();

                    //在UI线程中更新内容
                    showResponse(responseData);
                }
            }
        });
    }


    /**
     * 在UI线程中更新UI
     * */
    public void showResponse(final String response){
        //回到UI线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                province.setText("成功！！！");
                province.setText(response);
            }
        });
    }

    /**
     * 将查询到的天气信息保存在数据库中
     * */
}