package com.seucpss.contact_detection;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.seucpss.contact_detection.Adapter.SearchMapAdapter;
import com.seucpss.contact_detection.Bean.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class Level2Activity extends AppCompatActivity {


    private MapView mMapView = null;
    private static final int REQUEST_PERMISSION = 1;//权限请求码
    private ArrayList<String> permissionList = new ArrayList<>();//权限集合
    private boolean ToastFlag = true;
    private boolean first = true;
    private LocationClient locationClient;
    private BaiduMap baiduMap;//百度地图实例
    private ListView placeList;
    private String city;

    private SuggestionSearch mSuggestionSearch;
    private DBOpenHelper mDBOpenHelper;

    private double lat;
    private double lon;
    private double lat2;
    private double lon2;
    private String Time;
    private Polyline mPolyline;
    private JSONObject jsonPatientsTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_level2);

        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                "GjTuLlDZbPGLCQ0EAAPPxMQa8eGNFByT");

        mMapView = (MapView) findViewById(R.id.bmapView);//地图控件
        placeList = (ListView) findViewById(R.id.list_view);//地点搜索关键词建议框

        mDBOpenHelper = new DBOpenHelper(this);//数据库查询对象

        Button btn = (Button) findViewById(R.id.show_route);//搜索一级响应历史路径按钮

        //初始化地图
        //当android系统小于5.0的时候直接定位显示，不用动态申请权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initialMap();
        } else {
            Level2ActivityPermissionsDispatcher.ApplySuccessWithCheck(this);
        }

        showlist();//在地图上展示服务器端发送的患者出没地点

        //监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        //编写监听器
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //处理sug检索结果
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    Log.i("result: ", "没有找到");
                    return;
                    //未找到相关结果
                } else {
                    //获取在线建议检索结果，并显示到listview中
                    List<SuggestionResult.SuggestionInfo> resl = suggestionResult.getAllSuggestions();
                    SearchMapAdapter Adapter = new SearchMapAdapter(Level2Activity.this, R.layout.position_search_list, resl, baiduMap);
                    placeList.setAdapter(Adapter);
                }
            }
        };


        //地点搜索输入框
        EditText editText = (EditText) findViewById(R.id.Editview);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // s  未改变之前的内容
                // start 内容被改变的的开始位置
                // count 原始文字被删除的个数
                // after 新添加的文字个数
                //========================= start 和 count 结合 获取被删除的文字 ===================
                // String deleteText = s.toString().substring(start + 1 - count, start + 1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // s  改变之后的内容
                // start 内容被改变的的开始位置
                // before 原始文字被删除的个数
                // count 新添加的文字个数
                //========================= start 和 count 结合 获取新添加的文字 ===================
                String addText = s.toString().substring(start, start + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // s  改变之后的最终内容
                //设置请求参数：keyword搜索的关键字，城市可以固定，城市限制设定为否。
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword("" + s)
                        .city(city)
                        .citylimit(false)
                );
            }
        });
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
        Button button = findViewById(R.id.show_route);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Level2Activity.this, RouteAddActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (locationClient != null) {
            locationClient.stop();
        }
        baiduMap.setMyLocationEnabled(false);

    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.level3_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        Level2ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 申请权限成功时
     */
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void ApplySuccess() {
        initialMap();
    }

    /**
     * 申请权限告诉用户原因时
     *
     * @param request
     */
    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleForMap(PermissionRequest request) {
        showRationaleDialog("使用此功能需要打开定位的权限", request);
    }

    /**
     * 申请权限被拒绝时
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapDenied() {
        Toast.makeText(this, "你拒绝了权限，该功能不可用", Toast.LENGTH_LONG).show();
    }

    /**
     * 申请权限被拒绝并勾选不再提醒时
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapNeverAskAgain() {
        AskForPermission();
    }

    /**
     * 告知用户具体需要权限的原因
     *
     * @param messageResId
     * @param request
     */
    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();//请求权限
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    /**
     * 被拒绝并且不再提醒,提示用户去设置界面重新打开权限
     */
    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("当前应用缺少定位权限,请去设置界面打开\n打开之后按两次返回键可回到该应用哦");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + Level2Activity.this.getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void initialMap() {
        //拿到地图实例
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);//在地图上显示当前位置(小圆点)
        MapStatusUpdate updateZoom = MapStatusUpdateFactory.zoomTo(18f);//显示初始地图缩放大小
        baiduMap.animateMapStatus(updateZoom);//更新地图状态
        locationClient = new LocationClient(this);
        initLocation();
        locationClient.registerLocationListener(new Level2Activity.MyLocationListener());
        locationClient.start();
    }

    //初始化百度地图定位参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 60000;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        option.setLocationNotify(false);
        locationClient.setLocOption(option);
    }

    //设置定位监听
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
            location.getTime();    //获取定位时间
            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();    //获取定位类型
            location.getLatitude();    //获取纬度信息
            location.getLongitude();    //获取经度信息
            location.getRadius();    //获取定位精准度
            location.getAddrStr();    //获取地址信息
            location.getCountry();    //获取国家信息
            location.getCountryCode();    //获取国家码
            location.getCity();    //获取城市信息
            location.getCityCode();    //获取城市码
            location.getDistrict();    //获取区县信息
            location.getStreet();    //获取街道信息
            location.getStreetNumber();    //获取街道码
            location.getLocationDescribe();    //获取当前位置描述信息
            location.getPoiList();    //获取当前位置周边POI信息

            location.getBuildingID();    //室内精准定位下，获取楼宇ID
            location.getBuildingName();    //室内精准定位下，获取楼宇名称
            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息
            //经纬度
            lat = location.getLatitude();
            lon = location.getLongitude();
            city = location.getCity();
            System.out.println(location.getLocType());
            //这个判断是为了防止每次定位都重新设置中心点和marker
            if (ToastFlag) {
                ToastFlag = false;
                Time = location.getTime();
                lat2 = location.getLatitude();
                lon2 = location.getLongitude();
            }
            if (true) {
                Time = location.getTime();
                lat2 = location.getLatitude();
                lon2 = location.getLongitude();
                setPosition2Center(baiduMap, location, true);
            }
        }
    }

    //在地图上显示当前定位点，并每隔五分钟记录一次定位到一级响应数据库
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);

        if (first) {
            Toast.makeText(Level2Activity.this, bdLocation.getAddrStr(), Toast.LENGTH_LONG).show();
            first = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
        if (isShowLoc) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);

            if (minute % 5 == 0) {

                mDBOpenHelper.add_level1(bdLocation.getAddrStr(), date.getTime(), bdLocation.getLatitude(), bdLocation.getLongitude());
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                String sim2 = dateFormat2.format(date);
            }


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;//不执行父类点击事件
        }

        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件

    }

    //在地图上展示患者出没位置
    public void showlist() {

        try {
            jsonPatientsTracks = mDBOpenHelper.getPatientsTracks();
            Iterator<String> dates = jsonPatientsTracks.keys();
            int counter = 0;
            while (dates.hasNext()) {
                String date = dates.next();
                JSONArray pois = jsonPatientsTracks.getJSONArray(date);
                for (int j = 0; j < pois.length(); j++) {
                    JSONObject singlePoi = pois.getJSONObject(j);

                    String name = singlePoi.getString("name");//地点名

                    JSONObject timeobj = singlePoi.getJSONObject("time");
                    String starttime = timeobj.getString("starttime");
                    String endtime = timeobj.getString("endtime");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");//小写的mm表示的是分钟
                    String st = date + "-" + starttime;
                    String et = date + "-" + endtime;
                    Date st_date = null;
                    Date et_date = null;
                    try {
                        st_date = sdf.parse(st);
                        et_date = sdf.parse(et);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Long stime = st_date.getTime();
                    Long etime = et_date.getTime();

                    JSONObject coordinateobj = singlePoi.getJSONObject("coordinate");
                    Double aLong = Double.valueOf(coordinateobj.getString("long"));
                    Double lat = Double.valueOf(coordinateobj.getString("lat"));

                    LatLng point = new LatLng(lat, aLong);
                    Position position = new Position(name, lat, aLong, stime, etime);
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.virus2);
                    Bundle args = new Bundle(); //这个用于在所画的点上附加相关信息
                    // 构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).zIndex(9).draggable(false).icon(bitmap);
                    Marker marker = (Marker) baiduMap.addOverlay(option);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("position", position);
                    marker.setExtraInfo(bundle);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //设置图标点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                Position position = (Position) bundle.getSerializable("position");
                showMyDialog(position);
                return true;
            }
        });
    }

    private void showMyDialog(final Position stationsBeanThis) {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String start = sDateFormat.format(new Date(stationsBeanThis.getStarttime() + 0));
        String end = sDateFormat.format(new Date(stationsBeanThis.getEndtime() + 0));
        String t = start + "\n到\n" + end;

        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .setTitle(stationsBeanThis.getPname())
                .setCancelable(false)
                .setMessage(t)
                .setIcon(R.drawable.virus2)
                .show();
    }


    //在地图上搜索显示历史路径
    public void showRoute(String d) {
        if (mPolyline != null) {
            mPolyline.remove();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");//小写的mm表示的是分钟
        String dstr = d + "-00:00:00";
        Date date = null;
        try {
            date = sdf.parse(dstr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time = date.getTime();
        //获取运动后的定位点
        List<Position> list = mDBOpenHelper.getAll3Data(time);

        if (list.size() < 2) {
            new AlertDialog.Builder(this)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false)
                    .setMessage("无数据记录")
                    .show();
            return;
        }
        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LatLng latLng = new LatLng(list.get(i).getLat(), list.get(i).getLon());
            latLngs.add(latLng);
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);

        //在地图上画出线条图层，mPolyline：线条图层
        mPolyline = (Polyline) mMapView.getMap().addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
    }


    //菜单触发事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_help:
                User user = mDBOpenHelper.getAllData().get(0);
                final HashMap<String, String> user_info = new HashMap<>();
                user_info.put("name", user.getName());
                user_info.put("id_number", user.getId_number());
                user_info.put("gender", user.getSexual());
                user_info.put("address", user.getAddress());
                user_info.put("phone_number", user.getPhone_number());
                user_info.put("village_code", user.getVillageCode());
                final NetworkHelper mNetworkHelper = new NetworkHelper();

                final boolean[] success = new boolean[1];
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[0] = mNetworkHelper.submitDataByDoPost(user_info, "/post_help/");
                            System.out.println(success[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(success[0]);
                            success[0] = false;
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("out" + success[0]);
                if (success[0]) {
                    Toast.makeText(this, "求助成功！", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    Toast.makeText(this, "传输失败！", Toast.LENGTH_SHORT).show();
                }
                /**个人求助按钮的触发事件
                 case R.id.item_help:
                 Intent intent2 = new Intent(this, MainActivity.class);
                 startActivity(intent2);
                 finish();
                 break;*/

            case R.id.item_report:
                Intent intent3 = new Intent(this, ReportActivity.class);
                startActivity(intent3);
//                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
