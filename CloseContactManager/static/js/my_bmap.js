var lng_s;
var lat_s;
var lng_w;
var lat_w;
var icon = new BMap.Icon('/static/img/marker.png', new BMap.Size(24, 24), {
    anchor: new BMap.Size(12, 24)
});

function draw_map_with_overlay(map_id, json_data, color) {

    var map = new BMap.Map(map_id); // 创建Map实例
    map.addControl(new BMap.MapTypeControl({
        mapTypes: [
            BMAP_NORMAL_MAP,
            BMAP_HYBRID_MAP
        ]
    }));
    map.setCurrentCity("南京"); // 设置地图显示的城市 此项是必须设置的
    map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放

    var list_map = [];

    for (var i = 0; i < json_data.length; i++) {
        var poi = json_data[i];
        var long = poi.coordinate.long;
        var lat = poi.coordinate.lat;
        var point = new BMap.Point(long, lat);
        list_map.push(point);

        var marker = new BMap.Marker(point, {
            icon: icon
        });
        marker.customData = {
            name: poi.name,
            long: long,
            lat: lat,
            starttime: poi.time.starttime,
            endtime: poi.time.endtime
        };
        marker.addEventListener("click", function (e) {
            var sContent =
                "<div class=\"mx-auto d-block\">" +
                "<h5 class=\"text-sm-left mt-2 mb-1\"><b>" + e.target.customData.name + "</b></h5>" +
                "<div class=\"location text-sm-left\"><i " +
                "class=\"fa fa-map-marker\"></i>&nbsp;&nbsp;" + e.target.customData.long + "°E," + e.target.customData.lat + "°N" +
                "</div>" +
                "<div class=\"location text-sm-left\"><i " +
                "class=\"fa fa-clock-o\"></i>&nbsp;&nbsp;" + e.target.customData.starttime +
                "-" + e.target.customData.endtime +
                "</div>" +
                "</div>";
            var opts = {
                enableMessage: false
            };
            var infoWindow = new BMap.InfoWindow(sContent, opts);
            this.openInfoWindow(infoWindow);
        });
        map.addOverlay(marker);
    }

// for (var i = 0; i < tmp.length; i++) {
//     var pois = tmp[i].pois;
//     for (var j = 0; j < pois.length; j++) {
//         var lon = pois[j].coordinate.long;
//         var lat = pois[j].coordinate.lat;
//         var point = new BMap.Point(lon, lat);
//         list_map.push(point);
//         map.addOverlay(new BMap.Marker(point));
//         map.centerAndZoom(point, 30);
//     }
// }

    var polyline = new BMap.Polyline(list_map, {strokeColor: color, strokeWeight: 4, strokeOpacity: 0.5});   //创建折线
    map.addOverlay(polyline);

    var loadCount = 0;
    map.addEventListener("tilesloaded", function () {//mapName是你的地图名称
        if (loadCount == 1) {
            var view = map.getViewport(eval(list_map));
            var mapZoom = view.zoom;
            var centerPoint = view.center;
            map.setCenter(centerPoint);
        }
        loadCount++;
    });

    setTimeout(function () {
        var view = map.getViewport(eval(list_map));
        var mapZoom = view.zoom;
        var centerPoint = view.center;
        map.centerAndZoom(centerPoint, mapZoom);
    });
// //添加覆盖物
//     function add_overlay() {
//         map.addOverlay(polyline);          //增加折线
//
//     }
//
// //清除覆盖物
//     function remove_overlay() {
//         map.clearOverlays();
//     }
}

function G(id) {
    return document.getElementById(id);
}

var search_map;


function draw_map_for_search(map_id, input_id, output_id, long_id, lat_id, refresh_id) {


    search_map = new BMap.Map(map_id); // 创建Map实例
    window.searchMap = search_map;
    //添加地图类型控件
    search_map.addControl(new BMap.MapTypeControl({
        mapTypes: [
            BMAP_NORMAL_MAP,
            BMAP_HYBRID_MAP
        ]
    }));
    search_map.setCurrentCity("南京"); // 设置地图显示的城市 此项是必须设置的
    search_map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放


    var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
        {
            "input": input_id,
            "location": search_map
        });
    ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        // str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        // str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
        // G(output_id).innerHTML = str;
        G(output_id).value = value;
    });

    var myValue;

    ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
        // G(output_id).innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
        G(output_id).value = myValue;
        setPlace(search_map, myValue, long_id, lat_id);
    });


    search_map.addEventListener("click", function showInfo(e) {
        var gc = new BMap.Geocoder();
        gc.getLocation(e.point, function (rs) {
            var addComp = rs.addressComponents;
            G(lat_id).value = e.point.lat;
            G(long_id).value = e.point.lng;
            G(output_id).value = addComp.province + " " + addComp.city + " " + addComp.district + " " + addComp.street + " " + addComp.streetNumber;
        });
    });
    var loadCount = 0;
    search_map.addEventListener("tilesloaded", function () {//mapName是你的地图名称
        if (loadCount == 1) {
            search_map.setCenter(new BMap.Point(118.802422, 32.064652));
        }
        loadCount++;
    });
    search_map.centerAndZoom(new BMap.Point(118.802422, 32.064652), 11); // 初始化地图,设置中心点坐标和地图级别

    G(refresh_id).addEventListener('click', function () {
        var centerPoint = new BMap.Point(G(long_id).value, G(lat_id).value);
        map.setCenter(centerPoint);
        map.removeOverlays();
        map.addOverlay(new BMap.Marker(centerPoint));

    });
}

//展示搜索结构
function setPlace(search_map, myValue, long_id, lat_id) {
    search_map.clearOverlays();    //清除地图上所有覆盖物
    function myFun() {
        var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
        search_map.centerAndZoom(pp, 18);
        search_map.addOverlay(new BMap.Marker(pp, {
            icon: icon
        }));    //添加标注
        G(long_id).value = pp.lng;
        G(lat_id).value = pp.lat;
        var sContent =
            "<div class=\"mx-auto d-block\">" +
            "<h5 class=\"text-sm-left mt-2 mb-1\"><b>" + myValue + "</b></h5>" +
            "<div class=\"location text-sm-left\"><i " +
            "class=\"fa fa-search_map-marker\"></i>&nbsp;&nbsp;" + pp.lng + "°E," + pp.lat + "°N" +
            "</div>" +
            "</div>";
        var opts = {
            enableMessage: false
        };
        var infoWindow = new BMap.InfoWindow(sContent, opts);
        search_map.openInfoWindow(infoWindow);
    }

    var local = new BMap.LocalSearch(search_map, { //智能搜索
        onSearchComplete: myFun
    });
    local.search(myValue);


}
