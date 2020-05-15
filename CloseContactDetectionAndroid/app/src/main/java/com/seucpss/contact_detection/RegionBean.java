package com.seucpss.contact_detection;

import java.util.List;

/**
 * Created by chen.yingjie on 2019/3/23
 */
public class RegionBean {
    public String cityName;
    //public String provinceId;
    public List<Area> areas;


    public static class Area {
        public String areaName;
        //public String cityId;
        public List<Street> streets;

        public static class Street {
            public String streetName;
            //public String id;
            public List<Village> villages;

            public static class Village{
                public String name;
                public String code;
            }

        }
    }
}

