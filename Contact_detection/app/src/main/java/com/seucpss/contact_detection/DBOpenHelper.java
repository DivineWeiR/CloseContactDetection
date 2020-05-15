package com.seucpss.contact_detection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seucpss.contact_detection.Bean.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 有关android本地的SQLite数据库操作
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    /**
     * 声明一个AndroidSDK自带的数据库变量db
     */
    private SQLiteDatabase db;

    /**
     * 写一个这个类的构造函数，参数为上下文context，所谓上下文就是这个类所在包的路径
     * 指明上下文，数据库名，工厂默认空值，版本号默认从1开始
     * super(context,"db_test",null,1);
     * 把数据库设置成可写入状态，除非内存已满，那时候会自动设置为只读模式
     * 不过，以现如今的内存容量，估计一辈子也见不到几次内存占满的状态
     * db = getReadableDatabase();
     */
    public DBOpenHelper(Context context) {
        super(context, "db_test", null, 1);
        db = getReadableDatabase();
    }

    /**
     * 重写两个必须要重写的方法，因为class DBOpenHelper extends SQLiteOpenHelper
     * 而这两个方法是 abstract 类 SQLiteOpenHelper 中声明的 abstract 方法
     * 所以必须在子类 DBOpenHelper 中重写 abstract 方法
     * 想想也是，为啥规定这么死必须重写？
     * 因为，一个数据库表，首先是要被创建的，然后免不了是要进行增删改操作的
     * 所以就有onCreate()、onUpgrade()两个方法
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DBOpenHelper", "DBOpenHelperDBOpenHelper");
        //注意 我的user表中，主键是一个自增的id，而不是身份证号，因为后面有个update操作需要
        db.execSQL("CREATE TABLE IF NOT EXISTS user(" +
                "id integer PRIMARY KEY autoincrement," +
                "id_number VARCHAR(18) ," +
                "name VARCHAR(255)," +
                "sexual VARCHAR(15)," +
                "phone_number VARCHAR(11)," +
                "track TEXT," +
                "address VARCHAR(1023)," +
                "village_name VARCHAR(1023)," +
                "village_code VARCHAR(12))");
        db.execSQL("CREATE TABLE IF NOT EXISTS report(" +
                "id integer PRIMARY KEY autoincrement," +
                "name VARCHAR(255)," +
                "sexual VARCHAR(15)," +
                "discover_address VARCHAR(1023)," +
                "phone_number VARCHAR(11)," +
                "address VARCHAR(1023))");
        db.execSQL("CREATE TABLE IF NOT EXISTS level_2_position(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Pname VARCHAR(255)," +
                "starttime INTEGER," +
                "endtime INTEGER," +
                "lat DOUBLE," +
                "lon DOUBLE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS level_1_position(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Pname VARCHAR(255)," +
                "time INTEGER," +
                "lat DOUBLE," +
                "lon DOUBLE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS tracks(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date DATETIME," +
                "daily_tracks TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("DBOpenHelper", "onUpgradeonUpgrade");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS report");
        db.execSQL("DROP TABLE IF EXISTS level_2_position");
        db.execSQL("DROP TABLE IF EXISTS level_1_position");
        db.execSQL("DROP TABLE IF EXISTS tracks");
        onCreate(db);
    }

    /**
     * 接下来写自定义的增删改查方法
     * 这些方法，写在这里归写在这里，以后不一定都用
     * add()
     * delete()
     * update()
     * getAllData()
     */
    //注册中使用的add方法，向数据库user表中插入注册信息
    public void add(String id_number, String name, String sexual, String phone_number, String address, String village_name, String village_code) {
        db.execSQL("INSERT INTO user (id_number,name,sexual,phone_number,address,village_name,village_code) VALUES(?,?,?,?,?,?,?)", new Object[]{id_number, name, sexual, phone_number, address, village_name, village_code});
    }

    //举报中使用的add_report方法，向数据库report表中写入举报信息
    public void add_report(String name, String sexual, String discover_address, String phone_number, String address) {
        db.execSQL("INSERT INTO report (name,sexual,discover_address,phone_number,address) VALUES(?,?,?,?,?)", new Object[]{name, sexual, discover_address, phone_number, address});
    }

    //三级响应中，向user表中更新track，选择id=1，因为user表只会有这一行，所以选择了一个id当主键，因为想判定id_number=数据库中已有的，不太好操作
    public void add_level3(String track) {
        db.execSQL("UPDATE user SET track = ? WHERE id = 1", new Object[]{track});
    }

    //插入二级响应用户提交数据
    public void add_level2(String Pname, long starttime, long endtime, double lat, double lon) {
        db.execSQL("INSERT INTO level_2_position (Pname,starttime,endtime,lat,lon) VALUES(?,?,?,?,?)", new Object[]{Pname, starttime, endtime, lat, lon});
    }

    //插入一级响应用户定位数据
    public void add_level1(String Pname, long time, double lat, double lon) {
        db.execSQL("INSERT INTO level_1_position (Pname,time,lat,lon) VALUES(?,?,?,?)", new Object[]{Pname, time, lat, lon});
    }

    //插入新增病例轨迹
    public void addNewPatientsTrack(String date, String newTracks) {
        Cursor cursor = db.rawQuery("SELECT daily_tracks FROM tracks WHERE date = ?", new String[]{date});
        if (cursor.getCount() == 0) {
            db.execSQL("INSERT INTO tracks (date,daily_tracks) VALUES(?,?)", new Object[]{date, newTracks});
            return;
        } else {
            while (cursor.moveToNext()) {
                try {
                    JSONArray oriDailyTracks = new JSONArray(cursor.getString(cursor.getColumnIndex("daily_tracks")));
                    JSONArray newDailyTracks = new JSONArray(newTracks);
                    JSONArray finalDailyTracks = JSONHelper.joinJSONArray(oriDailyTracks, newDailyTracks);
                    db.execSQL("UPDATE tracks SET daily_tracks=? WHERE date=?", new Object[]{finalDailyTracks.toString(), date});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //插入轨迹
    public void addAllPatientsTrack(String date, String tracks) {
        db.execSQL("INSERT INTO tracks (date,daily_tracks) VALUES(?,?)", new Object[]{date, tracks});
    }

    /**
     * 有关删除和更新
     public void delete(String name,String password){
     db.execSQL("DELETE FROM user WHERE IDNumber = AND password ="+name+password);
     }
     public void updata(String password){
     db.execSQL("UPDATE user SET password = ?",new Object[]{password});
     }
     */

    /**
     * 前三个没啥说的，都是一套的看懂一个其他的都能懂了
     * 下面重点说一下查询表user全部内容的方法
     * 我们查询出来的内容，需要有个容器存放，以供使用，
     * 所以定义了一个ArrayList类的list
     * 有了容器，接下来就该从表中查询数据了，
     * 这里使用游标Cursor，这就是数据库的功底了，
     * 在Android中我就不细说了，因为我数据库功底也不是很厚，
     * 但我知道，如果需要用Cursor的话，第一个参数："表名"，中间5个：null，
     * 最后是查询出来内容的排序方式："name DESC"
     * 游标定义好了，接下来写一个while循环，让游标从表头游到表尾
     * 在游的过程中把游出来的数据存放到list容器中
     *
     * @return
     */
    public ArrayList<User> getAllData() {

        ArrayList<User> list = new ArrayList<User>();
        Cursor cursor = db.query("user", null, null, null, null, null, "name DESC");
        while (cursor.moveToNext()) {
            String id_number = cursor.getString(cursor.getColumnIndex("id_number"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String sexual = cursor.getString(cursor.getColumnIndex("sexual"));
            String phone_number = cursor.getString(cursor.getColumnIndex("phone_number"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String village_name = cursor.getString(cursor.getColumnIndex("village_name"));
            String village_code= cursor.getString(cursor.getColumnIndex("village_code"));
            list.add(new User(id_number, name, sexual, phone_number, address,village_name,village_code));
        }
        return list;
    }

    //获取二级响应存储数据
    public ArrayList<Position> getLevel2Data() {

        ArrayList<Position> list = new ArrayList<Position>();
        Cursor cursor = db.query("level_2_position", null, null, null, null, null, "starttime DESC");
        while (cursor.moveToNext()) {
            String Pname = cursor.getString(cursor.getColumnIndex("Pname"));
            long starttime = cursor.getLong(cursor.getColumnIndex("starttime"));
            long endtime = cursor.getLong(cursor.getColumnIndex("endtime"));

            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Position position = new Position(Pname, lat, lon, starttime, endtime);
            position.setId(id);
            list.add(position);
        }
        return list;
    }

    //获取刚插入的二级响应数据id，用于删除
    public int getMaxId() {

        Cursor cursor = db.rawQuery("select last_insert_rowid() from level_2_position", null);
        int strid = -1;
        if (cursor.moveToFirst()) {
            strid = cursor.getInt(0);
        }
        return strid;
    }

    //删除二级响应信息
    public void delete(int id) {
        db.execSQL("DELETE FROM level_2_position WHERE id = " + id);
    }

    public ArrayList<Position> getAll3Data(Long date) {

        ArrayList<Position> list = new ArrayList<Position>();
        Long date2 = date + 24 * 3600 * 1000;
        Cursor cursor = db.query("level_1_position", null, "time>? and time<?", new String[]{String.valueOf(date), String.valueOf(date2)}, null, null, "time DESC");
        while (cursor.moveToNext()) {
            String Pname = cursor.getString(cursor.getColumnIndex("Pname"));
            long starttime = cursor.getLong(cursor.getColumnIndex("time"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Position position = new Position(Pname, lat, lon, starttime, 0);
            position.setId(id);
            list.add(position);
        }
        return list;
    }


    //搜索与给定时间段有交集的二级响应数据集合
    public ArrayList<Position> get2times(Long starttime, Long endtime) {

        ArrayList<Position> list = new ArrayList<Position>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select DISTINCT * from level_2_position where (starttime>? and starttime<?) or (endtime>? and endtime<?) or (starttime<? and endtime>? )", new String[]{String.valueOf(starttime), String.valueOf(endtime), String.valueOf(starttime), String.valueOf(endtime), String.valueOf(starttime), String.valueOf(endtime)});
//        Cursor cursor = db.query("level_2_position",null,"(starttime>? and starttime<?) or (endtime>? and endtime<?) or (starttime<? and endtime>? )",new String[]{String.valueOf(starttime),String.valueOf(endtime),String.valueOf(starttime),String.valueOf(endtime),String.valueOf(starttime),String.valueOf(endtime)},null,null,null);
        while (cursor.moveToNext()) {
            String Pname = cursor.getString(cursor.getColumnIndex("Pname"));
            long st = cursor.getLong(cursor.getColumnIndex("starttime"));
            long et = cursor.getLong(cursor.getColumnIndex("endtime"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Position position = new Position(Pname, lat, lon, st, et);
            position.setId(id);
            list.add(position);
        }
        return list;
    }


    //搜索在给定时间段内的用户一级响应定位点集合
    public ArrayList<Position> get1times(Long starttime, Long endtime) {

        ArrayList<Position> list = new ArrayList<Position>();
//        Cursor cursor=db.rawQuery("select DISTINCT * from level_2_position where (starttime>? and starttime<?) or (endtime>? and endtime<?) or (starttime<? and endtime>? )",new String[]{String.valueOf(starttime),String.valueOf(endtime),String.valueOf(starttime),String.valueOf(endtime),String.valueOf(starttime),String.valueOf(endtime)});
        @SuppressLint("Recycle") Cursor cursor = db.query("level_1_position", null, "(time>? and time<?)", new String[]{String.valueOf(starttime), String.valueOf(endtime)}, null, null, null);
        while (cursor.moveToNext()) {
            String Pname = cursor.getString(cursor.getColumnIndex("Pname"));
            long stime = cursor.getLong(cursor.getColumnIndex("time"));
//            long et = cursor.getLong(cursor.getColumnIndex("endtime"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Position position = new Position(Pname, lat, lon, stime, 0);
            position.setId(id);
            list.add(position);
        }
        return list;
    }

    public JSONObject getPatientsTracks() {
        JSONObject tracks = new JSONObject();
        @SuppressLint("Recycle") Cursor cursor = db.query("tracks", null, null, null, null, null, "date");
        //获取当前时间
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String strDailyTrack = cursor.getString(cursor.getColumnIndex("daily_tracks"));
            JSONArray dailyTrack;
            try {
                dailyTrack = new JSONArray(strDailyTrack);
                tracks.put(date, dailyTrack);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tracks;
    }

    public JSONObject getPatientsTrackNames(){
        JSONObject tracks = new JSONObject();
        @SuppressLint("Recycle") Cursor cursor = db.query("tracks", null, null, null, null, null, "date");
        //获取当前时间
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String strDailyTrack = cursor.getString(cursor.getColumnIndex("daily_tracks"));
            JSONArray dailyTrack;
            try {
                dailyTrack = new JSONArray(strDailyTrack);
                JSONArray dailyTracks = new JSONArray();
                for(int i=0;i<dailyTrack.length();i++){
                    dailyTracks.put(dailyTrack.getJSONObject(i).get("name"));
                }
                tracks.put(date, dailyTracks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tracks;
    }

    public JSONArray get14DayRoute() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date) + "-00:00";
        System.out.println(time);
        Date date2 = null;
        try {
            date2 = simpleDateFormat2.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date2s = date2.getTime();
        JSONArray tracks = new JSONArray();
//        List<Map<String, Object>> datelist = new ArrayList<>();
        try {
            for (int n = 0; n < 14; n++) {
//            Map<String, Object> datamap = new HashMap<>();
//            List<Map<String, Object>> poislist = new ArrayList<>();
                long timestart = date2s - n * 24 * 3600 * 1000;
                List<Position> positionByDate = getAll3Data(timestart);

                for (int i = 0; i < positionByDate.size() - 1; i++) {
                    // 从list中索引为 list.size()-1 开始往前遍历
                    for (int j = positionByDate.size() - 1; j > i; j--) {
                        // 进行比较
                        if (simpleDateFormat3.format(new Date(positionByDate.get(j).getStarttime())).equals(simpleDateFormat3.format(new Date(positionByDate.get(i).getStarttime())))) {
                            // 去重
                            positionByDate.remove(j);
                        }
                    }
                }
                JSONArray jsonPOIs = new JSONArray();
                for (int i = 0; i < positionByDate.size(); i++) {
                    JSONObject jsonPOI = new JSONObject();

                    String starttime = simpleDateFormat3.format(new Date(positionByDate.get(i).getStarttime()));
                    String endtime = simpleDateFormat3.format(new Date(positionByDate.get(i).getStarttime() + 300000));

                    JSONObject jsonTime = new JSONObject();
                    jsonTime.put("endtime", endtime);
                    jsonTime.put("starttime", starttime);
                    jsonPOI.put("time", jsonTime);

                    JSONObject jsonCoordinate = new JSONObject();
                    jsonCoordinate.put("lat", positionByDate.get(i).getLat());
                    jsonCoordinate.put("long", positionByDate.get(i).getLon());
                    jsonPOI.put("coordinate", jsonCoordinate);
                    jsonPOI.put("name", positionByDate.get(i).getPname());

                    jsonPOIs.put(jsonPOI);
                }
                String dateString = simpleDateFormat.format(new Date(timestart));
                JSONObject jsonDailyTrack = new JSONObject();
                jsonDailyTrack.put("date", dateString);
                jsonDailyTrack.put("pois", jsonPOIs);


                tracks.put(jsonDailyTrack);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(tracks.toString());
        return tracks;

    }
}
