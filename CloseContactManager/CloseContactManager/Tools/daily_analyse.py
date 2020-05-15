import schedule
from CloseContactManager.Tools import database_operater as dbop


def analysis_per_day():
    dbop.clear_statistic()
    users = dbop.get_users(limit=20)
    for user in users:
        print(user)
        data = {
            str(user['status_code']): 1
        }
        print(data)
        if user['processed'] == '1':
            dbop.update_all_item_in_statistic(user['village_code'], data, '0')
            data[str(user['status_code'])] = 0
            dbop.update_all_item_in_statistic(user['village_code'], data, '1')
        else:
            dbop.update_all_item_in_statistic(user['village_code'], data)
    helps, reports = dbop.get_helps_and_reports()
    for help in helps:
        print(help)
        data = {
            "help_count": 1
        }
        dbop.update_all_item_in_statistic(help['village_code'], data)
    for report in reports:
        print(report)
        data = {
            "report_count": 1
        }
        dbop.update_all_item_in_statistic(report['village_code'], data)


if __name__ == '__main__':
    schedule.every().day.at('05:00').do(analysis_per_day)

    while True:
        schedule.run_pending()
