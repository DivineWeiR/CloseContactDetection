package com.seucpss.contact_detection;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 注册界面
 */

/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private String realCode;
    private DBOpenHelper mDBOpenHelper;
    private Button mBtRegisteractivityRegister;
    //private RelativeLayout mRlRegisteractivityTop;
    //private ImageView mIvRegisteractivityBack;
    private LinearLayout mLlRegisteractivityBody;
    private EditText mEtRegisteractivityId_number;
    private EditText mEtRegisteractivityName;
    private RadioGroup mRgRegisteractivitySexual;
    private EditText mEtRegisteractivityPhone_number;
    private EditText mEtRegisteractivityAddress;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mIvRegisteractivityShowcode;
    private Button btnCheck;
    private TextView tvResult;
    private RelativeLayout mRlRegisteractivityBottom;
    private int mType;
    private String selectedStreet;
    private String selectedCity;
    private String selectedArea;
    private String selectedVillage;
    private RegionAdapter regionAdapter;
    private List<RegionBean> cityDatas;
    //声明用来保证持续登陆的变量
    private String strId_number = "";
    private String sexual = "";
    private String village_name = "";
    private String village_code = "";
    private final JSONObject[] jsonPatientsTracks = new JSONObject[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //地址选择器弹窗
        regionAdapter = new RegionAdapter(getBaseContext());
        cityDatas = GsonU.getJsonData(getBaseContext());
        tvResult = findViewById(R.id.tvResult);
        tvResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!tvResult.getText().toString().isEmpty()) {
                    tvResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkType();

                PopupU.showRegionView(RegisterActivity.this, mType, selectedCity, selectedArea, selectedStreet, selectedVillage, new PopupU.OnRegionListener() {
                    @Override
                    public void onRegionListener(String city, String area, String street, String village) {
                        // 选择完回调结果赋值给当前

                        selectedCity = city;
                        selectedArea = area;
                        selectedStreet = street;
                        selectedVillage = village;

                        int positionCity = regionAdapter.getCityPosition(selectedCity);
                        int positionArea = regionAdapter.getAreaPosition(selectedCity, selectedArea);
                        int positionStreet = regionAdapter.getStreetPosition(selectedCity, selectedArea, selectedStreet);
                        int positionVillage = regionAdapter.getVillagePosition(selectedCity, selectedArea, selectedStreet, selectedVillage);
                        //输出的是所选社区对应的编码
                        System.out.println(cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.get(positionVillage).code);
                        tvResult.setText(selectedCity + " " + selectedArea + " " + selectedStreet + " " + selectedVillage);
                        village_name = selectedCity + selectedArea + selectedStreet + selectedVillage;
                        village_code = (cityDatas.get(positionCity).areas.get(positionArea).streets.get(positionStreet).villages.get(positionVillage).code).toString();
                    }
                });
            }
        });
        initView();
        // 为点击事件设置监听器
        setOnClickListener();
         /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(mEtRegisteractivityId_number, "id_number", "身份证号格式不正确");
        setOnFocusChangeErrMsg(mEtRegisteractivityPhone_number, "phone_number", "手机号格式不正确");
