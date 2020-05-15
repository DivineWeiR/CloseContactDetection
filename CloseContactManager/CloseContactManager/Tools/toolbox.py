import datetime
import json
import time
from CloseContactManager.Tools.push_message_channel import Channel, ChannelException


def status_transfer(status_input):
    statuses = {
        '0': '零风险',
        '1': '密切接触者-低风险',
        '2': '密切接触者-中风险',
        '3': '密切接触者-高风险',
        '4': '疑似病例',
        '5': '确诊病例',
        '6': '无症状感染者',
        '零风险': '0',
        '密切接触者-低风险': '1',
        '密切接触者-中风险': '2',
        '密切接触者-高风险': '3',
        '疑似病例': '4',
        '确诊病例': '5',
        '无症状感染者': '6'

    }
    return statuses[status_input]


def get_status_color(status_code):
    status_color = {
        '0': '#00c292',
        '1': '#fb9678',
        '2': '#fb693e',
        '3': '#fb0005',
        '4': '#c40005',
        '5': '#640004',
        '6': '#444444'
    }
    return status_color[status_code]


def get_current_time():
    return datetime.datetime.strftime(datetime.datetime.now(), "%Y-%m-%d %H:%M:%S")


def generate_response(code, message=None, data=None):
    return json.dumps({
        "status_code": code,
        "message": message,
        "data": data
    })


def datestr2timestamp(time_str, time_format="%Y-%m-%d"):
    time_ = time.strptime(time_str, time_format)
    return int(time.mktime(time_))


def timestr2timestamp(time_str):
    hour, min = time_str.split(':')
    return int(hour) * 100 + int(min)


def time_compare(time1, time2):
    hour1, min1 = time1.split(':')
    hour2, min2 = time2.split(':')

    if int(hour1) > int(hour2):
        return 1
    elif int(hour1) < int(hour2):
        return -1
    else:
        if int(min1) > int(min2):
            return 1
        elif int(min1) < int(min2):
            return -1
        else:
            return 0


# 饼图数据及标签
def get_status_data_piechart(raw_data, required_user_type='0123456'):
    legend_data, series_data, color_data = [], [], []
    for i in required_user_type:
        legend_data.append(status_transfer(i))
        series_data.append(raw_data[i])
        color_data.append(get_status_color(i))
    return legend_data, series_data, color_data


# 条形图数据 状态分布
def get_status_data_barchart(total_raw_data, new_raw_data, required_user_type='0123456'):
    total_data, new_data, legend_data = [], [], []
    for i in required_user_type:
        legend_data.append(status_transfer(i))
        total_data.append(total_raw_data[i])
        new_data.append(new_raw_data[i])
    return legend_data, total_data, new_data


# 条形图数据 状态分布
def get_help_and_report_data_barchart(total_raw_data, new_raw_data):
    total_data, new_data, legend_data = [], [], []
    legend_data.append('个人求助')
    total_data.append(total_raw_data['help_count'])
    new_data.append(new_raw_data['help_count'])
    legend_data.append('举报信息')
    total_data.append(total_raw_data['report_count'])
    new_data.append(new_raw_data['report_count'])
    return legend_data, total_data, new_data


def push_message_to_android(user_status, msg_json):
    opts = {'msg_type': 1, 'expires': 300}
    msg = json.dumps(
        {"title": "新增用户轨迹", "description": "新增一名" + user_status + "用户", "custom_content": msg_json})
    print(msg)

    c = Channel()
    try:
        ret = c.pushMsgToAll(msg, opts)
        print('ret: '),
        print(ret)
        print(c.getRequestId())
        return True
    except ChannelException as e:
        print("[Failed]Push Message to Android")
        print('[code]'),
        print(e.getLastErrorCode())
        print('[msg]'),
        print(e.getLastErrorMsg())
        print('[request id]'),
        print(c.getRequestId())
        return False
