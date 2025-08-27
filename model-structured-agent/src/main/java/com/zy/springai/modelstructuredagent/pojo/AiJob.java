package com.zy.springai.modelstructuredagent.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors
public class AiJob {

    public record Job(JobType jobType, Map<String, String> keyInfos) {
    }

    @Getter
    @AllArgsConstructor
    public enum JobType {
        CANCEL("CANCEL", "取消"),
        QUERY("QUERY", "查询票务信息"),
        OTHER("OTHER", "其他");

        private String jobType;
        private String desc;

    }
}
