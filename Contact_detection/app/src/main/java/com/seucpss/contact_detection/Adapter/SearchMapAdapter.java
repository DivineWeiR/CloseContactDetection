package com.seucpss.contact_detection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.seucpss.contact_detection.R;
import com.seucpss.contact_detection.RouteAddActivity;

import java.util.List;

public class SearchMapAdapter extends ArrayAdapter {
    private final int resourceId;
    private Context context;
    private BaiduMap mapView;
    public SearchMapAdapter(@NonNull Context context, int resourceId, List<SuggestionResult.SuggestionInfo> object, BaiduMap mapView) {
        super(context, resourceId,object);
        this.resourceId = resourceId;
        this.context=context;
        this.mapView=mapView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final SuggestionResult.SuggestionInfo position1=(SuggestionResult.SuggestionInfo)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView pname=(TextView)view.findViewById(R.id.textview);
        Button button=(Button)view.findViewById(R.id.show_route);
        pname.setText(position1.city+position1.key);
        System.out.println(position1.getAddress());
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, RouteAddActivity.class);
                intent.putExtra("name",position1.key);
                intent.putExtra("adr",position1.district);
                intent.putExtra("lat",position1.pt.latitude);
                intent.putExtra("lon",position1.pt.longitude);
                context.startActivity(intent);
            }
        });
        pname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(0)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0).latitude(position1.pt.latitude)
                        .longitude(position1.pt.longitude).build();
                mapView.setMyLocationData(locData);
                LatLng ll = new LatLng(position1.pt.latitude,position1.pt.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mapView.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });
        return view;
    }
}
