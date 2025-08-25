package com.zy.springai.chatclient.pojo;


import lombok.Data;

@Data
public class ModelOptions {

    private String platform;
    private String model;
    private Double temperature;
    // 用户标识，用于隔离记忆
    private String userId;
}
