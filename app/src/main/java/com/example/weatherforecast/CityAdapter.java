package com.example.weatherforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * @author dhx
 * city类的适配器
 */
public class CityAdapter extends ArrayAdapter<City> {
    private int resourceId;

    public CityAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        City city=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView==null){
            //缓存为空时才加载布局
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            //缓存空时，创建一个用于缓存的实例
            viewHolder=new ViewHolder();
            viewHolder.subProvince=(TextView)view.findViewById(R.id.subProvince);
            viewHolder.subCity=(TextView)view.findViewById(R.id.subCity);

            //将viewHolder保存在view中
            view.setTag(viewHolder);
        }
        else {
            //否则重用convertView，以此达到了不会重复加载布局
            view=convertView;
            //重用viewHolder,重新获取viewHolder
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.subProvince.setText(city.getProvince());
        viewHolder.subCity.setText(city.getCity());
        return view;
    }
    //创建一个内部类ViewHolder,用于对控件的实例进行缓存
    public class ViewHolder{
        TextView subProvince,subCity;
    }
}
