package com.seucpss.contact_detection;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 */

/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class ReportActivity extends AppCompatActivity implements View.OnClickListener {


    private DBOpenHelper mDBOpenHelper;
    private Button mBtReportactivityReport;
    private Button mBtReportactivityBack;
    //private RelativeLayout mRlReportactivityTop;
    //private ImageView mIvReportactivityBack;
    private LinearLayout mLlReportactivityBody;
    private EditText mEtReportactivityName;
    private EditText mEtReportactivitySexual;
    private EditText mEtReportactivityDiscovery_address;
    private EditText mEtReportactivityPhone_number;
    private EditText mEtReportactivityAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initView();
        // 为点击事件设置监听器
        setOnClickListener();
         /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(mEtReportactivityPhone_number, "phone_number", "手机号格式不正确");
        setOnFocusChangeErrMsg(mEtReportactivitySexual, "sexual", "性别只能填男或女");


        mDBOpenHelper = new DBOpenHelper(this);

    }


    private void initView() {
        mBtReportactivityBack = findViewById(R.id.bt_reportactivity_back);
        mBtReportactivityReport = findViewById(R.id.bt_reportactivity_report);
        //mRlReportactivityTop = findViewById(R.id.rl_reportactivity_top);
        //mIvReportactivityBack = findViewById(R.id.iv_reportactivity_back);
        mLlReportactivityBody = findViewById(R.id.ll_reportactivity_body);
        mEtReportactivityDiscovery_address = findViewById(R.id.et_reportactivity_discovery_address);
        mEtReportactivityName = findViewById(R.id.et_reportactivity_name);
        mEtReportactivitySexual = findViewById(R.id.et_reportactivity_sexual);
        mEtReportactivityPhone_number = findViewById(R.id.et_reportactivity_phone_number);
        mEtReportactivityAddress = findViewById(R.id.et_reportactivity_address);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         * 举报页面能点击的就三个地方
         * top处返回箭头、举报按钮
         */
        //mIvReportactivityBack.setOnClickListener(this);
        mBtReportactivityReport.setOnClickListener(this);
        mBtReportactivityBack.setOnClickListener(this);
    }

    private void setOnFocusChangeErrMsg(final EditText editText, final String inputType, final String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus) {
                            if (inputType == "phone_number") {
                                if (isPhone_numberValid(inputStr)) {
                                    editText.setError(null);
                                } else {
                                    editText.setError(errMsg);
                                }
                            }
                            if (inputType == "sexual") {
                                if (isSexualValid(inputStr)) {
                                    editText.setError(null);
                                } else {
                                    editText.setError(errMsg);
                                }
                            }
                        }
                    }
                }
        );
    }

    // 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
    private boolean isPhone_numberValid(String phonenumber) {
        if (phonenumber == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(phonenumber);
        return m.matches();
    }

    // 性别只能填男或女
    private boolean isSexualValid(String gender) {
        return gender.equals("男") || gender.equals("女");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnClickListener() {
        mBtReportactivityReport.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_reportactivity_back: //返回主页面
//                Intent intent1 = new Intent(this, Level3Activity.class);
//                startActivity(intent1);
                finish();
                break;
            case R.id.bt_reportactivity_report:    //匿名举报按钮
                //获取用户输入
                String discovery_address = mEtReportactivityDiscovery_address.getText().toString().trim();
                String name = mEtReportactivityName.getText().toString().trim();
                String sexual = mEtReportactivitySexual.getText().toString().trim();
                String phone_number = mEtReportactivityPhone_number.getText().toString().trim();
                String address = mEtReportactivityAddress.getText().toString().trim();
                User user = mDBOpenHelper.getAllData().get(0);
                final HashMap<String, String> report = new HashMap<>();
                report.put("name", user.getName());
                report.put("id_number", user.getId_number());
                report.put("gender", user.getSexual());
                report.put("address", user.getAddress());
                report.put("phone_number", user.getPhone_number());
                report.put("village_code",user.getVillageCode());
                report.put("acused_name", name);
                report.put("acused_gender", sexual);
                report.put("acused_phone_number", phone_number);
                report.put("acused_address", address);
                report.put("witness_location", discovery_address);
                //举报
                if (!TextUtils.isEmpty(discovery_address) && !TextUtils.isEmpty(name) && isSexualValid(sexual) && !TextUtils.isEmpty(address)) {
                    mDBOpenHelper.add_report(name, sexual, discovery_address, phone_number, address);
                    final NetworkHelper mNetworkHelper = new NetworkHelper();

                    final boolean[] success = new boolean[1];
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                success[0] = mNetworkHelper.submitDataByDoPost(report, "/post_report/");
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
                        ReportActivity.this.finish();
                        Toast.makeText(this, "举报成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "传输失败！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "信息不完善，举报失败", Toast.LENGTH_SHORT).show();

                }

        }
    }
}

