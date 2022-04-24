# stockServer
## 股票走势曲线分析小程序-后端服务

> 小程序登陆接口
```
GET http://localhost:8099/api/loginByMini
```

> 获取大盘指数接口
```
GET http://localhost:8099/api/composite_index

return List<日线行情>[上证，深证，创业板]

```


> 个股搜索接口

```
GET http://localhost:8099/api/search_stock
Content-Type: application/json

{
  "code": "0000",
  "name": "",
  "pageSize": 10,
  "pageNum": "1"

}

```

> 相似度分析接口,"KL"--趋势线分段法  "PK"--皮尔逊相关系数法
```
GET http://localhost:8099/api/stock_analysis
Content-Type: application/json

{
  "code": "000001",
  "range": 120,
  "preRange": 5,
  "model":"KL"
}
```