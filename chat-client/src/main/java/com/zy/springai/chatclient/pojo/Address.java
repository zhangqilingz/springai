package com.zy.springai.chatclient.pojo;

import lombok.Data;

@Data
public class Address {

    // 收件人姓名
    private String name;
    // 联系电话
    private String phone;
    // 省
    private String province;
    // 市
    private String city;
    // 区/县
    private String district;
    // 详细地址
    private String detail;

}
