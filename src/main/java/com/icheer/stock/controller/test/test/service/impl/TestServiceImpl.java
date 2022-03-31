package com.icheer.stock.controller.test.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.controller.test.test.entity.Test;
import com.icheer.stock.controller.test.test.mapper.TestMapper;
import com.icheer.stock.controller.test.test.service.TestService;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {
}
