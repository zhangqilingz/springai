package com.zy.springai.modelstructuredagent.service.impl;

import com.zy.springai.modelstructuredagent.service.TicksService;
import org.springframework.stereotype.Service;

@Service
public class TicksServiceImpl implements TicksService {

    @Override
    public String cancel(String ticksNo, String name) {
        return "退票成功！！";
    }
}
