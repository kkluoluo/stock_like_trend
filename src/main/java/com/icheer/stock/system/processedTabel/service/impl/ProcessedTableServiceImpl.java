package com.icheer.stock.system.processedTabel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;
import com.icheer.stock.system.processedTabel.mapper.ProcessedTableMapper;
import com.icheer.stock.system.processedTabel.service.ProcessedTableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class ProcessedTableServiceImpl extends ServiceImpl<ProcessedTableMapper, ProcessedTable> implements ProcessedTableService {

    @Resource
    private ProcessedTableMapper processedTableMapper;

    @Override
    public ArrayList<ProcessedTable> list(String table_name) {
        return processedTableMapper.list(table_name);
    }

    @Override
    public ArrayList<String> listHS300() {
        return processedTableMapper.listHS300();
    }

    @Override
    public ArrayList<ProcessedTable> listDescById(String table_name) {
        return processedTableMapper.listDescById(table_name);
    }
}
