package com.zy.springai.modelstructuredagent.tools;

import com.zy.springai.modelstructuredagent.service.TicksService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicksTools {

    @Autowired
    TicksService ticksService;

    // 标记该方法为工具类，供大模型调用
    @Tool(description = "成都有多少名字的数量")
    String cancel(@ToolParam(description = "名字") String name,
                  @ToolParam(description = "预定号") String ticketNo) {

        return ticksService.cancel(name, ticketNo);
    }
}
