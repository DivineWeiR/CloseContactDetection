package com.seucpss.contact_detection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import android.icu.text.Edits;


/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class Level3Activity extends AppCompatActivity implements View.OnClickListener {


    private DBOpenHelper mDBOpenHelper;
    private Button mBtLevel3activityReport;
    //private RelativeLayout mRlLevel3activityTop;
    //private ImageView mIvLevel3activityBack;
    private RelativeLayout mRlRelativeLayout1;
    private int check_number = 0;
    /**
     * 所需后台返回的数据格式list_date 包括日期，且list_date中的日期，需要和arr中的每一项一一对应。
     */
//    private String[][] arr = {{"华润苏果超市（殷巷店）35", "同仁医院35", "金鹰广场35"}, {"华润苏果超市（殷巷店）36", "同仁医院36", "金鹰广场36"}, {"华润苏果超市（殷巷店）37", "同仁医院37", "金鹰广场37"}, {"华润苏果超市（殷巷店）38", "同仁医院38", "金鹰广场38"}, {"华润苏果超市（殷巷店）39", "同仁医院39", "金鹰广场39"}, {"华润苏果超市（殷巷店）310"}, {"华润苏果超市（殷巷店）311", "同仁医院311"}, {"华润苏果超市（殷巷店）312", "同仁医院312", "金鹰广场312"}, {"华润苏果超市（殷巷店）313", "金鹰广场313"}, {"华润苏果超市（殷巷店）314", "同仁医院314", "金鹰广场314"}, {}, {"华润苏果超市（殷巷店）316", "同仁医院316", "金鹰广场316"}};
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<Integer> dateIds = new ArrayList<>();
    //    private ArrayList<ArrayList<String>> poiList = new ArrayList<>();
    //    private JSONArray dateList;
//    private JSONArray poiList;
    private int checkboxCount;
    private int childComponentCount = 0;
    private JSONObject jsonPatientsTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level3);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                "GjTuLlDZbPGLCQ0EAAPPxMQa8eGNFByT");
         mDBOpenHelper = new DBOpenHelper(this);
        initView();



        SharedPreferences share = getSharedPreferences("test",
                Context.MODE_PRIVATE);
        // 获取编辑器来存储数据到sharedpreferences中
        SharedPreferences.Editor editor = share.edit();
        editor.putString("id_number", "hh");
        // 将数据提交到sharedpreferences中
        editor.commit();
        //声明RL，CB，TX
        RelativeLayout aLayout = (RelativeLayout) this
                .findViewById(R.id.relativeLayout1);
        RelativeLayout.LayoutParams relativeLayoutParams = null;
        //此参数为给每个控件设置的id值
        int chk_id = 1;
        CheckBox checkBox = null;
        TextView textView = null;


        /**两层循环，从list_date中获得共有多少个日期，设置一个TextView来输出日期，之后，将每个arr中的对应地点输出
         * 第一层输出日期，第二层输出每个日期下面对应的地点
         */
        Iterator<String> dates = jsonPatientsTracks.keys();
        int counter = 0;
        while (dates.hasNext()) {
            String date = dates.next();
            textView = new TextView(this);
            textView.setId(chk_id += 1);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            //如果是第一个TextView，就放到最顶端
            if (0 == counter) {
                relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                //不是第一个TextView，就依次放置在最后已输出的CheckBox下面
                relativeLayoutParams.addRule(RelativeLayout.BELOW, chk_id - 1);
            }
            counter++;
            textView.setText(date);
            dateList.add(date);
            textView.setLayoutParams(relativeLayoutParams);
            aLayout.addView(textView);
            dateIds.add(childComponentCount);
            childComponentCount++;
            JSONArray pois = new JSONArray();
            try {
                pois = jsonPatientsTracks.getJSONArray(date);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < pois.length(); i++) {
                checkBox = new CheckBox(this);
                checkBox.setId(chk_id += 1);
                relativeLayoutParams = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                relativeLayoutParams.addRule(RelativeLayout.BELOW, chk_id - 1);
//                 checkBox.setText(arr[j][i]);
                String text = "";
                try {
                    text = pois.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkBox.setText(text);
                childComponentCount++;
                checkBox.setLayoutParams(relativeLayoutParams);
                aLayout.addView(checkBox);

            }
        }


    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.level3_menu, menu);
        return true;
    }


    private void initView() {

        mRlRelativeLayout1 = findViewById(R.id.relativeLayout1);
        mBtLevel3activityReport = findViewById(R.id.bt_level3activity_report);
        //mRlLevel3activityTop = findViewById(R.id.rl_level3activity_top);
        //mIvLevel3activityBack = findViewById(R.id.iv_level3activity_back);
        //mIvLevel3activityBack.setOnClickListener(this);
        mBtLevel3activityReport.setOnClickListener(this);
        jsonPatientsTracks = mDBOpenHelper.getPatientsTrackNames();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            //点击返回，返回主页面
            /** case R.id.iv_level3activity_back:
             Intent intent1 = new Intent(this, MainActivity.class);
             startActivity(intent1);
             finish();
             break;
             */
            //点击确认提交按钮
            case R.id.bt_level3activity_report:
//                StringBuffer sb = new StringBuffer();
                JSONArray track = new JSONArray();

//                int k = 1;
                //因为getChildAt（）方法会读取TextView导致，对CB的操作失败，采用两层循环，避开TextView
                int childIndex = 0;
                for (int j = 0; j < dateList.size(); j++) {
                    JSONArray dailyTrack = new JSONArray();
                    childIndex++;
                    int maxId;
                    if (j == dateList.size() - 1) {
                        maxId = childComponentCount;
                    } else {
                        maxId = dateIds.get(j + 1);
                    }
                    while (childIndex < maxId) {
                        CheckBox cb = (CheckBox) mRlRelativeLayout1.getChildAt(childIndex);
                        //每有一个复选框被点击，就将他的文本添加到sb中，且check_number +1
                        if (cb.isChecked()) {
//                            sb.append(cb.getText().toString());
                            JSONObject poi = new JSONObject();
                            try {
                                JSONObject time = new JSONObject();
                                time.put("starttime", "00:00");
                                time.put("endtime", "24:00");
                                poi.put("time", time);
                                JSONObject coordinate = new JSONObject();
                                coordinate.put("long", 0.0);
                                coordinate.put("lat", 0.0);
                                poi.put("coordinate", coordinate);
                                poi.put("name", cb.getText().toString());
                                dailyTrack.put(poi);
                                check_number += 1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        childIndex++;
                    }
                    try {
                        JSONObject daily_item = new JSONObject();
                        daily_item.put("date", dateList.get(j));
                        daily_item.put("pois", dailyTrack);
                        track.put(daily_item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    k += arr[j].length + 1;
//                    System.out.println(k);

                }

//如果有check_number被选中，就写入数据库的track中，提示提交成功，并跳转至主页面。若无选中，则提示无风险，跳转至主页面
                if (check_number != 0) {
//                    mDBOpenHelper.add_level3(sb.toString());
                    User user = mDBOpenHelper.getAllData().get(0);
                    final HashMap<String, String> user_info = new HashMap<>();
                    user_info.put("name", user.getName());
                    user_info.put("id_number", user.getId_number());
                    user_info.put("gender", user.getSexual());
                    user_info.put("address", user.getAddress());
                    user_info.put("phone_number", user.getPhone_number());
                    user_info.put("village_code", user.getVillageCode());
                    user_info.put("status", "1");
                    user_info.put("track", track.toString());
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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                        dialog.setIcon(R.mipmap.ic_launcher_round);
                        dialog.setTitle("反馈信息");
                        dialog.setMessage("您当前被认定为【" + TypeTransferHelper.transStatusCode(1) + "】，请注意！");
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

