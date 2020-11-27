package com.example.weatherforecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ResponseCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.System.in;
import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    Button btnUpdate,btnQuery;
    EditText adcode;
    TextView province,city,weather,temperature,humidity,reporttime;
    Weather weatherObject;
    ImageView pic;
    String provinceInfo,cityInfo,adcodeInfo,weatherInfo,temperatureInfo,humidityInfo,reporttimeInfo;
    String responData;
    boolean ok=false;//表示jason解析是否成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdate=(Button)findViewById(R.id.update);
        btnQuery=(Button)findViewById(R.id.query);
        pic=(ImageView)findViewById(R.id.pic);
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


        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode=adcode.getText().toString();
                Pattern pattern=Pattern.compile("[0-9]{6}");
                Matcher matcher=pattern.matcher(inputCode);
                if(!matcher.matches()){
                    Toast.makeText(MainActivity.this,"adcode必须为6位",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("查询adcode：","————————————"+inputCode);
                    //首先从数据库中里面读取，没有再去读取在线API
                    Cursor cursor= queryDB(inputCode);
                    //没有数据就是0
                    Log.i("CURSOR：","————————————"+cursor.getCount());
                    if(cursor.getCount()!=0){
                        Log.i("读取数据库内容","————————————从数据库读取————————————");
                        cursor.moveToNext();
                        if(cursor.getString(cursor.getColumnIndex("province"))!=null){
                            Toast.makeText(MainActivity.this, "从数据库读取内容", Toast.LENGTH_SHORT).show();
                            String strwea="天气状况："+cursor.getString(cursor.getColumnIndex("weather"));
                            String sttem="温度:"+cursor.getString(cursor.getColumnIndex("temperature"));
                            String strhum="湿度："+cursor.getString(cursor.getColumnIndex("humidity"));
                            String strrepo="时间："+cursor.getString(cursor.getColumnIndex("reporttime"));
                            province.setText(cursor.getString(cursor.getColumnIndex("province")));
                            city.setText(cursor.getString(cursor.getColumnIndex("city")));
                            weather.setText(strwea);
                            temperature.setText(sttem);
                            humidity.setText(strhum);
                            reporttime.setText(strrepo);
                            if (strwea.contains("云")){
                                pic.setImageResource(R.drawable.yun);
                            }
                            else if(strwea.contains("雨")){
                                pic.setImageResource(R.drawable.rain);
                            }
                            else if(strwea.contains("雪")){
                                pic.setImageResource(R.drawable.snow);
                            }
                            else if(strwea.contains("阴")){
                                pic.setImageResource(R.drawable.yin);
                            }
                            else {
                                pic.setImageResource(R.drawable.sun);
                            }
                        }
                    }

                    //在线API读取天气
                    else {
                        getDataAsync(inputCode);
//                        getDataSysnc(inputCode);
//                  延时等待查询结果
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(ok){
                            //若jason解析成功，将新的天气信息插入数据库
                            insertIntoDB();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"adcode error!",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                
            }
        });

        /**
         * 点击更新按钮在线API查询，并更新到数据库中
         * */
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input=adcode.getText().toString();
                Pattern pattern=Pattern.compile("[0-9]{6}");
                Matcher matcher=pattern.matcher(input);
                if(!matcher.matches()){
                    Toast.makeText(MainActivity.this,"adcode必须为6位",Toast.LENGTH_SHORT).show();
                }
                else{
                    getDataAsync(input);
//                    getDataSysnc(input);
                    //json解析失败就不更新
//                  延时等待查询结果
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    if(ok){
                        Log.i("执行更新UI操作","--------执行更新UI操作");
                        //在UI线程中更新内容
                        showResponse(responData);
                        //更新原来的数据库
                        updateDB();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"adcode error!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
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
                startActivityForResult(intent,1);
                break;
            default:break;
        }
        return true;
    }
    /**
     * 返回的订阅城市结果显示
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                assert data != null;
                String adcode = data.getStringExtra("adcode");
                getDataAsync(adcode);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showResponse(responData);
            }
        }
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
                .url("https://restapi.amap.com/v3/weather/weatherInfo?city="+adcode+"&key=a9948278454bf695b041c30914e29a03")
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
                    int dataLength=responseData.length();
                    Log.i("okhttp","————————数据长度："+dataLength+"——————————————");
                    //数据长度小于100表示输入的adcode不正确
                    if(dataLength>100){
                        ok=true;
                        //解析json数据
                        parseJsonWithFastJson(responseData);
                        responData=responseData;
                    }
                    else{
                        ok=false;
                    }
                }
            }
        });
    }

    /**
     * 通过同步的方式获取数据
     * */
    public void getDataSysnc (final String inputCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //1.创建客户端
                    OkHttpClient client=new OkHttpClient.Builder()
                            .connectTimeout(8, TimeUnit.SECONDS)
                            .readTimeout(8,TimeUnit.SECONDS)
                            .build();

                    //2.创建请求
                    Request request=new Request.Builder()
                            .url("https://restapi.amap.com/v3/weather/weatherInfo?city="+inputCode+"&key=a9948278454bf695b041c30914e29a03")
                            .build();
                    Response response=null;
                    //得到response对象
                    response=client.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.i("okhttp","————————获取数据成功———————");
                        String responseData=response.body().string();
                        int dataLength=responseData.length();
                        Log.i("okhttp","————————数据长度："+dataLength+"——————————————");
                        //数据长度小于100表示输入的adcode不正确
                        if(dataLength>100){
                            ok=true;
                            //解析json数据
                            parseJsonWithFastJson(responseData);
                            //在UI线程中更新内容
                            showResponse(responseData);
                        }
                        else{
                            ok=false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 在UI线程中更新UI
     * */
    public void showResponse(final String response){
        //回到UI线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<lives> livesList=weatherObject.getLives();
                lives lives=livesList.get(0);
                String strWea="天气状况："+lives.getWeather();
                String strTem="温度:"+lives.getTemperature();
                String strHum="湿度："+lives.getHumidity();
                String strRepo="时间："+lives.getReporttime();
                if (lives.getWeather().contains("云")){
                    pic.setImageResource(R.drawable.yun);
                }
                else if(lives.getWeather().contains("雨")){
                    pic.setImageResource(R.drawable.rain);
                }
                else if(lives.getWeather().contains("雪")){
                    pic.setImageResource(R.drawable.snow);
                }
                else if(lives.getWeather().contains("阴")){
                    pic.setImageResource(R.drawable.yin);
                }
                else {
                    pic.setImageResource(R.drawable.sun);
                }
                province.setText(lives.getProvince());
                city.setText(lives.getCity());
                weather.setText(strWea);
                temperature.setText(strTem);
                humidity.setText(strHum);
                reporttime.setText(strRepo);

                //更新class中的字符串用来传参
                provinceInfo=lives.getProvince();
                cityInfo=lives.getCity();
                adcodeInfo=lives.getAdcode();
                weatherInfo=lives.getWeather();
                temperatureInfo=lives.getTemperature();
                humidityInfo=lives.getHumidity();
                reporttimeInfo=lives.getReporttime();
            }
        });
    }

    /**
     * 使用fastJson解析数据
     * */
    public void parseJsonWithFastJson(String jsonData){
        try {
            //将json数据解析成Weather类型格式，必须建立完全对应的实体类
            weatherObject=JSONObject.parseObject(jsonData,Weather.class);

            Log.i("————解析以后的数据————",jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将查询到的天气信息保存在数据库中
     * */
    public void insertIntoDB() {
        ContentValues values = new ContentValues();
        values.put("province", provinceInfo);
        values.put("city", cityInfo);
        values.put("adcode", adcodeInfo);
        values.put("weather", weatherInfo);
        values.put("temperature", temperatureInfo);
        values.put("humidity", humidityInfo);
        values.put("reporttime", reporttimeInfo);
        db.insert("weather", null, values);
//        Toast.makeText(MainActivity.this, "已成功添加到数据库！", Toast.LENGTH_SHORT).show();

    }

    /**
     * 更新原有的天气信息
     * */
    public void updateDB(){
        out.println("更新数据adcode——————————————————————"+adcodeInfo);
        out.println("更新数据，weatherInfo———————————————————————"+weatherInfo);
       db.execSQL("update weather set weather=?,temperature=?,humidity=?,reporttime=? where adcode=?"
       ,new String[]{weatherInfo,temperatureInfo,humidityInfo,reporttimeInfo,adcodeInfo});
        Toast.makeText(MainActivity.this, "更新数据库信息成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 从数据库中读取是否存在当前城市的天气记录
     * */
    public Cursor queryDB(String adcode){
        Cursor cursor=db.rawQuery("select * from weather where adcode=?",new String[]{adcode});
        return  cursor;
    }
}