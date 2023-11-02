package com.zoo.xxx.bean

/**
 * 版本信息
 */
class VersionInfo(
    var createBy: String = "",//": "string",
    var createTime: String = "",//: "2023-08-02T14:21:30.568Z",
    var id: Int = 0,//: 0,
    var packageName: String = "",//: "string",
    var packagePath: String = "",//: "string",
    var packageStatus: Int = 0,//是否应用（0是1否）
    var packageType: Int = 0,//": 0,包类型（1pda,2叉车平板）
    var packageTypeName: String? = null,//: "string",
    var packageVersion: String? = null,//: "string",
    var remark: String? = null,//: "string",
    var updateBy: String = "",//: "string",
    var updateTime: String = ""//: "2023-08-02T14:21:30.568Z"
)
