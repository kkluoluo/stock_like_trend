package com.icheer.stock.controller.test.testResult.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.controller.test.testResult.entity.Test;
import com.icheer.stock.controller.test.testResult.mapper.TestMapper;
import com.icheer.stock.controller.test.testResult.service.TestService;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {
}
