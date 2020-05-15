import datetime
import json
import traceback
import datetime
from multiprocessing import Process

# import mysql.connector as pymysql
import pymysql
from CloseContactManager.Tools.database_config import database_host, database_user, database_passwd, database_name
from CloseContactManager.Tools import toolbox


# 获取管理员
def get_admin(uid, password=None):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    if password is not None:
        cursor.execute("SELECT `village_code` FROM `admin` WHERE `username` = %s AND `password` = %s",
                       [uid, password])
    else:
        cursor.execute("SELECT `village_code` FROM `admin` WHERE `username`= %s", [uid, ])
    result = cursor.fetchone()

    cursor.close()
    conn.close()
    if result:
        return result[0]
    else:
        return None


# 插入管理员
def insert_admin(uid, password, village_code):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO `admin` (`username`,`password`,`village_code`) VALUES (%s,%s,%s)",
                       [uid, password, village_code])
        conn.commit()
        return True
    except:
        print(traceback.print_exc())
        return False
    finally:
        cursor.close()
        conn.close()


# 按状态获取用户
def get_users(mask_code=None, query_statuses=None, processed=None, limit=10, offset_epoch=0):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    query_sentences = []
    where_clauses = []
    if mask_code:
        where_clauses.append("`village_code` REGEXP '%s'" % mask_code)
    if query_statuses:
        where_clause = "("
        or_clauses = []
        for q_status in query_statuses:
            or_clauses.append("`status`='%s'" % q_status)
        where_clause += " OR ".join(or_clauses)
        where_clause += ")"
        where_clauses.append(where_clause)
    if processed:
        where_clauses.append("`processed`='%s'" % processed)
    if len(where_clauses) > 0:
        query_sentences.append("WHERE ")
        if len(where_clauses) == 1:
            query_sentences.append(where_clauses[0])
        else:
            query_sentences.append(" AND ".join(where_clauses))
    query_sentences.append("ORDER BY `status` ASC, `village_code` ASC")
    if limit:
        query_sentences.append("LIMIT {limit} OFFSET {offset}".format(limit=limit, offset=limit * offset_epoch))
    query_sentence = " ".join(query_sentences)
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status`,`processed`,`village_code` FROM `users_info` " \
          "{query_sentence}".format(query_sentence=query_sentence)
    print(sql)
    cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5],
            'processed': result[6],
            'village_code': result[7]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info


# 【待删除】获取所有用户
def get_all_users(mask_code=''):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    mask_code = str(mask_code).ljust(12, '.')
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status` FROM `users_info`" \
          " WHERE `village_code` REGEXP '{mask_code}' ORDER BY `village_code` ASC, `status` ASC".format(
        mask_code=mask_code)
    cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            # 'status':toolbox.status_transfer(result[5].decode('utf-8')),
            'status_code': result[5]
            # 'track': json.loads(result[6]),
            # 'date': result[7],
            # 'processed': result[8]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info


# 【待删除】获取病例
def get_patients(mask_code=''):
    mask_code = str(mask_code).ljust(12, '.')
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status`,`processed` FROM `users_info` " \
          "WHERE `village_code` REGEXP '{mask_code}' AND  (`status`='4' OR `status`='5')" \
          "ORDER BY `village_code` ASC, `status` ASC".format(mask_code=mask_code)
    cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5],
            'processed': result[6]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info


# 【待删除】
def get_new_patients(mask_code=''):
    mask_code = str(mask_code).ljust(12, '.')
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status`,`processed` FROM `users_info` " \
          "WHERE `village_code` REGEXP '{mask_code}' AND `processed`='0' AND (`status`='4' OR `status`='5')" \
          "ORDER BY `village_code` ASC, `status` ASC".format(mask_code=mask_code)
    users_num = cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5],
            'processed': result[6]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info, users_num


# 【待删除】安卓获取病例信息
def get_patients_by_android():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM `users_info_old` WHERE `status`='4' OR `status`='5' ORDER BY `status` ASC")
    results = cursor.fetchall()
    users_info_old = []
    for result in results:
        user = {
            'name': result[1],
            'gender': result[2],
            # 'status': toolbox.status_transfer(result[5].decode('utf-8')),
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5],
            'track': json.loads(result[6])
        }
        users_info_old.append(user)

    cursor.close()
    conn.close()
    return users_info_old


# 安卓获取病例轨迹
def get_patients_track_by_android():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM `total_track`")
    results = cursor.fetchall()
    tracks = {}
    for result in results:
        tracks[result[0].strftime("%Y-%m-%d %H:%M:%S")] = result[1]

    cursor.close()
    conn.close()
    return tracks


