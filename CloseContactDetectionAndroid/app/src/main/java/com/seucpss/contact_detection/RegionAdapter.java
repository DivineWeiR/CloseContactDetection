package com.seucpss.contact_detection;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen.yingjie on 2019/3/23
 */
public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    public static final int DATA_CITY = 1;// 选择省
    public static final int DATA_AREA = 2;// 选择市
    public static final int DATA_STREET = 3;// 选择县/区
    public static final int DATA_VILLAGE = 4;

    private List<RegionBean> cityDatas; // 总（省)数据
    private Context mContext;
    // 显示数据类型
    private int dataType;
    // 选中的省item的position
    private int positionCity;
    // 选中的市item的position
    private int positionArea;
    // 选中的县/区item的position
    private int positionStreet;
    private int positionVillage;
    // 记录选择状态

    private List<Boolean> isCheckedC;
    private List<Boolean> isCheckedA;
    private List<Boolean> isCheckedS;
    private List<Boolean> isCheckedV;


    private String checkedCity;
    private String checkedArea;
    private String checkedStreet;
    private String checkedVillage;

    private OnItemCheckedListener onItemCheckedListener;

    public RegionAdapter(Context context) {
        mContext = context;

        isCheckedC = new ArrayList<>();
        isCheckedA = new ArrayList<>();
        isCheckedS = new ArrayList<>();
        isCheckedV = new ArrayList<>();
    }

    /**
     * 刷新
     *
     * @param cityDatas 数据集
     * @param dataType      应该选择(显示)    省：DATA_PROVINCE 市：DATA_CITY 县/区：DATA_AREA
     * @param checkedStreet    已选择省
     * @param checkedCity          已选择市
     * @param checkedArea          已选择县/区
     */
    public void refreshData(List<RegionBean> cityDatas, int dataType,  String checkedCity, String checkedArea, String checkedStreet, String checkedVillage) {
        this.cityDatas = cityDatas;
        this.dataType = dataType;
        this.checkedCity = checkedCity;
        this.positionCity = getCityPosition(checkedCity);
        this.positionArea = getAreaPosition(checkedCity, checkedArea);
        this.positionStreet = getStreetPosition(checkedCity, checkedArea, checkedStreet);
        this.positionVillage = getVillagePosition(checkedCity, checkedArea, checkedStreet, checkedVillage);
        // 初始化选中状态false
        if (dataType == DATA_CITY) {
            isCheckedC.clear();
            if (cityDatas == null) {
                return;
            }
            for (int i = 0; i < cityDatas.size(); i++) {
                isCheckedC.add(false);
            }
            if (positionCity != -1) {
                isCheckedC.set(positionCity, true);
            }
        } else if (dataType == DATA_AREA) {
            isCheckedA.clear();
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null) {
                return;
            }
            for (int i = 0; i < cityDatas.get(positionCity).areas.size(); i++) {
                isCheckedA.add(false);
            }
            if (positionArea != -1) {
                isCheckedA.set(positionArea, true);
            }
        } else if (dataType == DATA_STREET){
            isCheckedS.clear();
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null ||
                    cityDatas.get(positionCity).areas.get(positionArea) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets == null) {
                return;
            }
            for (int i = 0; i < cityDatas.get(positionCity).areas.get(positionArea).streets.size(); i++) {
                isCheckedS.add(false);
            }
            if (positionStreet != -1) {
                isCheckedS.set(positionStreet, true);// 已选中
            }
        }else if (dataType == DATA_VILLAGE) {
            isCheckedV.clear();
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null ||
                    cityDatas.get(positionCity).areas.get(positionArea) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages == null){
                return;
            }
            for (int i = 0; i < cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.size();i++){
                isCheckedV.add(false);
            }
            if(positionVillage != -1){
                isCheckedV.set(positionVillage, true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_region_recycleview, viewGroup, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        List<Boolean> isChecked = new ArrayList<>();

        // 显示数据，把当前position设置为tag
        if (dataType == DATA_CITY) {
            isChecked = isCheckedC;
            if (cityDatas == null ||
                    cityDatas.get(position) == null) {
                return;
            }
            holder.tvName.setText(cityDatas.get(position).cityName);
        } else if (dataType == DATA_AREA) {
            isChecked = isCheckedA;
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null ||
                    cityDatas.get(positionCity).areas.get(position) == null) {
                return;
            }
            holder.tvName.setText(cityDatas.get(positionCity).areas.get(position).areaName);
        } else if(dataType == DATA_STREET) {
            isChecked = isCheckedS;
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null ||
                    cityDatas.get(positionCity).areas.get(positionArea) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(position) == null) {
                return;
            }
            holder.tvName.setText(cityDatas.get(positionCity).areas.get(positionArea).streets.get(position).streetName);
        }else{
            isChecked = isCheckedV;
            if (cityDatas == null ||
                    cityDatas.get(positionCity) == null ||
                    cityDatas.get(positionCity).areas == null ||
                    cityDatas.get(positionCity).areas.get(positionArea) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet) == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages == null ||
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.get(position) == null){
                return;
            }
            holder.tvName.setText(cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.get(position).name);
        }
        // 根据选中状态更新UI
        if (isChecked.get(position)) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.ff5000));
            holder.tvSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.v666666));
            holder.tvSelected.setVisibility(View.GONE);
        }
        if (onItemCheckedListener != null) {
            final List<Boolean> finalIsChecked = isChecked;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 1、所有值重置为false
                    for (int i = 0; i < finalIsChecked.size(); i++) {
                        finalIsChecked.set(i, false);
                    }
                    // 2、当前点击的设置为true
                    finalIsChecked.set(position, true);
                    // 3、更新
                    notifyDataSetChanged();

                    if (dataType == DATA_CITY) {
                        checkedCity = holder.tvName.getText().toString();
                    } else if (dataType == DATA_AREA) {
                        // 这里不能只给checkedCity赋值，checkedProvince容易为空，在refresh中赋值。
                        checkedArea = holder.tvName.getText().toString();
                    } else if(dataType == DATA_STREET){
                        checkedStreet = holder.tvName.getText().toString();
                    }else{
                        checkedVillage = holder.tvName.getText().toString();
                    }
                    onItemCheckedListener.onItemChecked(dataType,  checkedCity, checkedArea, checkedStreet, checkedVillage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (dataType == DATA_CITY) {
            if (cityDatas != null) {
                size = cityDatas.size();
            }
        } else if (dataType == DATA_AREA) {
            if (cityDatas != null &&
                    cityDatas.get(positionCity) != null &&
                    cityDatas.get(positionCity).areas != null) {
                size = cityDatas.get(positionCity).areas.size();
            }
        } else if(dataType == DATA_STREET){ // dataType == DATA_AREA 选择县/区前和后都展示同样的数据
            if (cityDatas != null &&
                    cityDatas.get(positionCity) != null &&
                    cityDatas.get(positionCity).areas != null &&
                    cityDatas.get(positionCity).areas.get(positionArea) != null &&
                    cityDatas.get(positionCity).areas.get(positionArea).streets != null) {
                size = cityDatas.get(positionCity).areas.get(positionArea).streets.size();
            }
        }else{
            if (cityDatas != null &&
                    cityDatas.get(positionCity) != null &&
                    cityDatas.get(positionCity).areas != null &&
                    cityDatas.get(positionCity).areas.get(positionArea) != null &&
                    cityDatas.get(positionCity).areas.get(positionArea).streets != null &&
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet) != null &&
                    cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages != null) {
                size = cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.size();
            }
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSelected = itemView.findViewById(R.id.tvSelected);
        }
    }

    /**
     * 获取已选省position
     *
     * @param city
     * @return
     */
    public int getCityPosition(String city) {
        cityDatas = GsonU.getJsonData(mContext);
        if (cityDatas != null && !TextUtils.isEmpty(city)) {
            for (int p=0; p<cityDatas.size(); p++) {
                if (cityDatas.get(p).cityName.equals(city)) {
                    return p;
                }
            }
        }
        return -1;
    }

    /**
     * 获取city的position
     *
     * @param city
     * @param area
     * @return
     */
    public int getAreaPosition(String city, String area) {
        cityDatas = GsonU.getJsonData(mContext);
        if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)) {
            if (cityDatas != null) {
                for (int p=0; p<cityDatas.size(); p++) {
                    if (cityDatas.get(p).cityName.equals(city)) {// 找到province的position
                        if (cityDatas.get(p) != null && cityDatas.get(p).areas != null) {
                            for (int c=0; c<cityDatas.get(p).areas.size(); c++) {
                                if (cityDatas.get(p).areas.get(c).areaName.equals(area)) {
                                    return c;
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    public int getStreetPosition( String city, String area, String street) {
        cityDatas = GsonU.getJsonData(mContext);
        if (!TextUtils.isEmpty(area) && !TextUtils.isEmpty(city)) {
            if (cityDatas != null) {
                for (int p=0; p<cityDatas.size(); p++) {
                    if (cityDatas.get(p).cityName.equals(city)) {// 找到province的position
                        if (cityDatas.get(p) != null && cityDatas.get(p).areas != null) {
                            for (int c=0; c<cityDatas.get(p).areas.size(); c++) {
                                if (cityDatas.get(p).areas.get(c).areaName.equals(area)) {
                                    if (cityDatas.get(p).areas.get(c) != null && cityDatas.get(p).areas.get(c).streets != null) {
                                        for (int a=0; a<cityDatas.get(p).areas.get(c).streets.size(); a++) {
                                            if (cityDatas.get(p).areas.get(c).streets.get(a).streetName.equals(street)) {
                                                return a;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    public int getVillagePosition(String city, String area, String street, String village){
        cityDatas = GsonU.getJsonData(mContext);
        if(!TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)){
            if(cityDatas != null){
                for (int p=0; p<cityDatas.size(); p++) {
                    if (cityDatas.get(p).cityName.equals(city)) {
                        if (cityDatas.get(p) != null && cityDatas.get(p).areas != null) {
                            for (int c=0; c<cityDatas.get(p).areas.size(); c++) {
                                if (cityDatas.get(p).areas.get(c).areaName.equals(area)) {
                                    if (cityDatas.get(p).areas.get(c) != null && cityDatas.get(p).areas.get(c).streets != null) {
                                        for (int a=0; a<cityDatas.get(p).areas.get(c).streets.size(); a++) {
                                            if (cityDatas.get(p).areas.get(c).streets.get(a).streetName.equals(street)) {
                                                if(cityDatas.get(p).areas.get(c).streets.get(a) != null&& cityDatas.get(p).areas.get(c).streets.get(a).villages !=null){
                                                    for (int v=0; v<cityDatas.get(p).areas.get(c).streets.get(a).villages.size();v++){
                                                        if(cityDatas.get(p).areas.get(c).streets.get(a).villages.get(v).name.equals(village)){
                                                            return v;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        this.onItemCheckedListener = onItemCheckedListener;
    }

    public interface OnItemCheckedListener {
        void onItemChecked(int lastDataType, String checkedCity, String checkedArea, String checkedStreet, String checkedVillage);
    }
}
