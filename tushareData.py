# 调用tushare接口读取HS300日线历史数据
# 处理数据，并将原始数据和处理后数据分别存入DB
import pandas as pd
import tushare as ts
import numpy as np
import pymysql
import time
import datetime
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
from sqlalchemy import create_engine

pymysql.install_as_MySQLdb()
# 本地数据库
engine_ts = create_engine('mysql://root:root@127.0.0.1:3306/db_tushare300?charset=utf8&use_unicode=1')
# 腾讯云数据库
# engine_ts = create_engine('mysql://stockAPP:$M&b%%8WUi4o@1.15.223.105:3306/stock_scan?charset=utf8&use_unicode=1')

# 个人账号tushareToken
ts.set_token('dce74e113985d5da2a8dffe05cccdba7c72aa1e0c34601dbb8d2cbc0')


# 写入数据到DB
def write_data(table_data, table_name):
    table_data.to_sql(table_name, engine_ts, index=True, if_exists='replace')
    print(table_name)
    with engine_ts.connect() as con:
        con.execute("""ALTER TABLE `{}`.`{}` \
                ADD PRIMARY KEY (`index`);"""
                    .format('db_tushare300', table_name))


# 获取初始所有ts接口数据(并且更新)
def get_data(code):
    ts_data1 = pd.DataFrame(ts.pro_bar(ts_code=code, adj='None', start_date='19900101', end_date='20020108', ma=[5]))
    if not ts_data1.empty:
        ts_data1 = ts_data1.sort_values(by='trade_date', ascending=True)
    ts_data2 = pd.DataFrame(ts.pro_bar(ts_code=code, adj='None', start_date='20020109', ma=[5]))\
        .sort_values(by='trade_date', ascending=True)
    ts_data = pd.concat([ts_data1, ts_data2]).reset_index(drop=True)
    return ts_data


# (暂时不用)更新当天ts接口数据
# def update_data(code):
#
#     # YYYYMMDD格式的当天日期
#     today = datetime.date.today().strftime('%Y%m%d')
#     # 由于需要计算均线, 需要当天以及前四天的数据
#     # calculate_date = int(today)-4
#     calculate_date = int(today) - 100
#     ts_data = pd.DataFrame(ts.pro_bar(ts_code=code, adj='None', start_date=str(calculate_date), ma=[5])) \
#         .sort_values(by='trade_date', ascending=True).reset_index(drop=True)
#     return ts_data


# 获取HS300股票代码列表
def get_list():
    df_cons = pd.read_excel(
        'https://csi-web-dev.oss-cn-shanghai-finance-1-pub.aliyuncs.com/static/html/csindex/public/uploads/file'
        '/autofile/cons/000300cons.xls').get(
        ["成分券代码Constituent Code", "交易所Exchange"])
    df_cons["成分券代码Constituent Code"] = df_cons["成分券代码Constituent Code"].astype(object)
    for m in range(300):
        if df_cons.loc[m][1] == '上海证券交易所':
            df_cons.loc[m][0] = str(df_cons.loc[m][0]).zfill(6) + '.sh'
        else:
            df_cons.loc[m][0] = str(df_cons.loc[m][0]).zfill(6) + '.sz'
    df_cons = df_cons.drop(columns="交易所Exchange")
    return df_cons


# 处理ma5数据，得到所有极值点以及极值点线段的斜率
def process_data(ma5_data):
    ini_data = pd.DataFrame({'iniPoint': [], 'curPoint': [], 'ma5Delta': [], 'ma5Slope': [], 'ma5Trend': []})
    sign_res = np.sign(ma5_data[5] - ma5_data[4])

    ini_point = 4
    for j in range(5, len(ma5_data) - 1):
        if sign_res != np.sign(ma5_data[j + 1] - ma5_data[j]):
            ma5_delta = ma5_data[j] - ma5_data[ini_point]
            ini_data.loc[ini_data.shape[0]] = [ini_point, j, ma5_delta, ma5_delta / (j - ini_point), sign_res]

            ini_point = j
            sign_res = np.sign(ma5_data[j + 1] - ma5_data[j])
    return ini_data


# 获取数据
def work():
    print('update_start, {}'.format(time.ctime()))

    # 获取HS300代码列表
    hs300_list = get_list()
    for i in range(300):
        # 调用tushare接口获取HS300历史日线行情
        df = get_data(hs300_list['成分券代码Constituent Code'][i])
        stock_code = str(hs300_list['成分券代码Constituent Code'][i]).replace('.', '_')
        # 写入数据到DB
        write_data(df, stock_code)
        # 处理ma5数据
        write_data(process_data(df.get('ma5')), stock_code + '_processed')

    print('update_end, {}'.format(time.ctime()))


# 每天定时获取数据
def scheduler_work():
    scheduler = BackgroundScheduler(timezone='Asia/Shanghai')
    interval_trigger = CronTrigger(hour=17, minute=15)

    scheduler.add_job(work(), interval_trigger, id='hs300_data')
    scheduler.start()


if __name__ == "__main__":
    work()