# 获取密切接触者
def get_close_contacts(mask_code=''):
    mask_code = str(mask_code).ljust(12, '.')
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status`,`processed` FROM `users_info` " \
          "WHERE `village_code` REGEXP '{mask_code}' AND  (`status`='0' OR`status`='1' OR `status`='2' OR `status`='3')" \
          "ORDER BY `village_code` ASC, `status` ASC".format(mask_code=mask_code)
    cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info


# 获取新增密切接触者
def get_new_close_contacts(mask_code=''):
    mask_code = str(mask_code).ljust(12, '.')
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    sql = "SELECT `id_number`,`name`,`gender`,`phone_number`,`address`,`status`,`processed` FROM `users_info` " \
          "WHERE `village_code` REGEXP '{mask_code}' AND `processed`= '0' AND " \
          "(`status`='0' OR`status`='1' OR `status`='2' OR `status`='3')" \
          "ORDER BY `village_code` ASC, `status` ASC".format(mask_code=mask_code)
    users_num = cursor.execute(sql)
    results = cursor.fetchall()
    users_info = []
    for result in results:
        user = {
            'id_number': result[0],
            'name': result[1],
            'gender': result[2],
            'phone_number': result[3],
            'address': result[4],
            'status': toolbox.status_transfer(str(result[5])),
            'status_code': result[5],
            'processed': result[6]
        }
        users_info.append(user)

    cursor.close()
    conn.close()
    return users_info, users_num


# 获取单一用户
def get_single_user(uid):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute(
        "SELECT `name`,`village_code`,`gender`,`phone_number`,`address`,`status` FROM `users_info` WHERE `id_number`=%s",
        [uid, ])
    basic_info = cursor.fetchone()
    cursor.execute("SELECT `track` FROM `users_track` WHERE `id_number`=%s", [uid, ])
    track_info = cursor.fetchone()
    if basic_info:
        administrative_sequence = get_administrative_name(basic_info[1])
        user = {
            'id_number': uid,
            'name': basic_info[0],
            'village_code': basic_info[1],
            'village_name_sequence': administrative_sequence,
            'village_name': ''.join(administrative_sequence),
            'gender': basic_info[2],
            'phone_number': basic_info[3],
            'address': basic_info[4],
            'status': toolbox.status_transfer(str(basic_info[5])),
            # 'status': toolbox.status_transfer(result[5].decode('utf-8')),
            'status_code': basic_info[5]
        }
        if track_info:
            user['track'] = json.loads(track_info[0])
            print(json.loads(track_info[0]))
        return user
    else:
        return None


# 插入用户（注册时）
def insert_user(user):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `name` FROM `users` WHERE `id_number`=(%s)", [user['id_number']])
        result = cursor.fetchone()
        print("RESULT:", result)
        if not result:
            cursor.execute(
                "INSERT INTO `users`(`id_number`, `name`, `village_code`) VALUES (%s,%s,%s)",
                [user['id_number'], user['name'], user['village_code']])
            conn.commit()

            return 200
        else:
            return 300
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 插入用户信息
def insert_user_info(user):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `name` FROM `users_info` WHERE `id_number`=(%s)", [user['id_number']])
        result = cursor.fetchone()
        print("RESULT:", result)
        if not result:
            cursor.execute(
                "INSERT INTO `users_info`(`id_number`, `name`, `gender`, `phone_number`,`address`, `village_code`, `status`,`update_time`,`processed`) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,str_to_date(%s,'%%Y-%%m-%%d %%H:%%i:%%s'),%s)",
                [user['id_number'], user['name'], user['gender'], user['phone_number'],
                 user['address'], user['village_code'], user['status'], toolbox.get_current_time(), '0'])
            cursor.execute(
                "INSERT INTO `users_track`(`id_number`,`track`,`update_time`,`processed`) VALUES(%s,%s,str_to_date(%s,'%%Y-%%m-%%d %%H:%%i:%%s'),%s)",
                [user['id_number'], user['track'], toolbox.get_current_time(), '0'])

            sub_proc = Process(target=update_all_item_in_statistic,
                               args=(user['village_code'], {str(user['status']): 1, }))
            sub_proc.start()

            conn.commit()
            return 200
        else:
            return 300
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 更新用户信息
def update_user_status_track(uid, status=None, track=None):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `village_code`,`status`,`processed` FROM `users_info` WHERE `id_number`=%s", [uid, ])
        village_code, old_status, processed = cursor.fetchone()
        if status is not None:
            cursor.execute("UPDATE `users_info` SET `status`=%s,`update_time`=%s,`processed`='0' WHERE `id_number`=%s",
                           [status, toolbox.get_current_time(), uid])
            if str(processed) == '1':
                sub_proc = Process(target=update_all_item_in_statistic,
                                   args=(village_code, {str(old_status): -1, str(status): 1}, '0'))
            else:
                sub_proc = Process(target=update_all_item_in_statistic,
                                   args=(village_code, {str(old_status): -1, str(status): 1}))
            sub_proc.start()

        if track is not None:
            cursor.execute("UPDATE `users_track` SET `track`=%s,`update_time`=%s,`processed`='0' WHERE `id_number`=%s",
                           [track, toolbox.get_current_time(), uid])
        conn.commit()
        return 200
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 【待删除】

# def insert_user(user):
#     conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
#                            charset="utf8mb4")
#     cursor = conn.cursor()
#     try:
#         cursor.execute("SELECT `name` FROM `users_info` WHERE `id_number`=(%s)", [user['id_number']])
#         result = cursor.fetchone()
#         print("RESULT:", result)
#         if result:
#             cursor.execute(
#                 "UPDATE `users_info_old` SET `status`=%s,`track`=%s,`date`=%s,`processed`='0' WHERE `id_number`=%s",
#                 [user['status'], user['track'], toolbox.get_current_time(), user['id_number']])
#         else:
#             cursor.execute(
#                 "INSERT INTO `users_info_old`(`id_number`, `name`, `gender`, `phone_number`,`address`, `status`,`track`,`date`,`processed`) "
#                 "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)",
#                 [user['id_number'], user['name'], user['gender'], user['phone_number'],
#                  user['address'], user['status'], json.dumps(user['track']), toolbox.get_current_time(), '0'])
#         conn.commit()
#         return True
#     except:
#         print(traceback.print_exc())
#         return False
#     finally:
#         cursor.close()
#         conn.close()


# 修改用户

# def modify_user(user):
#     conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
#                            charset="utf8mb4")
#     cursor = conn.cursor()
#     try:
#         cursor.execute("UPDATE `users_info_old` SET `status`=%s,`date`=%s,`processed`=%s WHERE `id_number`=%s",
#                        [user['status'], toolbox.get_current_time(), user['processed'], user['id_number']])
#         conn.commit()
#         return True
#     except:
#         print(traceback.print_exc())
#         return False
#     finally:
#         conn.close()
#         cursor.close()


# 修改用户


# 检查用户信息及轨迹
def check_user(uid, check_info=False, check_track=False):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `village_code`, `status` FROM `users_info` WHERE `id_number`=%s", [uid, ])
        village_code, status = cursor.fetchone()
        if check_info:
            cursor.execute("UPDATE `users_info` SET `update_time`=%s,`processed`=%s WHERE `id_number`=%s",
                           [toolbox.get_current_time(), '1', uid])

            sub_proc = Process(target=update_all_item_in_statistic, args=(village_code, {str(status): -1}, '1'))
            sub_proc.start()

        if check_track:
            cursor.execute("UPDATE `users_track` SET `update_time`=%s,`processed`=%s WHERE `id_number`=%s",
                           [toolbox.get_current_time(), '1', uid])

        conn.commit()
        return True
    except:
        print(traceback.print_exc())
        return False
    finally:
        conn.close()
        cursor.close()


# 删除用户
def delete_user(uid, delete_user_account=True, delete_user_info=True, delete_user_track=True):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `village_code`, `status`, `processed` FROM `users_info` WHERE `id_number`=%s", [uid, ])
        village_code, status, processed = cursor.fetchone()

        if delete_user_account:
            cursor.execute("DELETE FROM `users` WHERE `id_number`=%s", [uid, ])
        if delete_user_info:
            cursor.execute("DELETE FROM `users_info` WHERE `id_number`=%s", [uid, ])
        if delete_user_track:
            cursor.execute("DELETE FROM `users_track` WHERE `id_number`=%s", [uid, ])

        if str(processed) == '1':
            sub_proc = Process(target=update_all_item_in_statistic, args=(village_code, {str(status): -1}, '0'))
        else:
            sub_proc = Process(target=update_all_item_in_statistic, args=(village_code, {str(status): -1}))
        sub_proc.start()

        conn.commit()
        return True
    except:
        print(traceback.print_exc())
        return False
    finally:
        conn.close()
        cursor.close()


# [待删除]获取新增密切接触者数量
def get_new_close_contacts_count():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute(
        "SELECT COUNT(*) FROM `users_info_old` WHERE `processed`= '0' AND (`status`='1' OR `status`='2' OR `status`='3')")
    result = int(cursor.fetchone()[0])
    conn.close()
    cursor.close()

    return result


def get_helps(mask_code=None, processed=None, limit=10, offset_epoch=0):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    query_sentences = []
    where_clauses = []
    if mask_code:
        where_clauses.append("`village_code` REGEXP '%s'" % mask_code)
    if processed:
        where_clauses.append("`processed`='%s'" % processed)
    if len(where_clauses) > 0:
        query_sentences.append(" AND ")
        if len(where_clauses) == 1:
            query_sentences.append(where_clauses[0])
        else:
            query_sentences.append(" AND ".join(where_clauses))
    query_sentences.append("ORDER BY `village_code` ASC")
    if limit:
        query_sentences.append("LIMIT {limit} OFFSET {offset}".format(limit=limit, offset=limit * offset_epoch))
    query_sentence = " ".join(query_sentences)
    sql = "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`update_time`,`processed` " \
          "FROM `help_and_report` WHERE `info_type`='0'{query_sentence}".format(query_sentence=query_sentence)
    print(sql)
    cursor.execute(sql)
    results = cursor.fetchall()
    personal_helps = []
    for result in results:
        help = {
            'id': result[0],
            'name': result[1],
            'id_number': result[2],
            'village_code': result[3],
            'gender': result[4],
            'phone_number': result[5],
            'address': result[6],
            'date': result[7].strftime("%Y-%m-%d %H:%M:%S"),
            'processed': result[8]

        }
        personal_helps.append(help)

    cursor.close()
    conn.close()
    return personal_helps


def get_reports(mask_code=None, processed=None, limit=10, offset_epoch=0):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    query_sentences = []
    where_clauses = []
    if mask_code:
        where_clauses.append("`village_code` REGEXP '%s'" % mask_code)
    if processed:
        where_clauses.append("`processed`='%s'" % processed)
    if len(where_clauses) > 0:
        query_sentences.append(" AND ")
        if len(where_clauses) == 1:
            query_sentences.append(where_clauses[0])
        else:
            query_sentences.append(" AND ".join(where_clauses))
    query_sentences.append("ORDER BY `village_code` ASC")
    if limit:
        query_sentences.append("LIMIT {limit} OFFSET {offset}".format(limit=limit, offset=limit * offset_epoch))
    query_sentence = " ".join(query_sentences)
    sql1 = "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`acused_name`," \
           "`acused_gender`,`acused_phone_number`,`acused_address`,`update_time`,`processed`,`witness_location` " \
           "FROM `help_and_report` WHERE `info_type`='1'{query_sentence}".format(query_sentence=query_sentence)
    reports = []
    print(sql1)
    cursor.execute(sql1)
    results = cursor.fetchall()
    for result in results:
        report = {
            'id': result[0],
            'name': result[1],
            'id_number': result[2],
            'village_code': result[3],
            'gender': result[4],
            'phone_number': result[5],
            'address': result[6],
            'acused_name': result[7],
            'acused_gender': result[8],
            'acused_phone_number': result[9],
            'acused_address': result[10],
            'date': result[11].strftime("%Y-%m-%d %H:%M:%S"),
            'processed': result[12],
            'witness_location': result[13]
        }
        reports.append(report)

    cursor.close()
    conn.close()
    return reports


def get_helps_and_reports(mask_code=None, processed=None, limit=10, offset_epoch=0):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    query_sentences = []
    where_clauses = []
    if mask_code:
        where_clauses.append("`village_code` REGEXP '%s'" % mask_code)
    if processed:
        where_clauses.append("`processed`='%s'" % processed)
    if len(where_clauses) > 0:
        query_sentences.append(" AND ")
        if len(where_clauses) == 1:
            query_sentences.append(where_clauses[0])
        else:
            query_sentences.append(" AND ".join(where_clauses))
    query_sentences.append("ORDER BY `village_code` ASC")
    if limit:
        query_sentences.append("LIMIT {limit} OFFSET {offset}".format(limit=limit, offset=limit * offset_epoch))
    query_sentence = " ".join(query_sentences)
    sql0 = "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`update_time`,`processed` " \
           "FROM `help_and_report` WHERE `info_type`='0'{query_sentence}".format(query_sentence=query_sentence)
    sql1 = "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`acused_name`," \
           "`acused_gender`,`acused_phone_number`,`acused_address`,`update_time`,`processed`,`witness_location` " \
           "FROM `help_and_report` WHERE `info_type`='1'{query_sentence}".format(query_sentence=query_sentence)
    print(sql0)
    cursor.execute(sql0)
    results = cursor.fetchall()
    personal_helps = []
    for result in results:
        help = {
            'id': result[0],
            'name': result[1],
            'id_number': result[2],
            'village_code': result[3],
            'gender': result[4],
            'phone_number': result[5],
            'address': result[6],
            'date': result[7].strftime("%Y-%m-%d %H:%M:%S"),
            'processed': result[8]

        }
        personal_helps.append(help)
    reports = []
    print(sql1)
    cursor.execute(sql1)
    results = cursor.fetchall()
    for result in results:
        report = {
            'id': result[0],
            'name': result[1],
            'id_number': result[2],
            'village_code': result[3],
            'gender': result[4],
            'phone_number': result[5],
            'address': result[6],
            'acused_name': result[7],
            'acused_gender': result[8],
            'acused_phone_number': result[9],
            'acused_address': result[10],
            'date': result[11].strftime("%Y-%m-%d %H:%M:%S"),
            'processed': result[12],
            'witness_location': result[13]
        }
        reports.append(report)

    cursor.close()
    conn.close()
    return personal_helps, reports


# 获取个人求助和举报信息
def get_all_helps_and_reports():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute(
        "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`info_type`,`acused_name`,"
        "`acused_gender`,`acused_phone_number`,`acused_address`,`update_time`,`processed`,`witness_location` "
        "FROM `help_and_report`")
    results = cursor.fetchall()

    personal_helps = []
    reports = []
    for result in results:
        if result[7] == '0':
            help = {
                'id': result[0],
                'name': result[1],
                'id_number': result[2],
                'village_code': result[3],
                'gender': result[4],
                'phone_number': result[5],
                'address': result[6],
                'info_type': result[7],
                'date': result[12].strftime("%Y-%m-%d %H:%M:%S"),
                'processed': result[13]

            }
            personal_helps.append(help)
        else:
            report = {
                'id': result[0],
                'name': result[1],
                'id_number': result[2],
                'village_code': result[3],
                'gender': result[4],
                'phone_number': result[5],
                'address': result[6],
                'info_type': result[7],
                'acused_name': result[8],
                'acused_gender': result[9],
                'acused_phone_number': result[10],
                'acused_address': result[11],
                'date': result[12].strftime("%Y-%m-%d %H:%M:%S"),
                'processed': result[13],
                'witness_location': result[14]
            }
            reports.append(report)

    cursor.close()
    conn.close()
    return personal_helps, reports


# 获取新增个人求助和举报信息
def get_new_helps_and_reports():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute(
        "SELECT `id`,`name`,`id_number`,`village_code`,`gender`,`phone_number`,`address`,`info_type`,`acused_name`,"
        "`acused_gender`,`acused_phone_number`,`acused_address`,`update_time`,`processed`,`witness_location` "
        "FROM `help_and_report` WHERE `processed` = '0'")
    results = cursor.fetchall()

    personal_helps = []
    reports = []
    for result in results:
        if result[6] == '0':
            help = {
                'id': result[0],
                'name': result[1],
                'id_number': result[2],
                'village_code': result[3],
                'gender': result[4],
                'phone_number': result[5],
                'address': result[6],
                'info_type': result[7],
                'date': result[12].strftime("%Y-%m-%d %H:%M:%S"),
                'processed': result[13]
            }
            personal_helps.append(help)
        else:
            report = {
                'id': result[0],
                'name': result[1],
                'id_number': result[2],
                'village_code': result[3],
                'gender': result[4],
                'phone_number': result[5],
                'address': result[6],
                'info_type': result[7],
                'acused_name': result[8],
                'acused_gender': result[9],
                'acused_phone_number': result[10],
                'acused_address': result[11],
                'date': result[12].strftime("%Y-%m-%d %H:%M:%S"),
                'processed': result[13],
                'witness_location': result[14]
            }
            reports.append(report)

    cursor.close()
    conn.close()
    return personal_helps, reports


# 【待删除】获取新增个人求助或举报信息
def get_new_helps_and_reports_count():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute("SELECT `info_type` FROM `help_and_report` WHERE `processed`= '0'")
    results = cursor.fetchall()
    helps = 0
    reports = 0
    for result in results:
        if result[0] == '0':
            helps += 1
        else:
            reports += 1
    conn.close()
    cursor.close()
    return helps, reports


# 增加个人求助或举报信息
def insert_help_and_report(item):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        if 'id' not in item:
            sql = "INSERT INTO `help_and_report`(`id_number`, `name`, `village_code`, `gender`, `phone_number`," \
                  "`address`,`info_type`,`acused_name`,`acused_gender`,`acused_phone_number`,`acused_address`," \
                  "`update_time`,`processed`,`witness_location`) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
            cursor.execute(sql,
                           [item['id_number'], item['name'], item['village_code'], item['gender'], item['phone_number'],
                            item['address'],
                            item['info_type'], item['acused_name'], item['acused_gender'], item['acused_phone_number'],
                            item['acused_address'], toolbox.get_current_time(), '0', item['witness_location']])

            if str(item['info_type']) == '0':
                sub_proc = Process(target=update_all_item_in_statistic, args=(item['village_code'], {'help': 1}))
            else:
                sub_proc = Process(target=update_all_item_in_statistic, args=(item['village_code'], {'status': 1}))
            sub_proc.start()

            conn.commit()
            return 200
        else:
            return 300
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 修改个人求助和帮助
def modify_help_and_report(item_id):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT `village_code`,`info_type` FROM `help_and_report` WHERE `id`=%s", [item_id, ])
        village_code, info_type = cursor.fetchone()

        cursor.execute("UPDATE `help_and_report` SET `update_time`=%s, `processed`='1' WHERE `id`= (%s) ",
                       [toolbox.get_current_time(), item_id])

        if str(info_type) == '0':
            sub_proc = Process(target=update_all_item_in_statistic, args=(village_code, {'help': -1}, '1'))
        else:
            sub_proc = Process(target=update_all_item_in_statistic, args=(village_code, {'report': -1}, '1'))
        sub_proc.start()

        conn.commit()
        return True
    except:
        print(traceback.print_exc())
        return False
    finally:
        conn.close()
        cursor.close()


# 查看防控等级
def get_disease_level():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM `disease_level`")
    result = cursor.fetchone()[0]

    conn.close()
    cursor.close()

    return result


# 修改疫情等级
def modify_disease_level(new_level):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()

    cursor.execute("UPDATE `disease_level` SET `disease_level`=%s", [new_level, ])
    conn.commit()

    cursor.close()
    conn.close()


# 更新总体轨迹
def update_total_track(uid, new_track):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        total_track_num = cursor.execute("SELECT `track` FROM `total_track`")
        if total_track_num == 1:
            total_track = json.loads(cursor.fetchone()[0])
            total_track[uid] = new_track
            cursor.execute("UPDATE `total_track` SET `track`=%s", [total_track, ])
            conn.commit()
            return 200
        else:
            return 400
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 从总体轨迹中删除用户的一个轨迹
def delete_from_total_track(uid):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    try:
        total_track_num = cursor.execute("SELECT `track` FROM `total_track`")
        if total_track_num == 1:
            total_track = json.loads(cursor.fetchone()[0])
            total_track.pop(uid)
            cursor.execute("UPDATE `total_track` SET `track`=%s", [total_track, ])
            conn.commit()
            return 200

        else:
            return 400
    except:
        print(traceback.print_exc())
        return 400
    finally:
        cursor.close()
        conn.close()


# 获取统计信息
def get_statistic_info(mask_code='', stype=None):
    print(mask_code)
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    if stype is not None:
        sql = "SELECT `statistic_type`,`health_count`,`confirmed_count`,`suspected_count`,`low_close_count`," \
              "`medium_close_count`,`high_close_count`,`asymptomatic_count`,`help_count`,`report_count`,`update_time` " \
              "FROM `statistic` WHERE `village_code`='{mask_code}' AND `statistic_type`='{stype}'" \
              "ORDER BY `village_code` ASC".format(mask_code=mask_code, stype=stype)
    else:
        sql = "SELECT `statistic_type`,`health_count`,`confirmed_count`,`suspected_count`,`low_close_count`," \
              "`medium_close_count`,`high_close_count`,`asymptomatic_count`,`help_count`,`report_count`,`update_time` " \
              "FROM `statistic` WHERE `village_code`='{mask_code}' " \
              "ORDER BY `village_code` ASC, `statistic_type` ASC".format(mask_code=mask_code)
    cursor.execute(sql)
    results = cursor.fetchall()
    cursor.close()
    conn.close()
    if stype == '0':
        total_counts = {
            '0': int(results[0][1]),
            '1': int(results[0][4]),
            '2': int(results[0][5]),
            '3': int(results[0][6]),
            '4': int(results[0][3]),
            '5': int(results[0][2]),
            '6': int(results[0][7]),
            'total': sum(results[0][1:8]),
            'help_count': int(results[0][8]),
            'report_count': int(results[0][9]),
            'update_time': results[0][10].strftime("%Y-%m-%d %H:%M:%S")
        }
        return total_counts
    elif stype == '1':
        new_counts = {
            '0': int(results[0][1]),
            '1': int(results[0][4]),
            '2': int(results[0][5]),
            '3': int(results[0][6]),
            '4': int(results[0][3]),
            '5': int(results[0][2]),
            '6': int(results[0][7]),
            'total': sum(results[0][1:8]),
            'help_count': int(results[0][8]),
            'report_count': int(results[0][9]),
            'update_time': results[0][10].strftime("%Y-%m-%d %H:%M:%S")
        }
        return new_counts
    else:
        total_counts = {
            '0': int(results[0][1]),
            '1': int(results[0][4]),
            '2': int(results[0][5]),
            '3': int(results[0][6]),
            '4': int(results[0][3]),
            '5': int(results[0][2]),
            '6': int(results[0][7]),
            'total': sum(results[0][1:8]),
            'help_count': int(results[0][8]),
            'report_count': int(results[0][9]),
            'update_time': results[0][10].strftime("%Y-%m-%d %H:%M:%S")
        }
        new_counts = {
            '0': int(results[1][1]),
            '1': int(results[1][4]),
            '2': int(results[1][5]),
            '3': int(results[1][6]),
            '4': int(results[1][3]),
            '5': int(results[1][2]),
            '6': int(results[1][7]),
            'total': sum(results[0][1:8]),
            'help_count': int(results[0][8]),
            'report_count': int(results[0][9]),
            'update_time': results[0][10].strftime("%Y-%m-%d %H:%M:%S")
        }
        return total_counts, new_counts


def insert_statistic_info(code, data, total_or_new):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    if len(code) != 12:
        print("行政编码不合规范！")
        return 400
    if total_or_new is not None:
        if modify_statistic_single(cursor=cursor, code=code, stype=total_or_new, data=data) == 200:
            if modify_statistic_single(cursor=cursor, code=code[0:9], stype=total_or_new, data=data) == 200:
                if modify_statistic_single(cursor=cursor, code=code[0:6], stype=total_or_new, data=data) == 200:
                    if modify_statistic_single(cursor=cursor, code=code[0:4], stype=total_or_new, data=data) == 200:
                        if modify_statistic_single(cursor=cursor, code=code[0:2], stype=total_or_new, data=data) == 200:
                            if modify_statistic_single(cursor=cursor, code='', stype=total_or_new, data=data) == 200:
                                conn.commit()
                                cursor.close()
                                conn.close()
                                return 200
    else:
        if modify_statistic_dual(cursor=cursor, code=code, data=data) == 200:
            if modify_statistic_dual(cursor=cursor, code=code[0:9], data=data) == 200:
                if modify_statistic_dual(cursor=cursor, code=code[0:6], data=data) == 200:
                    if modify_statistic_dual(cursor=cursor, code=code[0:4], data=data) == 200:
                        if modify_statistic_dual(cursor=cursor, code=code[0:2], data=data) == 200:
                            if modify_statistic_dual(cursor=cursor, code='', data=data) == 200:
                                conn.commit()
                                cursor.close()
                                conn.close()
                                return 200
    print("插入数据库失败")
    conn.rollback()
    cursor.close()
    conn.close()
    return 400


def update_all_item_in_statistic(code, values, only_total=None):
    data = {'health_count': 0,
            'asymptomatic_count': 0,
            'confirmed_count': 0,
            'suspected_count': 0,
            'low_close_count': 0,
            'medium_close_count': 0,
            'high_close_count': 0,
            'help_count': 0,
            'report_count': 0
            }
    if not values:
        print("【Error】 modify_one_into_statistic:错误的添加指令！")
        return 400
    if '0' in values.keys():
        data['health_count'] = values['0']
    if '6' in values.keys():
        data['asymptomatic_count'] = values['6']
    if '5' in values.keys():
        data['confirmed_count'] = values['5']
    if '4' in values.keys():
        data['suspected_count'] = values['4']
    if '1' in values.keys():
        data['low_close_count'] = values['1']
    if '2' in values.keys():
        data['medium_close_count'] = values['2']
    if '3' in values.keys():
        data['high_close_count'] = values['3']
    if 'help_count' in values.keys():
        data['help_count'] = values['help_count']
    if 'report_count' in values.keys():
        data['report_count'] = values['report_count']

    return insert_statistic_info(code, data, only_total)


def clear_statistic(onlyNew=False):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    # sql_update = "UPDATE `statistic` SET `health_count`='0',`confirmed_count`='0',`suspected_count`='0'," \
    #              "`low_close_count`='0',`medium_close_count`='0',`high_close_count`='0',`asymptomatic_count`='0'" \
    #              "`help_count`='0',`report_count`='0',`update_time`=str_to_date(%s,'%%Y-%%m-%%d %%H:%%i:%%s')"
    sql_update = "DELETE FROM `statistic`"
    if onlyNew:
        sql_update += " WHERE `statistic_type`='0'"
    try:
        # cursor.execute(sql_update, [toolbox.get_current_time()])
        cursor.execute(sql_update)
        conn.commit()
        return 200
    except:
        print(traceback.print_exc())
        conn.rollback()
        return 400
    finally:
        cursor.close()
        conn.close()


def modify_statistic_dual(cursor, code, data):
    code1 = modify_statistic_single(cursor, code, 0, data)
    code2 = modify_statistic_single(cursor, code, 1, data)
    if code1 == code2 == 200:
        return 200
    else:
        return 400


def modify_statistic_single(cursor, code, stype, data):
    sql_select = "SELECT `index`,`health_count`,`confirmed_count`,`suspected_count`," \
                 "`low_close_count`,`medium_close_count`,`high_close_count`,`asymptomatic_count`," \
                 "`help_count`,`report_count` FROM `statistic` WHERE `village_code`=%s AND `statistic_type`=%s"

    sql_insert = "INSERT INTO `statistic`(`village_code`,`statistic_type`,`health_count`,`confirmed_count`," \
                 "`suspected_count`,`low_close_count`,`medium_close_count`,`high_close_count`,`asymptomatic_count`," \
                 "help_count,report_count,update_time) " \
                 "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,str_to_date(%s,'%%Y-%%m-%%d %%H:%%i:%%s'))"

    sql_update = "UPDATE `statistic` SET `health_count`='%s',`confirmed_count`='%s',`suspected_count`='%s'," \
                 "`low_close_count`='%s',`medium_close_count`='%s',`high_close_count`='%s',`asymptomatic_count`='%s'," \
                 "`help_count`='%s',`report_count`='%s',`update_time`=str_to_date(%s,'%%Y-%%m-%%d %%H:%%i:%%s')" \
                 " WHERE `index`='%s'"
    # sql_update = "UPDATE `statistic` SET `health_count`=%d,`confirmed_count`=%d,`suspected_count`=%d," \
    #              "`low_close_count`=%d,`medium_close_count`=%d,`high_close_count`=%d," \
    #              "`help_count`=%d,`report_count`=%d,`update_time`=(%s)" \
    #              " WHERE `index`=%s"
    try:
        cursor.execute(sql_select, [code, stype])
        result = cursor.fetchone()
        if not result:
            cursor.execute(sql_insert,
                           [code, stype,
                            data['health_count'], data['confirmed_count'], data['suspected_count'],
                            data['low_close_count'], data['medium_close_count'],
                            data['high_close_count'], data['asymptomatic_count'],
                            data['help_count'], data['report_count'], toolbox.get_current_time()])
        else:
            cursor.execute(sql_update,
                           [int(result[1]) + int(data['health_count']),
                            int(result[2]) + int(data['confirmed_count']),
                            int(result[3]) + int(data['suspected_count']),
                            int(result[4]) + int(data['low_close_count']),
                            int(result[5]) + int(data['medium_close_count']),
                            int(result[6]) + int(data['high_close_count']),
                            int(result[7]) + int(data['asymptomatic_count']),
                            int(result[8]) + int(data['help_count']),
                            int(result[9]) + int(data['report_count']),
                            toolbox.get_current_time(),
                            int(result[0])])
        print(code, "insert successfully")
        return 200
    except:
        print(traceback.print_exc())
        return 400


def get_administrative_name(code):
    print("code", code)
    if code == '':
        return "全国"
    else:
        conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                               charset="utf8mb4")
        cursor = conn.cursor()
        if len(code) == 2:
            sql = "SELECT `province_name` FROM `administrative_division` WHERE `province_code`=%s" % code
        elif len(code) == 4:
            sql = "SELECT `province_name`,`city_name` FROM `administrative_division` WHERE `city_code` =%s" % code
        elif len(code) == 6:
            sql = "SELECT `province_name`,`city_name`,`district_name` FROM `administrative_division` WHERE `district_code` =%s" % code
        elif len(code) == 9:
            sql = "SELECT `province_name`,`city_name`,`district_name`,`street_name` FROM `administrative_division` WHERE `street_code` =%s" % code
        elif len(code) == 12:
            sql = "SELECT `province_name`,`city_name`,`district_name`,`street_name`,`village_name` FROM `administrative_division` WHERE `village_code` =%s" % code
        else:
            conn.close()
            cursor.close()
            print("行政编码错误")
            return None
        cursor.execute(sql)
        result = cursor.fetchone()
        if result:
            # name = ''.join(result)
            cursor.close()
            conn.close()
            return result
        else:
            conn.close()
            cursor.close()
            print("行政编码错误")
            return None


def get_administrative_name_by_mask(mask_code, code):
    if mask_code == '':
        return get_administrative_name(code)
    else:
        conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                               charset="utf8mb4")
        cursor = conn.cursor()
        if len(mask_code) == 9:
            sql = "SELECT `village_name` FROM `administrative_division` WHERE `village_code`=%s" % code
        elif len(mask_code) == 6:
            sql = "SELECT `street_name`,`village_name` FROM `administrative_division` WHERE `village_code` =%s" % code
        elif len(mask_code) == 4:
            sql = "SELECT `district_name`,`city_name`,`district_name` FROM `administrative_division` WHERE `village_code` =%s" % code
        elif len(mask_code) == 2:
            sql = "SELECT `city_name`,`district_name`,`street_name`,`village_name` FROM `administrative_division` WHERE `village_code` =%s" % code
        else:
            conn.close()
            cursor.close()
            print("行政编码错误")
            return None
        cursor.execute(sql)
        result = cursor.fetchone()
        if result:
            # name = ''.join(result)
            cursor.close()
            conn.close()
            return result
        else:
            conn.close()
            cursor.close()
            print("行政编码错误")
            return None


def get_all_administrative_list():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    administrative_list = {}
    try:
        sql = "SELECT * FROM `administrative_division`"
        cursor.execute(sql)
        results = cursor.fetchall()
        for result in results:
            province_code = result[0]
            province_name = result[1]
            city_code = result[2]
            city_name = result[3]
            district_code = result[4]
            district_name = result[5]
            street_code = result[6]
            street_name = result[7]
            village_code = result[8]
            village_name = result[9]
            if province_code not in administrative_list.keys():
                administrative_list[province_code] = {
                    'code': province_code,
                    'name': province_name,
                    'children': {}}
            if city_code not in administrative_list[province_code]['children'].keys():
                administrative_list[province_code]['children'][city_code] = {
                    'code': city_code,
                    'name': city_name,
                    'children': {}}
            if district_code not in administrative_list[province_code]['children'][city_code]['children'].keys():
                administrative_list[province_code]['children'][city_code]['children'][district_code] = {
                    'code': district_code,
                    'name': district_name,
                    'children': {}}
            if street_code not in administrative_list[province_code]['children'][city_code]['children'][district_code][
                'children'].keys():
                administrative_list[province_code]['children'][city_code]['children'][district_code]['children'][
                    street_code] = {
                    'code': street_code,
                    'name': street_name,
                    'children': {}}
            administrative_list[province_code]['children'][city_code]['children'][district_code]['children'][
                street_code]['children'][village_code] = village_name
        return administrative_list
    except:
        print(traceback.print_exc())
        return None
    finally:
        cursor.close()
        conn.close()


def get_administrative_list(administrative_code=None):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    administrative_list = {}
    try:
        if not administrative_code:
            sql = "SELECT `province_code`,`province_name` FROM `administrative_division`"
        elif len(administrative_code) == 2:
            sql = "SELECT `city_code`,`city_name` FROM `administrative_division` " \
                  "WHERE `province_code`=%s" % administrative_code
        elif len(administrative_code) == 4:
            sql = "SELECT `district_code`,`district_name` FROM `administrative_division` " \
                  "WHERE `city_code`=%s" % administrative_code
        elif len(administrative_code) == 6:
            sql = "SELECT `street_code`,`street_name` FROM `administrative_division` " \
                  "WHERE `district_code`=%s" % administrative_code
        elif len(administrative_code) == 9:
            sql = "SELECT `village_code`,`village_name` FROM `administrative_division` " \
                  "WHERE `street_code`=%s" % administrative_code
        elif len(administrative_code) == 12:
            sql = "SELECT `village_name` FROM `administrative_division` " \
                  "WHERE  `village_code`=%s" % administrative_code
            cursor.execute(sql)
            return cursor.fetchone(cursor.fetchone()[0])
        else:
            print("行政编码错误", administrative_code)
            return 400

        cursor.execute(sql)
        results = cursor.fetchall()
        items = {}
        if results:
            for result in results:
                code = result[0].strip()
                name = result[1].strip()
                items[code] = name
            return items
        else:
            print("行政编码错误")
            return None
    except:
        print(traceback.print_exc())
        return None
    finally:
        cursor.close()
        conn.close()


def get_administrative_tree(administrative_code=None):
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    administrative_tree = []
    try:
        if len(administrative_code) == 2:
            sql = "SELECT `city_code`,`city_name`,`district_code`,`district_name` FROM `administrative_division` " \
                  "WHERE `province_code`=%s" % administrative_code
        else:
            sql = "SELECT `street_code`,`street_name`,`village_code`,`village_name` FROM `administrative_division` " \
                  "WHERE `district_code`=%s" % administrative_code
        cursor.execute(sql)
        results = cursor.fetchall()
        level1_codes = {}
        level2_codes = {}
        level1_index = 0
        last_l2_code = ""
        for result in results:
            level1_code = result[0]
            level1_name = result[1]
            level2_code = result[2]
            level2_name = result[3]
            if level2_code == last_l2_code:
                continue
            else:
                last_l2_code = level2_code
                if level1_code not in level1_codes.keys():
                    level1_codes[level1_code] = level1_index
                    level1_item = {
                        "name": level1_name,
                        "code": level1_code,
                        "children": [{
                            "name": level2_name,
                            "code": level2_code
                        }, ]
                    }
                    administrative_tree.append(level1_item)
                    level1_index += 1
                else:
                    administrative_tree[level1_codes[level1_code]]['children'].append({
                        "name": level2_name,
                        "code": level2_code
                    })
        return administrative_tree
    except:
        print(traceback.print_exc())
        return None
    finally:
        cursor.close()
        conn.close()


def is_administrative_code_existed(code):
    if code == '':
        return True
    if len(code) == 2:
        sql = "SELECT `province_name` FROM `administrative_division` WHERE `province_code`=%s" % code
    elif len(code) == 4:
        sql = "SELECT `city_name` FROM `administrative_division` WHERE `city_code`=%s" % code
    elif len(code) == 6:
        sql = "SELECT `district_name` FROM `administrative_division` WHERE `district_code`=%s" % code
    elif len(code) == 9:
        sql = "SELECT `street_name` FROM `administrative_division` WHERE `street_code`=%s" % code
    elif len(code) == 12:
        sql = "SELECT `village_name` FROM `administrative_division` WHERE `village_code`=%s" % code
    else:
        return False
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute(sql)
    result = cursor.fetchone()
    cursor.close()
    conn.close()
    if result:
        return True
    else:
        return False


def get_provinces():
    conn = pymysql.connect(host=database_host, user=database_user, passwd=database_passwd, db=database_name,
                           charset="utf8mb4")
    cursor = conn.cursor()
    cursor.execute("SELECT `provinces` FROM `administrative_provinces`")
    result = cursor.fetchone()[0]
    # json_ = json.loads(str(result))
    conn.close()
    cursor.close()
    return json.loads(result)


if __name__ == '__main__':
    print(update_user_status_track('340403199505051717', status=6))
