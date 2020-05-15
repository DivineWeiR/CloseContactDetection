package com.seucpss.contact_detection;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.seucpss.contact_detection.Adapter.Level2Adapter;
import com.seucpss.contact_detection.Bean.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RouteAddActivity extends AppCompatActivity implements View.OnClickListener {

    // 定义显示时间控件
    private Calendar calendar; // 通过Calendar获取系统时间
    //记录开始时间
    private int sYear;
    private int sMonth;
    private int sDay;
    private int sHour;
    private int sMinute;

    //记录结束时间
    private int eYear;
    private int eMonth;
    private int eDay;
    private int eHour;
    private int eMinute;

    //时间输入框
    private EditText et_st;
    private EditText et_et;
    private EditText et_sd;
    private EditText et_ed;

    private DBOpenHelper mDBOpenHelper;

    private String pname;
    private double lat;
    private double lon;

    private List<Position> list;
    private ListView listView;
    private Level2Adapter adapter;
    private JSONObject jsonPatientsTracks;

    private Button submit_btn;//计算二级响应风险按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        setContentView(R.layout.activity_route_add);

        //获取地点搜索跳转信息
        Intent intent = getIntent();
        pname = intent.getStringExtra("name");
        lat = intent.getDoubleExtra("lat", 0.00);
        lon = intent.getDoubleExtra("lon", 0.00);
        SuggestionResult.SuggestionInfo position = (SuggestionResult.SuggestionInfo) intent.getSerializableExtra("position");

        mDBOpenHelper = new DBOpenHelper(this);
        list = mDBOpenHelper.getLevel2Data();//获取用户已提交的二级响应信息

        listView = findViewById(R.id.list_view);//用户已提交二级响应行程列表
        adapter = new Level2Adapter(RouteAddActivity.this, R.layout.level2_list, list, mDBOpenHelper);
        listView.setAdapter(adapter);
        initView();
        et_st = (EditText) findViewById(R.id.starttime);
        et_st.setText("起始时间");
        et_et = findViewById(R.id.endtime);
        et_et.setText("结束时间");
        et_sd = (EditText) findViewById(R.id.startdate);
        et_sd.setText("起始日期");
        et_ed = findViewById(R.id.enddate);
        et_ed.setText("结束日期");

        et_st.setFocusable(false);
        et_st.setFocusableInTouchMode(false);
        et_et.setFocusable(false);
        et_et.setFocusableInTouchMode(false);
        et_sd.setFocusable(false);
        et_sd.setFocusableInTouchMode(false);
        et_ed.setFocusable(false);
        et_ed.setFocusableInTouchMode(false);

        TextView name = findViewById(R.id.name);
        TextView addstr = findViewById(R.id.addstr);
        Button addbtn = findViewById(R.id.addbtn);
        name.setText(intent.getStringExtra("name"));
        addstr.setText(intent.getStringExtra("adr"));

        calendar = Calendar.getInstance();

        //起始日期时间、结束日期时间输入框设置
        et_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RouteAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                // TODO Auto-generated method stub
                                sYear = year;
                                sMonth = month;
                                sDay = day;
                                // 更新EditText控件日期 小于10加0
                                et_sd.setText(new StringBuilder()
                                        .append(sYear)
                                        .append("-")
                                        .append((sMonth + 1) < 10 ? "0"
                                                + (sMonth + 1) : (sMonth + 1))
                                        .append("-")
                                        .append((sDay < 10) ? "0" + sDay : sDay));
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RouteAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                // TODO Auto-generated method stub
                                eYear = year;
                                eMonth = month;
                                eDay = day;
                                // 更新EditText控件日期 小于10加0
                                et_ed.setText(new StringBuilder()
                                        .append(eYear)
                                        .append("-")
                                        .append((eMonth + 1) < 10 ? "0"
                                                + (eMonth + 1) : (eMonth + 1))
                                        .append("-")
                                        .append((eDay < 10) ? "0" + eDay : eDay));
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(RouteAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                sHour = hourOfDay;
                                sMinute = minute;
                                et_st.setText(new StringBuilder()
                                        .append((sHour) < 10 ? "0" + (sHour) : (sHour))
                                        .append(":")
                                        .append((sMinute < 10) ? "0" + sMinute : sMinute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar
                        .get(Calendar.MINUTE), true).show();
            }
        });
        et_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(RouteAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                eHour = hourOfDay;
                                eMinute = minute;
                                et_et.setText(new StringBuilder()
                                        .append((eHour) < 10 ? "0" + (eHour) : (eHour))
                                        .append(":")
                                        .append((eMinute < 10) ? "0" + eMinute : eMinute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar
                        .get(Calendar.MINUTE), true).show();
            }
        });


        //添加二级响应行程按钮事件
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                String starttime = sYear + "-" + ((sMonth + 1) < 10 ? "0"
                        + (sMonth + 1) : (sMonth + 1)) + "-" + ((sDay < 10) ? "0" + sDay : sDay) + "-" + ((sHour) < 10 ? "0" + (sHour) : (sHour))
                        + ":" + ((sMinute < 10) ? "0" + sMinute : sMinute);
                String endtime = eYear + "-" + ((eMonth + 1) < 10 ? "0"
                        + (eMonth + 1) : (eMonth + 1)) + "-" + ((eDay < 10) ? "0" + eDay : eDay) + "-" + ((eHour) < 10 ? "0" + (eHour) : (eHour))
                        + ":" + ((eMinute < 10) ? "0" + eMinute : eMinute);
                Date startdate = null;
                Date enddate = null;
                try {
                    startdate = simpleDateFormat.parse(starttime);
                    enddate = simpleDateFormat.parse(endtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long start = startdate.getTime();
                long end = enddate.getTime();
                mDBOpenHelper.add_level2(pname, start, end, lat, lon);
                Position position1 = new Position(pname, lat, lon, start, end);
                position1.setId(mDBOpenHelper.getMaxId());
                list.add(position1);
                adapter.notifyDataSetChanged();//刷新已添加行程列表显示
            }
        });
    }

    private void initView() {
        submit_btn = findViewById(R.id.btn_level2activity_submit);
        submit_btn.setOnClickListener(this);
    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.level3_menu, menu);
        return true;
    }

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public void onClick(View view) {
        System.out.println("Level2 Submit");
        switch (view.getId()) {
            case R.id.btn_level2activity_submit:
                List<Position> positions;
                int low = 0;
                int medium = 0;
                int high = 0;
                int status_code = 0;
                try {

                    jsonPatientsTracks = mDBOpenHelper.getPatientsTracks();
                    Iterator<String> dates = jsonPatientsTracks.keys();
                    int counter = 0;
                    while (dates.hasNext()) {
                        String date = dates.next();
                        JSONArray pois = jsonPatientsTracks.getJSONArray(date);
                        for (int j = 0; j < pois.length(); j++) {
                            JSONObject singlePoi = pois.getJSONObject(j);

                            String name = singlePoi.getString("name");

                            JSONObject jsonTime = singlePoi.getJSONObject("time");
                            String startTime = jsonTime.getString("starttime");
                            String endTime = jsonTime.getString("endtime");

                            long stime = TypeTransferHelper.transTimeStrToTimestamp(date, startTime, "yyyy-MM-dd-HH:mm");
                            long etime = TypeTransferHelper.transTimeStrToTimestamp(date, endTime, "yyyy-MM-dd-HH:mm");
                            positions = mDBOpenHelper.get2times(stime, etime);
                            for (int m = 0; m < positions.size() - 1; m++) {
                                for (int n = positions.size() - 1; n > m; n--) {
                                    if (list.get(n).getId() == (list.get(m).getId())) {
                                        list.remove(n);
                                    }
                                }
                            }
                            JSONObject jsonCoordinate = singlePoi.getJSONObject("coordinate");
                            double aLong = jsonCoordinate.getDouble("long");
                            double aLat = jsonCoordinate.getDouble("lat");

                            //计算风险度
                            for (int a = 0; a < positions.size(); a++) {
                                double length = getDistance(aLong, aLat, positions.get(a).getLon(), positions.get(a).getLat());
                                if (length <= 50) {
                                    high++;
                                } else if (length > 50 && length <= 500) {
                                    medium++;
                                } else if (length > 500 && length <= 1000) {
                                    low++;
                                }
                            }
                        }
                    }
                    if (low > 0) {
                        status_code = 1;
                    }
                    if (medium > 0) {
                        status_code = 2;
                    }
                    if (high > 0) {
                        status_code = 3;
                    }

                }  catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (status_code > 0) {
                    User user = mDBOpenHelper.getAllData().get(0);
                    final HashMap<String, String> user_info = user.getUserInfo();
                    user_info.put("status", status_code+"");
                    user_info.put("track", mDBOpenHelper.get14DayRoute().toString());
                    System.out.println(user_info.toString());
                    final NetworkHelper mNetworkHelper = new NetworkHelper();

                    final boolean[] success = new boolean[1];
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                success[0] = mNetworkHelper.submitDataByDoPost(user_info, "/post_self_data/");
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
                        Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
                        android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                        dialog.setIcon(R.mipmap.ic_launcher_round);
                        dialog.setTitle("反馈信息");
                        dialog.setMessage("您当前被认定为【" + TypeTransferHelper.transStatusCode(status_code) + "】，请注意！");
                        dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(this, "传输失败！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "您没有感染风险", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lng1 经度1
     * @param lat1 纬度1
     * @param lng2 经度2
     * @param lat2 纬度2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2,
                                     double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }


    //菜单触发事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.item_help:
                User user = mDBOpenHelper.getAllData().get(0);
                final HashMap<String, String> user_info = new HashMap<>();
                user_info.put("name", user.getName());
                user_info.put("id_number", user.getId_number());
                user_info.put("gender", user.getSexual());
                user_info.put("address", user.getAddress());
                user_info.put("phone_number", user.getPhone_number());
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
