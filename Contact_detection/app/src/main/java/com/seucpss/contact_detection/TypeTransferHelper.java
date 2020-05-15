package com.seucpss.contact_detection;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class TypeTransferHelper {
    /**
     * 方法名称:transMapToString
     * 传入参数:map
     * 返回值:String 形如 username'chenziwen^password'1234
     */
    public static String transMapToString(Map map) {
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("'").append(null == entry.getValue() ? "" :
                    entry.getValue().toString()).append(iterator.hasNext() ? "^" : "");
        }
        return sb.toString();
    }

    /**
     * 方法名称:transStringToMap
     * 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map transStringToMap(String mapString) {
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens();
             map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }

    public static Map transJSONObjectToMap(JSONObject obj) {
        //map对象
        Map<String, Object> data = new HashMap<>();
        //循环转换
        Iterator it = obj.keys();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }

    public static ArrayList<String> transJSONStringToList(String str) {
        String[] arrayStr = (str.substring(1, str.length() - 1)).split(",");//string数组
        // 使用Arrays.asList 转换
        return new ArrayList<String>(Arrays.asList(arrayStr));
    }

    public static String transStatusCode(int status_code) {
        switch (status_code) {
            case 0:
            default:
                return "零风险";
            case 1:
                return "低风险-密切接触者";
            case 2:
                return "中风险-密切接触者";
            case 3:
                return "高风险-密切接触者";
            case 4:
                return "疑似病例";
            case 5:
                return "确诊病例";
        }
    }
    public static long transTimeStrToTimestamp(String date, String time, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");//小写的mm表示的是分钟
        String time_ = date + "-" + time;
        Date dTime = sdf.parse(time_);
        return dTime.getTime();
    }
}
