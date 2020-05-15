package com.seucpss.contact_detection;

import java.util.HashMap;

/**
 *
 */
public class User {
    private String id_number;         //用户名即身份证号
    private String name;           //姓名
    private String sexual;            //性别
    private String phone_number;    //电话
    private String address;       //家庭地址
    private String villageName;
    private String villageCode;

    public User(String id_number, String name, String sexual, String phone_number,
                String address, String village_name, String village_code) {
        this.id_number = id_number;
        this.name = name;
        this.sexual = sexual;
        this.phone_number = phone_number;
        this.address = address;
        this.villageName = village_name;
        this.villageCode = village_code;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSexual() {
        return sexual;
    }

    public void setSexual(String sexual) {
        this.sexual = sexual;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_number='" + id_number + '\'' +
                ", name='" + name + '\'' +
                ", sexual='" + sexual + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", address='" + address + '\'' +
                ", village_name='" + villageName + '\'' +
                ", village_code='" + villageCode + '\'' +
                '}';
    }

    public HashMap<String,String> getUserInfo(){
        final HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("name", getName());
        userInfo.put("id_number", getId_number());
        userInfo.put("gender", getSexual());
        userInfo.put("address", getAddress());
        userInfo.put("phone_number", getPhone_number());
        userInfo.put("village_name",getVillageName());
        userInfo.put("village_code",getVillageCode());
        return userInfo;
    }
}

