package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author dhx
 * spinnerPro，spinnerCity:一，二级spinner
 * list,sublist:一二级城市列表
 * selectedPro,selectedCity：选择的省份、城市
 */
public class AddCity extends AppCompatActivity {
    private String selectedPro = null, selectedCity = null;
    private Spinner spinnerPro;
    private Spinner spinnerCity;
    private Division division;
    private Button btnOk, btnBack;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> sublist = new ArrayList<String>();
    int selectedProInt, selectedCityInt;
    private String[][] citys = new String[][]{
            {"山西省", "阳泉市", "太原市", "临汾市", "运城市", "大同市"},
            {"四川省", "广元市", "成都市", "绵阳市", "广安市", "德阳市", "巴中市"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        spinnerPro = (Spinner) findViewById(R.id.spnProvince);
        spinnerCity = (Spinner) findViewById(R.id.spnCity);
        btnOk = (Button) findViewById(R.id.btnokk);
        btnBack = (Button) findViewById(R.id.btnbackk);
        //加入一级列表
        for (int i = 0; i < citys.length; i++) {
            list.add(citys[i][0]);
            Log.i("citys", i + "————————" + citys[i][0]);
        }
        //为第一个spinner添加适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCity.this, android.R.layout.simple_spinner_item, list);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPro.setAdapter(adapter);

        spinnerPro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPro = list.get(position);
                selectedProInt = position;
                //清除数据
                sublist.clear();
                for (int j = 1; j < citys[position].length; j++) {
                    sublist.add(citys[position][j]);
                }
                Log.i("sublist——————", String.valueOf(sublist));
                //为下一级spinner设置适配器
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (AddCity.this, android.R.layout.simple_spinner_item, sublist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPro=citys[0][0];
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("index", "省份下标————————————————————————" + selectedProInt);
                selectedCity = citys[selectedProInt][position+1];
                Log.i("选择的省份为", "————————————————————————" + selectedPro);
                Log.i("选择的城市为", "————————————————————————" + selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCity=citys[selectedProInt][0];
            }
        });




        /**
         * 根据获取的城市去查询相应的adcode
         * */
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adcode=getAdcode(selectedPro,selectedCity);
                Intent intent = new Intent(AddCity.this, SubCity.class);
                intent.putExtra("province",selectedPro);
                intent.putExtra("city",selectedCity);
                intent.putExtra("adcode",adcode);
                //设置结果码，1表示成功
                setResult(1,intent);
                finish();
            }
        });

        /**
         * 取消
         * */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCity.this, SubCity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 将选择的城市发送HTTP请求，查询adcode
     * */
    public String getAdcode(String province,String city) {
        final String[] result = new String[1];
        //1.创建客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build();

        //2.创建请求
        Request request = new Request.Builder()
                .url("https://restapi.amap.com/v3/config/district?keywords=" + province + "&subdistrict=2&key=a9948278454bf695b041c30914e29a03")
                .build();
        //3.call对象,4.以异步的方式执行
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("Addcity", "————————获取城市数据失败—————————");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //用回调的方式在子线程中执行
                if (response.isSuccessful()) {
                    Log.i("Addcity", "————————获取城市数据成功—————————");
                    String responseData = response.body().string();
                    int dataLength = responseData.length();
                    Log.i("Addcity", "————————城市数据长度：" + dataLength + "——————————————");
                    parseJsonWithFastJson(responseData);
                    //一级districts，表示省份
                    List<districts> districtsList1 =division.getDistricts();
                    //二级districts，表示城市
                    List<districts> districtsList2 =districtsList1.get(0).getDistricts();
                    //搜索城市列表，寻找匹配的城市
                    for(districts d:districtsList2){
                        String city=d.getName();
                        if(city.equals(selectedCity)){
                            result[0] =d.getAdcode();
                            break;
                        }
                    }
                }

            }

        });
        //延时等待结果
        try{
            Thread.sleep(600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Addcity", "————————当前城市的adcode—————————"+result[0]);
        return result[0];
    }

    /**
     * 使用fastJson解析数据
     * */
    public void parseJsonWithFastJson(String jsonData){
        try {
            //将json数据解析成Weather类型格式，建立完全对应的实体类
            division= JSONObject.parseObject(jsonData,Division.class);
            Log.i("————解析以后的城市数据————",jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
