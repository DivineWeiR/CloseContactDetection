package com.seucpss.contact_detection;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by chen.yingjie on 2019/3/23
 */
public class RegionPopupWindow extends LinearLayout {

    FrameLayout flFork;// 叉
    TextView tvProvince;
    TextView tvCity;
    TextView tvArea;
    TextView tvVillage;
    View bottomLineProvince;
    View bottomLineCity;
    View bottomLineArea;
    View bottomLineVillage;
    RecyclerView recycleView;

    private OnForkClickListener onForkClickListener;
    private OnRpwItemClickListener onRpwItemClickListener;

    private RegionAdapter regionAdapter;
    private List<RegionBean> cityDatas;
    private LinearLayoutManager recycleManager;
    private int mType;

    private String checkCity;
    private String checkArea;
    private String checkStreet;
    private String checkVillage;

    public RegionPopupWindow(Context context) {
        this(context, null);
    }

    public RegionPopupWindow(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegionPopupWindow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.region_popupwindow, this, true);
        setBackgroundResource(R.color.white);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
        bindListeners();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    private void initView() {
        cityDatas = GsonU.getJsonData(getContext());

        if (mType == Config.TYPE_EDIT) {// 编辑模式
            // 省显示黑色选定值，底线隐藏
            tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
            tvProvince.setText(checkCity);
            bottomLineProvince.setVisibility(GONE);
            // 市显示红色"请选择"， 底线显示
            tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
            tvCity.setText(checkArea);
            bottomLineCity.setVisibility(GONE);
            // 县/区不显示，底线隐藏
            tvArea.setText(checkStreet);
            tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
            bottomLineArea.setVisibility(GONE);
            //需要注意有关四级
            tvVillage.setText(checkVillage);
            tvVillage.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
            bottomLineVillage.setVisibility(VISIBLE);

            regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_VILLAGE, checkCity, checkArea, checkStreet, checkVillage);

            // 定位到已选项
            int targetPosition = regionAdapter.getVillagePosition( checkCity, checkArea, checkStreet,checkVillage);
            scrollToPosition(targetPosition);
        } else if (mType == Config.TYPE_ADD) {// 添加模式
            //  第一次进来，没有选择地址。
            if (TextUtils.isEmpty(checkCity) && TextUtils.isEmpty(checkArea) && TextUtils.isEmpty(checkStreet) && TextUtils.isEmpty(checkVillage)) {
                // 初始状态
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                tvProvince.setText("请选择");
                bottomLineProvince.setVisibility(VISIBLE);

                tvCity.setText("");
                bottomLineCity.setVisibility(GONE);

                tvArea.setText("");
                bottomLineArea.setVisibility(GONE);

                tvVillage.setText("");
                bottomLineVillage.setVisibility(GONE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_CITY, checkCity, checkArea, checkStreet, checkVillage);
            } else {//  选择过地址，再次选择。
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
                tvProvince.setText(checkCity);
                bottomLineProvince.setVisibility(GONE);

                // 市显示红色"请选择"， 底线显示
                tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
                tvCity.setText(checkArea);
                bottomLineCity.setVisibility(GONE);

                // 县/区不显示，底线隐藏
                tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
                tvArea.setText(checkStreet);
                bottomLineArea.setVisibility(GONE);

                tvVillage.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                tvVillage.setText(checkVillage);
                bottomLineVillage.setVisibility(VISIBLE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_VILLAGE, checkCity, checkArea, checkStreet, checkVillage);

                // 定位到已选项
                int targetPosition = regionAdapter.getVillagePosition(checkCity, checkArea, checkStreet, checkVillage);
                scrollToPosition(targetPosition);
            }
        }
    }

    private void findViews() {
        flFork = findViewById(R.id.flFork);
        tvProvince = findViewById(R.id.tvProvince);
        tvCity = findViewById(R.id.tvCity);
        tvArea = findViewById(R.id.tvArea);
        tvVillage = findViewById(R.id.tvVillage);
        bottomLineProvince = findViewById(R.id.bottomLineProvince);
        bottomLineCity = findViewById(R.id.bottomLineCity);
        bottomLineArea = findViewById(R.id.bottomLineArea);
        bottomLineVillage = findViewById(R.id.bottomLineVillage);
        recycleView = findViewById(R.id.recycleView);

        recycleManager = new LinearLayoutManager(getContext());
        regionAdapter = new RegionAdapter(getContext());
        recycleView.setLayoutManager(recycleManager);
        recycleView.setAdapter(regionAdapter);
    }

    private void bindListeners() {
        // 点击省
        tvProvince.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 省显示黑色选定值，底线隐藏
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                bottomLineProvince.setVisibility(VISIBLE);

                // 市显示红色"请选择"， 底线显示
                tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineCity.setVisibility(GONE);

                // 县/区不显示，底线隐藏
                tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineArea.setVisibility(GONE);

                tvVillage.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineVillage.setVisibility(GONE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_CITY, checkCity, checkArea, checkStreet, checkVillage);

                // 定位到已选项
                int targetPosition = regionAdapter.getCityPosition(checkCity);
                scrollToPosition(targetPosition);
            }
        });

        // 点击市
        tvCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 省显示黑色选定值，底线隐藏
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineProvince.setVisibility(GONE);

                // 市显示红色"请选择"， 底线显示
                tvCity.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                bottomLineCity.setVisibility(VISIBLE);

                // 县/区不显示，底线隐藏
                tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineArea.setVisibility(GONE);

                tvVillage.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineVillage.setVisibility(GONE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_AREA,  checkCity, checkArea, checkStreet, checkVillage);

                // 定位到已选项
                int targetPosition = regionAdapter.getAreaPosition( checkCity, checkArea);
                scrollToPosition(targetPosition);
            }
        });

        // 点击县/区
        tvArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineProvince.setVisibility(GONE);

                // 市显示红色"请选择"， 底线显示
                tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineCity.setVisibility(GONE);

                // 县/区不显示，底线隐藏
                tvArea.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                bottomLineArea.setVisibility(VISIBLE);

                tvVillage.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineVillage.setVisibility(GONE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_STREET,  checkCity, checkArea, checkStreet,checkVillage);

                // 定位到已选项
                int targetPosition = regionAdapter.getStreetPosition(checkCity, checkArea, checkStreet);
                scrollToPosition(targetPosition);
            }
        });

        tvVillage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineProvince.setVisibility(GONE);

                // 市显示红色"请选择"， 底线显示
                tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineCity.setVisibility(GONE);

                // 县/区不显示，底线隐藏
                tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
                bottomLineArea.setVisibility(GONE);

                tvVillage.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                bottomLineVillage.setVisibility(VISIBLE);

                regionAdapter.refreshData(cityDatas, RegionAdapter.DATA_VILLAGE,  checkCity, checkArea, checkStreet,checkVillage);

                // 定位到已选项
                int targetPosition = regionAdapter.getVillagePosition(checkCity, checkArea, checkStreet,checkVillage);
                scrollToPosition(targetPosition);
            }
        });

        // 点击❌
        flFork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onForkClickListener.onForkClick();
            }
        });

        regionAdapter.setOnItemCheckedListener(new RegionAdapter.OnItemCheckedListener() {
            @Override
            public void onItemChecked(int lastDataType,  String checkedCity, String checkedArea, String checkedStreet, String checkedVillage) {
                int newDataType = 0;
                // 根据上次点击的类型 赋予新值。
                if (lastDataType == RegionAdapter.DATA_CITY) {// 点击了 省，该选择市
                    newDataType = RegionAdapter.DATA_AREA;

                    checkCity = checkedCity;
                    checkArea = checkedArea;
                    checkStreet = checkedStreet;
                    checkVillage = checkedVillage;

                    // 省显示黑色选定值，底线隐藏
                    tvProvince.setTextColor(getContext().getResources().getColor(R.color.v333333));
                    tvProvince.setText(checkCity);
                    bottomLineProvince.setVisibility(GONE);

                    // 市显示红色"请选择"， 底线显示
                    tvCity.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                    tvCity.setText("请选择");
                    bottomLineCity.setVisibility(VISIBLE);

                    // 县/区不显示，底线隐藏
                    tvArea.setText(checkStreet);
                    bottomLineArea.setVisibility(GONE);

                    tvVillage.setText(checkVillage);
                    bottomLineVillage.setVisibility(GONE);

                    // 如果选择了省，把已选择市和县/区置空。
                    checkArea = "";
                    checkStreet = "";

                    checkVillage = "";

                } else if (lastDataType == RegionAdapter.DATA_AREA) {// 点击了 市，该选择县/区
                    newDataType = RegionAdapter.DATA_STREET;

                    checkCity = checkedCity;
                    checkArea = checkedArea;
                    checkStreet = checkedStreet;
                    checkVillage = checkedVillage;

                    // 省显示黑色选定值，底线隐藏

                    // 市显示黑色选定值，底线隐藏
                    tvCity.setTextColor(getContext().getResources().getColor(R.color.v333333));
                    tvCity.setText(checkArea);
                    bottomLineCity.setVisibility(GONE);

                    // 县/区显示红色"请选择，底线显示
                    tvArea.setText("请选择");
                    tvArea.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                    bottomLineArea.setVisibility(VISIBLE);

                    tvVillage.setText(checkVillage);
                    bottomLineVillage.setVisibility(GONE);
                    // 如果选择了市，就把已选择县/区置空。
                    checkStreet = "";
                    checkVillage = "";
                } else if(lastDataType == RegionAdapter.DATA_STREET){
                    newDataType = RegionAdapter.DATA_VILLAGE;

                    checkCity = checkedCity;
                    checkArea = checkedArea;
                    checkStreet = checkedStreet;
                    checkVillage = checkedVillage;

                    // 市显示黑色选定值，底线隐藏
                    tvArea.setTextColor(getContext().getResources().getColor(R.color.v333333));
                    tvArea.setText(checkStreet);
                    bottomLineArea.setVisibility(GONE);

                    // 县/区显示红色"请选择，底线显示
                    tvVillage.setText("请选择");
                    tvVillage.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                    bottomLineVillage.setVisibility(VISIBLE);
                    if (TextUtils.isEmpty(checkCity)) {
                        checkCity = tvProvince.getText().toString();
                    }
                    if (TextUtils.isEmpty(checkArea)) {
                        checkArea = tvCity.getText().toString();
                    }

                    checkVillage = "";
                } else if (lastDataType == RegionAdapter.DATA_VILLAGE){// 点击了 县/区

                    checkCity = checkedCity;
                    checkArea = checkedArea;
                    checkStreet = checkedStreet;
                    checkVillage = checkedVillage;
                    // 省显示黑色选定值，底线隐藏

                    // 市显示黑色选定值，底线隐藏

                    // 县/区显示红色选定值，底线显示
                    tvVillage.setText(checkVillage);
                    // 回传
                    if (TextUtils.isEmpty(checkCity)) {
                        checkCity = tvProvince.getText().toString();
                    }
                    if (TextUtils.isEmpty(checkArea)) {
                        checkArea = tvCity.getText().toString();
                    }
                    if (TextUtils.isEmpty(checkStreet)) {
                        checkStreet = tvArea.getText().toString();
                    }
                    if (TextUtils.isEmpty(checkVillage)) {
                        checkVillage = tvVillage.getText().toString();
                    }
                    onRpwItemClickListener.onRpwItemClick( replace(checkCity), replace(checkArea), replace(checkStreet), replace(checkVillage));
                }
                regionAdapter.refreshData(cityDatas, newDataType,  checkCity, checkArea, checkStreet,  checkVillage);
                scrollToPosition(0);
            }
        });
    }

    private void scrollToPosition(int position) {
        if (position == -1) {
            recycleManager.scrollToPositionWithOffset(0, 0);
        } else {
            recycleManager.scrollToPositionWithOffset(position, 0);
        }
    }

    // "请选择"用""代替
    private String replace(String text) {
        String newText = "";
        if (text.equals("请选择")) {
            return newText;
        }
        return text;
    }

    public void setHistory(int mType,  String city, String area, String street, String village) {
        this.mType = mType;

        this.checkCity = city;
        this.checkArea = area;
        this.checkStreet = street;
        this.checkVillage = village;
    }

    public void setOnForkClickListener(OnForkClickListener onForkClickListener) {
        this.onForkClickListener = onForkClickListener;
    }

    public void setOnRpwItemClickListener(OnRpwItemClickListener onRpwItemClickListener) {
        this.onRpwItemClickListener = onRpwItemClickListener;
    }

    // 叉点击回调
    public interface OnForkClickListener {
        void onForkClick();
    }

    // 城市/县/区列表item点击回调
    public interface OnRpwItemClickListener {
        void onRpwItemClick( String selectedCity, String selectedArea, String selectedStreet, String selectedVillage);
    }
}
