//  添加轨迹
var tracks = [];
var dates = {};

//页面切换
function showAddTrackBlock(doShow) {
    if (doShow === true) {
        $('#blur-orgin').addClass('blur');
        $('#blur-cover').removeClass('hidden-panel');

        $("#modify_track_button").hide();
        $("#add_track_button").show();

        event.stopPropagation();
    } else {
        $('#blur-orgin').removeClass('blur');
        $('#blur-cover').addClass('hidden-panel');
        event.stopPropagation();
        clear_input();
    }
}

function showModifyBlock(poi_name, poi_date, poi_long, poi_lat, poi_stime, poi_etime, date_index, poi_index) {
    $("#date-picker").val(poi_date);
    $("#search-keyword").val(poi_name);
    $("#long-hidden").val(poi_long);
    $("#lat-hidden").val(poi_lat);
    $("#starttime-picker").val(poi_stime);
    $("#endtime-picker").val(poi_etime);
    $("#date-index-hidden").val(date_index);
    $("#poi-index-hidden").val(poi_index);


    $("#modify_track_button").show();
    $("#add_track_button").hide();
    $("#center-refresh").click();

    $('#blur-orgin').addClass('blur');
    $('#blur-cover').removeClass('hidden-panel');
}


function modifyTrack(panel_id, old_date_index_str, old_poi_index_str) {
    var old_date_index = parseInt(old_date_index_str);
    var old_poi_index = parseInt(old_poi_index_str);
    var date = $("#date-picker").val();
    var name = $("#search-keyword").val();
    var long = parseFloat($("#long-hidden").val()).toFixed(6) + "";
    var lat = parseFloat($("#lat-hidden").val()).toFixed(6) + "";
    var starttime = $("#starttime-picker").val();
    var endtime = $("#endtime-picker").val();

    var old_date = tracks[old_date_index].date;
    if (date === old_date) {
        var poi = tracks[old_date_index].pois[old_poi_index];
        poi.name = name;
        poi.coordinate.lat = lat;
        poi.coordinate.long = long;

        poi.time.starttime = starttime;
        poi.time.endtime = endtime;
        generateTrackListPanel(panel_id);
        showAddTrackBlock(false);
    } else {
        deleteTrack(panel_id, old_date_index, old_poi_index);
        add_track(date, name, long, lat, starttime, endtime);
        generateTrackListPanel(panel_id);
        showAddTrackBlock(false);
    }

}

// 添加轨迹
function addNewTrack(panel_id) {
    var date = $("#date-picker").val();
    var name = $("#search-keyword").val();
    var long = parseFloat($("#long-hidden").val()).toFixed(6) + "";
    var lat = parseFloat($("#lat-hidden").val()).toFixed(6) + "";
    var starttime = $("#starttime-picker").val();
    var endtime = $("#endtime-picker").val();

    add_track(date, name, long, lat, starttime, endtime);

    generateTrackListPanel(panel_id);
    showAddTrackBlock(false);
}

function add_track(date, name, long, lat, starttime, endtime) {
    if (date in dates) {
        tracks[dates[date]].pois.push({
            name: name,
            coordinate: {
                long: long,
                lat: lat
            },
            time: {
                starttime: starttime,
                endtime: endtime
            }
        });
    } else {
        dates[date] = tracks.length;
        tracks.push({
            date: date,
            pois: [{
                name: name,
                coordinate: {
                    long: long,
                    lat: lat
                },
                time: {
                    starttime: starttime,
                    endtime: endtime
                }
            }]
        })
    }
}

function generateTrackListPanel(panel_id) {
    var innerHtml = "";
    for (var date_index in tracks) {
        innerHtml += "<div class=\"col-md-4\">" +
            "<div class=\"card\">" +
            "<div class=\"card-header\">" +
            "<strong class=\"card-title\">" +
            tracks[date_index].date +
            "</strong></div>" +
            "<div class=\"card-body text-box\" id=\"text-" + tracks[date_index].date + "\">";
        for (var poi_index in tracks[date_index].pois) {
            var poi = tracks[date_index].pois[poi_index];
            innerHtml += "<div class=\"mx-auto\"><div class='row'><div class=\"col-md-10 col-sm-12\" id=\"poi-" + date_index + "-" + poi_index + "\">" +
                "<h5 class=\"text-sm-center mt-2 mb-1\"><b>" + poi.name + "</b></h5>" +
                "<div class=\"location text-sm-center\"><i class=\"fa fa-map-marker\"></i>&nbsp;&nbsp;" +
                poi.coordinate.long + "°E, " + poi.coordinate.lat + "°N" +
                "</div><div class=\"location text-sm-center\"><i class=\"fa fa-clock-o\"></i>&nbsp;&nbsp;" +
                poi.time.starttime + " - " + poi.time.endtime +
                "</div></div><div class=\"col-md-1 col-sm-6 my-auto no-padding\">" +
                "<a href='javascript:void(0)' class='btn btn-outline-success btn-sm' title='修改该轨迹信息' " +
                "onclick='showModifyBlock(\"" + poi.name + "\",\"" + tracks[date_index].date + "\",\"" + poi.coordinate.long + "\",\"" + poi.coordinate.lat + "\",\"" + poi.time.starttime + "\",\"" + poi.time.endtime + "\"," + date_index + "," + poi_index + ")'><i class='fa fa-bars'></i></a></div>" +
                "<div class=\"col-md-1 col-sm-6 my-auto no-padding\">" +
                "<a href='javascript:void(0)' class=\"btn btn-outline-secondary btn-sm\" title='删除当前轨迹' " +
                "onclick='deleteTrack(\"" + panel_id + "\"," + date_index + "," + poi_index + ")'><i class='fa fa-times'></i></a></div>" +
                "</div></div><hr>"
        }
        innerHtml += "</div></div></div>";
    }
    document.getElementById(panel_id).innerHTML = innerHtml;
    G("track-info").value=JSON.stringify(tracks);
}

function deleteTrack(panel_id, date_index, poi_index) {
    tracks[date_index].pois.splice(poi_index, 1);
    if (tracks[date_index].pois.length === 0) {
        var date = tracks[date_index].date;
        tracks.splice(date_index, 1);
        delete dates.date;
    }
    generateTrackListPanel(panel_id);
}

function clear_input() {
    $("#date-picker").val("");
    $("#search-keyword").val("");
    $("#long-hidden").val("");
    $("#lat-hidden").val("");
    $("#starttime-picker").val("");
    $("#endtime-picker").val("");
    startTime = 0;
    endTime = 9999;
}