//        setOnFocusChangeErrMsg(mEtRegisteractivitySexual, "sexual", "性别只能填男或女");
        /**增加用户体验，不使用登陆界面，未添加
         *接收用户在登录界面输入的数据，如果输入过了就不用再输入了
         *注意接收上一个页面Intent的信息，需要getIntent，而非重新new一个Intent
         *Intent it_from_login = getIntent();
         *idnumber = it_from_login.getStringExtra("idnumber");
         *把对应的account设置到phone_number输入框
         *mEtRegisteractivityId_number.setText(idnumber);
         */
        //有关自动登陆
        mDBOpenHelper = new DBOpenHelper(this);
        SharedPreferences share = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strId_number = share.getString("Id_number", "");
        //一旦注册成功后，会在该路径下生成一个文件，通过判定该文件是否存在，判定是否可以自动登陆
        File file = new File("/data/data/com.seucpss.contact_detection/shared_prefs/Login.xml");
        if (!file.exists()) {
            initView();
        } else {
            final String[] diseaseLevel = new String[1];
            Thread threadGetInfo = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHelper mNetworkHelper = new NetworkHelper();
                    try {
                        diseaseLevel[0] = (String) mNetworkHelper.LoadData("/request_disease_level").get("data");
                        jsonPatientsTracks[0] = mNetworkHelper.LoadData("/request_patients_poi_new").getJSONObject("data");
                        for (Iterator dateIter = jsonPatientsTracks[0].keys(); dateIter.hasNext(); ) {
                            String date = (String) dateIter.next();
                            String dailytracks = jsonPatientsTracks[0].getString(date);
                            mDBOpenHelper.addAllPatientsTrack(date, dailytracks);
                        }
                        System.out.println(mDBOpenHelper.getPatientsTracks());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            threadGetInfo.start();
            try {
                threadGetInfo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (diseaseLevel[0]) {
                case "1": {
                    System.out.println("DiseaseLevel1:" + diseaseLevel[0]);
                    Intent intent = new Intent(this, Level1Activity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case "2": {
                    System.out.println("DiseaseLevel2:" + diseaseLevel[0]);
                    Intent intent = new Intent(this, Level2Activity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case "3":
                default: {
                    System.out.println("DiseaseLevel3:" + diseaseLevel[0]);
                    Intent intent = new Intent(this, Level3Activity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
            }


        }


        //将验证码用图片的形式显示出来
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }


    private void initView() {
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register);
        //mRlRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mLlRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);
        mEtRegisteractivityId_number = findViewById(R.id.et_registeractivity_id_number);
        mEtRegisteractivityId_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String id_number = (String) mEtRegisteractivityId_number.getText().toString();
                    RadioButton femaleRadio = (RadioButton) findViewById(R.id.rb_registeractivity_sexual_female);
                    RadioButton maleRadio = (RadioButton) findViewById(R.id.rb_registeractivity_sexual_male);
                    String checkCode = id_number.charAt(id_number.length() - 2) + "";
                    if (Integer.parseInt(checkCode) % 2 == 0) {
                        maleRadio.setChecked(false);
                        femaleRadio.setChecked(true);
                        sexual = "女";
                    } else {
                        femaleRadio.setChecked(false);
                        maleRadio.setChecked(true);
                        sexual = "男";
                    }
                }
            }
        });
        mEtRegisteractivityName = findViewById(R.id.et_registeractivity_name);
        mRgRegisteractivitySexual = findViewById(R.id.rg_registeractivity_sexual);
        mEtRegisteractivityPhone_number = findViewById(R.id.et_registeractivity_phone_number);
        mEtRegisteractivityAddress = findViewById(R.id.et_registeractivity_address);
        mEtRegisteractivityPhonecodes = findViewById(R.id.et_registeractivity_phoneCodes);
        mIvRegisteractivityShowcode = findViewById(R.id.iv_registeractivity_showCode);
        mRlRegisteractivityBottom = findViewById(R.id.rl_registeractivity_bottom);

        /**
         * 注册页面能点击的就三个地方
         * top处返回箭头、刷新验证码图片、注册按钮
         */
        mIvRegisteractivityShowcode.setOnClickListener(this);
        mBtRegisteractivityRegister.setOnClickListener(this);
    }

    //监听，判断每个输入框输入内容是否合法，否则提示
    private void setOnFocusChangeErrMsg(final EditText editText, final String inputType, final String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus) {
                            if (inputType == "id_number") {
                                if (isId_numberValid(inputStr)) {
                                    editText.setError(null);
                                } else {
                                    editText.setError(errMsg);
                                }
                            }
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

    //身份证号的正则表达式
    private boolean isId_numberValid(String idnumber) {
        if (idnumber == null) {
            return false;
        }
        String pattern = "^[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(idnumber);
        return m.matches();
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


    private void setOnClickListener() {
        mBtRegisteractivityRegister.setOnClickListener(this);
    }

    public void onRadioButtonClicked(View view) {
        RadioButton button = (RadioButton) view;
        boolean isChecked = button.isChecked();
        switch (view.getId()) {
            case R.id.rb_registeractivity_sexual_male:
                if (isChecked) {
                    sexual = "男";

                }
                break;
            case R.id.rb_registeractivity_sexual_female:
                if (isChecked) {
                    sexual = "女";
                }
                break;
            default:
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_registeractivity_showCode:    //改变随机验证码的生成
                mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.bt_registeractivity_register:    //注册按钮
                //获取用户输入的用户名、密码、验证码
                String id_number = mEtRegisteractivityId_number.getText().toString().trim();
                String name = mEtRegisteractivityName.getText().toString().trim();
                String phone_number = mEtRegisteractivityPhone_number.getText().toString().trim();
                String address = mEtRegisteractivityAddress.getText().toString().trim();
                String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();
                //注册验证
                if (isId_numberValid(id_number) && !TextUtils.isEmpty(phoneCode) && !TextUtils.isEmpty(name) && isSexualValid(sexual) && isPhone_numberValid(phone_number) && !TextUtils.isEmpty(address) && !village_code.isEmpty()) {
                    if (phoneCode.equals(realCode)) {

                        //将相关信息写到SharedPreferences中去，为实现自动登录
                        SharedPreferences share = getSharedPreferences("Login",
                                Context.MODE_PRIVATE);
                        // 获取编辑器来存储数据到sharedpreferences中
                        SharedPreferences.Editor editor = share.edit();
                        editor.putString("id_number", mEtRegisteractivityId_number.getText().toString());
                        // 将数据提交到sharedpreferences中
                        editor.commit();
                        final HashMap<String, String> user_info = new HashMap<>();
                        user_info.put("id_number", id_number);
                        user_info.put("name", name);
                        user_info.put("village_code", village_code);
                        final boolean[] success = new boolean[1];
                        Thread threadSubmitInfo = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetworkHelper mNetworkHelper = new NetworkHelper();
                                try {
                                    success[0] = mNetworkHelper.submitDataByDoPost(user_info, "/post_register_data/");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    success[0] = false;
                                }
                            }
                        });
                        threadSubmitInfo.start();
                        final String[] diseaseLevel = new String[1];
                        try {
                            threadSubmitInfo.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (success[0]) {
                            //将用户名信息加入到数据库中
                            mDBOpenHelper.add(id_number, name, sexual, phone_number, address, village_name, village_code);
                            Toast.makeText(this, "验证通过，注册成功！", Toast.LENGTH_SHORT).show();
                            Thread threadGetInfo = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    NetworkHelper mNetworkHelper = new NetworkHelper();
                                    try {
                                        diseaseLevel[0] = (String) mNetworkHelper.LoadData("/request_disease_level").get("data");
                                        jsonPatientsTracks[0] = mNetworkHelper.LoadData("/request_patients_poi_new").getJSONObject("data");
                                        for (Iterator dateIter = jsonPatientsTracks[0].keys(); dateIter.hasNext(); ) {
                                            String date = (String) dateIter.next();
                                            String dailytracks = jsonPatientsTracks[0].getString(date);
                                            mDBOpenHelper.addAllPatientsTrack(date, dailytracks);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            threadGetInfo.start();
                            try {
                                threadGetInfo.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            switch (diseaseLevel[0]) {
                                case "1": {
                                    System.out.println("DiseaseLevel1:" + diseaseLevel[0]);
                                    Intent intent = new Intent(this, Level1Activity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                case "2": {
                                    System.out.println("DiseaseLevel2:" + diseaseLevel[0]);
                                    Intent intent = new Intent(this, Level2Activity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                case "3":
                                default: {
                                    System.out.println("DiseaseLevel3:" + diseaseLevel[0]);
                                    Intent intent = new Intent(this, Level3Activity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(this, "传输失败！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "验证码错误,注册失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "注册信息错误，注册失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkType() {
        if (TextUtils.isEmpty(selectedCity) && TextUtils.isEmpty(selectedArea) && TextUtils.isEmpty(selectedStreet)) {
            mType = Config.TYPE_ADD;
        } else {
            mType = Config.TYPE_EDIT;
        }
    }
}

