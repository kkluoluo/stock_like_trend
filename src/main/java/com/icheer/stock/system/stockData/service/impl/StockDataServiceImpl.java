package com.icheer.stock.system.stockData.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.system.stockData.entity.StockData;
import com.icheer.stock.system.stockData.mapper.StockDataMapper;
import com.icheer.stock.system.stockData.service.StockDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StockDataServiceImpl extends ServiceImpl<StockDataMapper, StockData> implements StockDataService {
    private static final Logger log = LoggerFactory.getLogger(com.icheer.stock.system.stockData.service.impl.StockDataServiceImpl.class);

//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private RedisTemplate<Object,Object> redisTemplate;
//
//    public User findByWxOpenid(String wxOpenid)
//    {
//        ExcludeEmptyQueryWrapper<User> queryWrapper = new ExcludeEmptyQueryWrapper<>();
//        queryWrapper.eq("wx_openid",wxOpenid);
//        return userMapper.selectOne(queryWrapper);
//    }



}

