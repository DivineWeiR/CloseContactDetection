package com.seucpss.contact_detection;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class NetworkHelper {

    /**
     * 使用get请求以普通方式提交数据
     *
     * @param map  传递进来的数据，以map的形式进行了封装
     * @param path 要求服务器servlet的地址
     * @return 返回的boolean类型的参数
     * @throws Exception
     */

    //这里定义了网络接口，可以根据修改之后用
    public String baseUrl = "http://47.100.26.203:8008";
//    public String baseUrl = "http://192.168.1.105:8008";

    public JSONObject LoadData(String path) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(baseUrl + path);
        System.out.println(baseUrl + path);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inStream = httpConn.getInputStream();
            while ((len = inStream.read(data)) != -1) {
                outStream.write(data, 0, len);
            }
            inStream.close();
            String str = new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据
            return new JSONObject(str);
        }
        return null;
    }

    public Boolean submitDataByDoGet(Map<String, String> map, String path) throws Exception {
        // 拼凑出请求地址
        StringBuilder sb = new StringBuilder(baseUrl + path);
        sb.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String str = sb.toString();
        System.out.println(str);
        URL url = new URL(str);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        // GET方式的请求不用设置什么DoOutPut()之类的吗？
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return true;
        }
        return false;
    }

    /**
     * 普通方式的DoPost请求提交数据
     *
     * @param map  传递进来的数据，以map的形式进行了封装
     * @param path 要求服务器servlet的地址
     * @return 返回的boolean类型的参数
     * @throws Exception
     */
    public Boolean submitDataByDoPost(Map<String, String> map, String path) throws Exception {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数
        URL url = new URL(baseUrl + path);
        // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
        StringBuilder sb = new StringBuilder();
        // sb.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String str = sb.toString();

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setReadTimeout(5000);
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Content-Length", String.valueOf(str.getBytes().length));

        OutputStream os = httpConn.getOutputStream();
        os.write(str.getBytes());
        System.out.println(httpConn.getResponseCode());
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return true;
        }
        return false;
    }


}
