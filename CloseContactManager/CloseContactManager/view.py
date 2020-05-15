from django.shortcuts import render, redirect, HttpResponse
from django.http import FileResponse
from CloseContactManager.Tools import database_operater as dbop
from CloseContactManager.Tools import toolbox

import json
import os
from multiprocessing import Process


#####################################

#           管理员功能函数             #

#####################################

# 进入管理员平台首页 查看所有密切接触者和所有确诊、疑似病例
def go_home(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get('village_code')
        disease_level = request.session.get('disease_level')
        administrative_name = request.session.get('administrative_name')
        # users = dbop.get_all_users()
        total_counts, new_counts = dbop.get_statistic_info(village_code)

        legend_data, series_data, color_data = toolbox.get_status_data_piechart(total_counts)
        _, total_data, new_data = toolbox.get_status_data_barchart(total_counts, new_counts)
        legend_data_hr, total_hr_data, new_hr_data = toolbox.get_help_and_report_data_barchart(total_counts, new_counts)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']

        print(disease_level)
        print(new_contact_count, new_help_count, new_report_count)
        return render(request, "admin/admin_home.html", locals())


def show_all_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get('village_code')
        page = 1
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        processed = request.GET.get("processed")
        print(processed)
        limit = 10
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']
        total_users = total_counts['total']
        new_users = new_counts['total']
        prev_enable, next_enable = 1, 1
        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            users = dbop.get_users(village_code, processed=processed, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_users:
                next_enable = 0
        else:
            users = dbop.get_users(village_code, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_users:
                next_enable = 0
        print(new_contact_count, new_help_count, new_report_count)
        return render(request, "admin/all_list.html", locals())


# 查看密切接触者
def show_contacts_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get('village_code')
        page = 1
        limit = 10
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # users = dbop.get_close_contacts()
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']
        total_users = total_counts['1'] + total_counts['2'] + total_counts['3']
        new_users = new_counts['1'] + new_counts['2'] + new_counts['3']
        prev_enable, next_enable = 1, 1

        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            users = dbop.get_users(village_code, query_statuses="123", processed=processed, limit=limit,
                                   offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_users:
                next_enable = 0
        else:
            users = dbop.get_users(village_code, query_statuses="123", limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_users:
                next_enable = 0
        # new_contact_count = dbop.get_new_close_contacts_count()
        return render(request, "admin/close_contacts_list.html", locals())


# 查看病例
def show_patients_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get('village_code')
        page = 1
        limit = 10
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # users = dbop.get_patients()

        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']
        print(new_contact_count, new_help_count, new_report_count)
        total_users = total_counts['4'] + total_counts['5'] + total_counts['6']
        new_users = new_counts['4'] + new_counts['5'] + new_counts['6']
        prev_enable, next_enable = 1, 1

        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            users = dbop.get_users(village_code, query_statuses="456", processed=processed, limit=limit,
                                   offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_users:
                next_enable = 0
        else:
            users = dbop.get_users(village_code, query_statuses="456", limit=10, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_users:
                next_enable = 0
        return render(request, "admin/patients_list.html", locals())


# 确认密切接触者
def check_close_contact(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.GET:
            uid = request.GET.get("uid")
            success = dbop.check_user(uid, check_info=True)
            if success:
                message = "编号" + uid + "的密切接触者已核实成功。"
                message_type = 0

            else:
                message = "编号" + uid + "的密切接触者未核实成功，请重新尝试！"
                message_type = 1
        else:
            message = "未找到需核实的密切接触者编号，请重新尝试!"
            message_type = 1
        village_code = request.session.get('village_code')
        page = 1
        limit = 10
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # users = dbop.get_close_contacts()
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']
        total_users = total_counts['1'] + total_counts['2'] + total_counts['3']
        new_users = new_counts['1'] + new_counts['2'] + new_counts['3']
        prev_enable, next_enable = 1, 1

        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            users = dbop.get_users(village_code, query_statuses="123", processed=processed, limit=limit,
                                   offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_users:
                next_enable = 0
        else:
            users = dbop.get_users(village_code, query_statuses="123", limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_users:
                next_enable = 0
        return render(request, "admin/close_contacts_list.html", locals())
        # return redirect("/admin/close_contacts_list", locals())


# 删除确诊病例
def delete_patient(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.GET:
            uid = request.GET.get("uid")
            success = dbop.delete_user(uid)
            if success:
                message = "编号" + uid + "的用户已删除成功。"
                message_type = 0

            else:
                message = "编号" + uid + "的用户未删除成功，请重新尝试！"
                message_type = 1
        else:
            message = "未找到需删除的用户编号，请重新尝试!"
            message_type = 1
        village_code = request.session.get('village_code')
        page = 1
        limit = 10
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count, new_report_count = new_counts['help_count'], new_counts['report_count']
        print(new_contact_count, new_help_count, new_report_count)
        total_users = total_counts['4'] + total_counts['5'] + total_counts['6']
        new_users = new_counts['4'] + new_counts['5'] + new_counts['6']
        prev_enable, next_enable = 1, 1

        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            users = dbop.get_users(village_code, query_statuses="456", processed=processed, limit=limit,
                                   offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_users:
                next_enable = 0
        else:
            users = dbop.get_users(village_code, query_statuses="456", limit=10, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_users:
                next_enable = 0
        return render(request, "admin/patients_list.html", locals())
        # return redirect("/admin/patients_list", locals())


# 显示详情页面
def show_personal_page(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.GET:
            uid = request.GET.get("uid")
            user = dbop.get_single_user(uid)
            if user:
                new_contact_count = dbop.get_new_close_contacts_count()
                new_help_count, new_report_count = dbop.get_new_helps_and_reports_count()
                print(new_contact_count, new_help_count, new_report_count)
                return render(request, "admin/personal_page.html", locals())
    return redirect('/admin/admin_home/')


# 显示添加用户界面
def show_add_user_page(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        provinces = dbop.get_provinces()
        # administrative_tree=dbop.get_administrative_tree()
        # print(administrative_tree)
        return render(request, "admin/personal_page_add.html", locals())


def add_user(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.POST:
            id_number = request.POST.get('id_number')
            name = request.POST.get('name')
            gender = request.POST.get('gender')
            phone_number = request.POST.get('phone_number')
            status = request.POST.get('status')
            user_village_code = request.POST.get('village_code')
            address = request.POST.get('address')
            track = request.POST.get('track')
            print(id_number, name, gender, phone_number, status, user_village_code, address, track)
            if id_number and name and phone_number and status and user_village_code and address and track:
                track = sorted(json.loads(track), key=lambda s: toolbox.datestr2timestamp(s['date']))
                for daily_track in track:
                    daily_track['pois'] = sorted(daily_track['pois'],
                                                 key=lambda s: toolbox.timestr2timestamp(s['time']['endtime']))
                user = {
                    'id_number': id_number,
                    'name': name,
                    'gender': gender,
                    'phone_number': phone_number,
                    'address': address,
                    'village_code': user_village_code,
                    'status': status,
                    'track': json.dumps(track)
                }
                result = dbop.insert_user_info(user)

                # 向安卓推送新增病例信息
                if int(status) >= 4:
                    sub_proc = Process(target=toolbox.push_message_to_android,
                                       args=(toolbox.status_transfer(status), track))
                    sub_proc.start()

                if result == 200:
                    message = "添加用户成功"
                    request.session['message'] = message
                    return redirect("/admin/patients_list")
                else:
                    message = "数据库添加用户失败！"
                    request.session['message'] = message
                    return redirect("/admin/show_add_user_page")
            else:
                message = "用户数据不全！"
                return redirect("/admin/show_add_user_page")
        message = "添加用户失败！"
        return redirect("/admin/show_add_user_page")


# 修改用户状态
def modify_user_status(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.GET:
            uid = request.GET.get("uid")
            new_status = request.GET.get('new_status')
            success = dbop.update_user_status_track(uid, status=new_status)
            if success:
                user = dbop.get_single_user(uid)
                message = "修改用户状态成功。"
                message_type = 0
                new_contact_count = dbop.get_new_close_contacts_count()
                new_help_count, new_report_count = dbop.get_new_helps_and_reports_count()
                return render(request, "admin/personal_page.html", locals())
    message = "修改用户状态失败！"
    message_type = 1
    new_contact_count = dbop.get_new_close_contacts_count()
    new_help_count, new_report_count = dbop.get_new_helps_and_reports_count()
    return render(request, "admin/personal_page.html", locals())


# 查看个人求助
def show_helps_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get("village_code")
        page = 1
        limit = 10
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # helps, reports = dbop.get_all_helps_and_reports()
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        print(total_counts, new_counts)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_report_count = new_counts['report_count']
        total_helps = total_counts['help_count']
        new_helps = new_counts['help_count']
        prev_enable, next_enable = 1, 1
        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            helps = dbop.get_helps(village_code, processed=processed, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_helps:
                next_enable = 0
        else:
            helps = dbop.get_helps(village_code, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_helps:
                next_enable = 0
        return render(request, "admin/helps_list.html", locals())


# 查看举报
def show_reports_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get("village_code")
        page = 1
        limit = 5
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # helps, reports = dbop.get_all_helps_and_reports()
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        print(total_counts, new_counts)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        new_help_count = new_counts['help_count']
        total_reports = total_counts['report_count']
        new_reports = new_counts['report_count']
        prev_enable, next_enable = 1, 1
        if request.GET.get("processed"):
            processed = request.GET.get("processed")
            reports = dbop.get_helps(village_code, processed=processed, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= new_reports:
                next_enable = 0
        else:
            reports = dbop.get_reports(village_code, limit=limit, offset_epoch=page - 1)
            if page == 1:
                prev_enable = 0
            if (page) * limit >= total_reports:
                next_enable = 0
        return render(request, "admin/reports_list.html", locals())


# 【待删除】查看个人求助和举报
def show_helps_and_reports_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get("village_code")
        page = 1
        limit = 5
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        # helps, reports = dbop.get_all_helps_and_reports()
        helps, reports = dbop.get_helps_and_reports(village_code, offset_epoch=page - 1)
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        return render(request, "admin/helps_and_reports_list.html", locals())


# 【待删除】查看新增个人求助和举报
def show_new_helps_and_reports_list(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        village_code = request.session.get("village_code")
        page = 1
        if request.GET.get("page"):
            page = int(request.GET.get("page"))
        helps, reports = dbop.get_helps_and_reports(village_code, processed='0', offset_epoch=page - 1)
        total_counts, new_counts = dbop.get_statistic_info(village_code)
        new_contact_count = new_counts['1'] + new_counts['2'] + new_counts['3']
        return render(request, "admin/helps_and_reports_list.html", locals())


# 确认个人求助和举报
def check_help_and_report(request):
    if not request.session.get('is_login', None):
        return redirect("/show_login_page/")
    else:
        if request.GET:
            item_id = request.GET.get('item_id')
            item_type = request.GET.get('item_type')
            success = dbop.modify_help_and_report(item_id)
            if success:
                helps, reports = dbop.get_all_helps_and_reports()
                if item_type == '0':
                    message = "编号" + item_id + "的个人求助已处理成功。"
                else:
                    message = "编号" + item_id + "的举报已处理成功。"
                message_type = 0

            else:
                helps, reports = dbop.get_all_helps_and_reports()
                if item_type == '0':
                    message = "编号" + item_id + "的个人求助未处理成功，请重新尝试！"
                else:
                    message = "编号" + item_id + "的举报未处理成功，请重新尝试！"
                message_type = 1
        else:
            helps, reports = dbop.get_all_helps_and_reports()
            message = "未找到需处理的个人求助（举报）编号，请重新尝试!"
            message_type = 1
        return redirect("/admin/helps_and_reports_list/", locals())
        # return render(request, "admin/helps_and_reports_list.html", locals())


# 修改防控等级
def update_disease_level(request):
    new_level = request.GET.get('new_level')
    dbop.modify_disease_level(new_level)

    return redirect('/admin/admin_home')


def track_search(request):
    return render(request, "admin/track_search.html")


#####################################

#           管理员登录函数             #

#####################################
# 显示管理员登录界面
def show_login_page(request):
    return render(request, "admin/login.html")


# 显示管理员注册界面
def show_register_page(request):
    return render(request, "admin/register.html")


# 管理员登录
def admin_login(request):
    if request.POST:
        uid = request.POST.get('account')
        password = request.POST.get('password')
        village_code = dbop.get_admin(uid, password)
        administrative_division = dbop.get_administrative_name(village_code)
        administrative_name = ''.join(administrative_division)
        disease_level = dbop.get_disease_level()
        if village_code is not None:
            request.session['is_login'] = True
            request.session['user_name'] = uid
            request.session['village_code'] = village_code
            request.session['administrative_division'] = administrative_division
            request.session['administrative_name'] = administrative_name
            request.session['disease_level'] = disease_level
            print(administrative_name)
            return redirect('/admin/admin_home/')
        message = "账号或密码错误。"
    else:
        message = "非法登录。"
    return render(request, 'admin/login.html', locals())


# 管理员注册
def admin_register(request):
    if request.session.get('is_login', None):
        # 登录状态不允许注册。你可以修改这条原则！
        return redirect("admin/admin_home")
    if request.method == "POST":
        uid = request.POST.get('account')
        village_code = request.POST.get('code')
        password1 = request.POST.get('password1')
        password2 = request.POST.get('password2')
        if password1 != password2:  # 判断两次密码是否相同
            message = "两次输入的密码不同！"
            return render(request, 'admin/register.html', locals())
        elif dbop.get_admin(uid):
            message = "该用户名已被注册。"
            return render(request, 'admin/register.html', locals())
        elif dbop.is_administrative_code_existed(village_code):
            message = "当前区域已有管理员。"
        else:
            flag = dbop.insert_admin(uid, password1, village_code)
            if flag:
                return redirect('/show_login_page')  # 自动跳转到登录页面
            else:
                message = "注册失败，请稍后尝试！"
                return render(request, 'admin/register.html', locals())
    return render(request, 'admin/register.html', locals())


# 管理员登出
def admin_logout(request):
    print(request.session)
    if not request.session.get('is_login', None):
        print("is_login:None")
        return redirect("/admin/admin_home/")
    print("is_login:True")
    request.session.flush()  # flush会一次性清空session中所有的内容
    return redirect("/show_login_page")


#####################################

#              其他函数               #

#####################################
def get_administrative_tree(request):
    if request.GET.get("code"):
        administrative_code = request.GET.get("code")
        if administrative_code:
            administrative_tree = dbop.get_administrative_tree(administrative_code)
            code = 200
            if len(administrative_code) == 2:
                level = 0
            elif len(administrative_code) == 6:
                level = 1
            message = '获取行政区划信息成功'
            data = {"level": level, "tree": administrative_tree}
        else:
            code = 500
            message = "错误的行政区划编码"
            data = []
    else:
        code = 500
        message = "未获取到行政区划信息"
        data = []
    print(code, message, data)
    return HttpResponse(toolbox.generate_response(code, message, data))


def download(request):
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # 项目根目录
    file_path = os.path.join(base_dir, 'download_files', "疫情通.apk")  # 下载文件的绝对路径

    if not os.path.isfile(file_path):  # 判断下载文件是否存在
        return HttpResponse("Sorry but Not Found the File")

    file = open(file_path, 'rb')

    try:
        response = FileResponse(file)
        response['Content-Type'] = 'application/octet-stream'
        response['Content-Disposition'] = 'attachment;filename="CloseClear.apk"'
        return response
    except:
        return HttpResponse("Sorry but Not Found the File")


#####################################

#            用户操作函数             #

#####################################


# 客户端用户获取病例信息
def client_get_patients(request):
    patients_info = dbop.get_patients_by_android()
    print(patients_info)
    if patients_info:
        code = 200
        message = '获取病例信息成功'
        data = patients_info
    else:
        code = 500
        message = "未获取到病例信息"
        data = []

    return HttpResponse(toolbox.generate_response(code, message, data))


# 客户端获取所有病例到访过的poi
def client_get_poi_by_date(request):
    patients_info = dbop.get_patients_by_android()
    print(patients_info)
    if patients_info:
        poi_map = {}
        for item in patients_info:
            for daily_track in item['track']:
                if daily_track['date'] not in poi_map:
                    poi_list = []
                    for p in daily_track['pois']:
                        poi_list.append(p['name'])
                    poi_map[daily_track['date']] = poi_list
                else:
                    for p in daily_track['pois']:
                        if p['name'] not in poi_map[daily_track['date']]:
                            poi_map[daily_track['date']].append(p['name'])
        new_map = {}

        keys = sorted(poi_map.keys(), key=toolbox.datestr2timestamp)
        print(keys)
        for key in keys:
            if poi_map[key]:
                new_map[key] = list(set(poi_map[key]))
        code = 200
        message = '获取病例信息成功'
        data = new_map
    else:
        code = 500
        message = "未获取到病例信息"
        data = []
    print(data)
    return HttpResponse(toolbox.generate_response(code, message, data))


# 客户端获取所有病例到访过的poi
def client_get_poi_by_date_high_level(request):
    patients_info = dbop.get_patients_by_android()
    print(patients_info)
    if patients_info:
        poi_map = {}
        for item in patients_info:
            for daily_track in item['track']:
                if daily_track['date'] not in poi_map:
                    poi_list = []
                    poi_list.extend(daily_track['pois'])
                    poi_map[daily_track['date']] = poi_list
                else:
                    for p in daily_track['pois']:
                        if p not in poi_map[daily_track['date']]:
                            poi_map[daily_track['date']].append(p)
        code = 200
        message = '获取病例信息成功'
        data = poi_map
    else:
        code = 500
        message = "未获取到病例信息"
        data = []

    return HttpResponse(toolbox.generate_response(code, message, data))


# 客户端获取疫情防控等级
def client_get_disease_level(request):
    disease_level = dbop.get_disease_level()
    print(disease_level)
    if disease_level:
        code = 200
        message = '获取疫情等级成功'
        data = disease_level
    else:
        code = 500
        message = '未获取到疫情等级'
        data = []
    return HttpResponse(toolbox.generate_response(code, message, data))


# 客户端上传注册用户信息
def client_post_register_info(request):
    if request.POST:
        name = request.POST.get('name')
        id_number = request.POST.get('id_number')
        village_code = request.POST.get('village_code')
        user = {
            "name": name,
            "id_number": id_number,
            "village_code": village_code
        }
        print(user)
        success = dbop.insert_user(user)
        if success:
            code = 200
            message = "上传成功"
        else:
            code = 500
            message = "上传失败"
    else:
        code = 1000
        message = "请求失败"
    return HttpResponse(toolbox.generate_response(code, message))


# 客户端上传用户信息
def client_post_user(request):
    if request.POST:
        name = request.POST.get('name')
        id_number = request.POST.get('id_number')
        gender = request.POST.get('gender')
        phone_number = request.POST.get('phone_number')
        village_code = request.POST.get('village_code')
        address = request.POST.get('address')
        status = request.POST.get('status')
        track = json.loads(request.POST.get('track'))
        new_track = []
        for daily_track in track:
            date = daily_track['date']
            daily_pois = daily_track['pois']
            if daily_pois:
                daily_pois = sorted(daily_pois, key=lambda s: toolbox.timestr2timestamp(s['time']['endtime']))
                new_daily_pois = []
                print(daily_pois)
                last_name = daily_pois[0]['name']
                start_time = daily_pois[0]['time']['starttime']
                end_time = daily_pois[0]['time']['endtime']
                for poi in daily_pois:
                    if poi['name'] == last_name:
                        end_time = poi['time']['endtime']
                    else:
                        new_poi = {
                            "name": last_name,
                            "coordinate": poi['coordinate'],
                            "time": {
                                "starttime": start_time,
                                "endtime": end_time
                            }
                        }
                        new_daily_pois.append(new_poi)
                        last_name = poi['name']
                        start_time = poi['time']['starttime']
                        end_time = poi['time']['endtime']
                if start_time == end_time:
                    start_time = "00:00"
                    end_time = "24:00"
                new_poi = {
                    "name": last_name,
                    "coordinate": daily_pois[-1]['coordinate'],
                    "time": {
                        "starttime": start_time,
                        "endtime": end_time
                    }
                }
                new_daily_pois.append(new_poi)
                new_track.append({
                    'date': date,
                    'pois': new_daily_pois
                })
        user = {
            "name": name,
            "id_number": id_number,
            "gender": gender,
            "phone_number": phone_number,
            "village_code": village_code,
            "address": address,
            "status": status,
            "track": json.dumps(new_track)
        }
        print(user)
        success = dbop.insert_user_info(user)
        if success:
            code = 200
            message = "上传成功"
        else:
            code = 500
            message = "上传失败"
    else:
        code = 1000
        message = "请求失败"
    return HttpResponse(toolbox.generate_response(code, message))


# 客户端上传个人求助信息
def client_post_help(request):
    if request.POST:
        name = request.POST.get('name')
        id_number = request.POST.get('id_number')
        gender = request.POST.get('gender')
        phone_number = request.POST.get('phone_number')
        village_code = request.POST.get('village_code')
        address = request.POST.get('address')
        help = {
            "name": name,
            "id_number": id_number,
            "gender": gender,
            "phone_number": phone_number,
            "address": address,
            "village_code": village_code,
            "acused_name": None,
            "acused_gender": None,
            "acused_phone_number": None,
            "acused_address": None,
            "info_type": '0',
            "witness_location": None
        }
        success = dbop.insert_help_and_report(help)
        print(success)
        if success:
            code = 200
            message = "上传成功"
        else:
            code = 500
            message = "上传失败"
    else:
        code = 1000
        message = "请求失败"
    return HttpResponse(toolbox.generate_response(code, message))


# 客户端上传举报信息
def client_post_report(request):
    if request.POST:
        name = request.POST.get('name')
        id_number = request.POST.get('id_number')
        gender = request.POST.get('gender')
        phone_number = request.POST.get('phone_number')
        village_code = request.POST.get('village_code')
        address = request.POST.get('address')
        acused_name = request.POST.get('acused_name')
        acused_gender = request.POST.get('acused_gender')
        acused_phone_number = request.POST.get('acused_phone_number')
        acused_address = request.POST.get('acused_address')
        witness_location = request.POST.get('witness_location')
        report = {
            "name": name,
            "id_number": id_number,
            "gender": gender,
            "phone_number": phone_number,
            "village_code": village_code,
            "address": address,
            "acused_name": acused_name,
            "acused_gender": acused_gender,
            "acused_phone_number": acused_phone_number,
            "acused_address": acused_address,
            "info_type": '1',
            "witness_location": witness_location
        }
        success = dbop.insert_help_and_report(report)
        if success:
            code = 200
            message = "上传成功"
        else:
            code = 500
            message = "上传失败"
    else:
        code = 1000
        message = "请求失败"
    return HttpResponse(toolbox.generate_response(code, message))
