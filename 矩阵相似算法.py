import pandas as pd
import tushare as ts
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
import pymysql
import time
import datetime
from dateutil.relativedelta import relativedelta
import akshare as ak
# 打开数据库连接
db = pymysql.connect(host='1.15.223.105',user='stockAPP',password='$M&b%%8WUi4o',database='stock_scan')

#db.close()
#date_now = datetime.datetime.now()
# 格式化成2016-03-20 11:45:39形式

#ts_data1 = ts_data1.sort_values(by='trade_date', ascending=True)

def main( code , day_range ):

    stock_compare=ts.get_hist_data(code)[0:day_range] #比较对象两个日期之间的前复权数据
    date_last=stock_compare.index[0]   #最新交易日

    compare_open=stock_compare['open']
    compare_close=stock_compare['close']
    compare_high=stock_compare['high']
    compare_low=stock_compare['low']

    stock_history=ts.get_hist_data("000002") #比较对象两个日期之间的前复权数据
    trade_days=len(stock_history)#606个交易日？？？
    print(trade_days)
    k_list=[]
    x_list=[]
    for i in range(0,trade_days, 10):

        stock_offer=stock_history[i:i+day_range]
        if(i+day_range>=trade_days):
            break
        open_o     =stock_offer['open']
        close_o    =stock_offer['close']
        high_o     =stock_offer['high']
        low_o      =stock_offer['low']

        open_k =np.corrcoef(compare_open,open_o)[0][1]
        close_k=np.corrcoef(compare_close,close_o)[0][1]
        high_k =np.corrcoef(compare_high,high_o)[0][1]
        low_k =np.corrcoef(compare_low,low_o)[0][1]
        k001  = (open_k+close_k+high_k+low_k)/4
        k_list.append(k001)
        x_list.append(i)
    plt.figure(1)
    x=x_list
    ys=k_list
    print(max(k_list))
    # 虚线  线风格(linestyle='dashed'，'-'，'--'，'-.'，':')
    plt.plot(x, ys, marker='o', color='blue', linestyle='--')



    plt.title(code+'与平安银行历史走势比较，K值')

    plt.show()

if __name__ == '__main__':
    # print('java 调用有第三方库的python脚本成功')
    main('600926',30)