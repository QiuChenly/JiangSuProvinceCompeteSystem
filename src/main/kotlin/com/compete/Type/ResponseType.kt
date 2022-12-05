package com.compete.Type

import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse(
    var code: Int? = null,
    var msg: String? = null
)

@Serializable
class ResponseImage : BaseResponse() {
    val image: String = ""
}

@Serializable
class LoginResponse(
    var token: String? = null
) : BaseResponse()

@Serializable
class QueryUserInfoResponse(
    val user: User
) : BaseResponse() {
    @Serializable
    data class User(
        val avatar: String,
        val balance: Int, // 9800
        val email: String, // ljxl@qq.com
        val idCard: String, // 210211199909090014
        val nickName: String, // 测试用户 01
        val phonenumber: String, // 13800000000
        val score: Int, // 10000
        val sex: Int, // 0
        val userId: Int, // 2
        val userName: String // test01
    )
}

@Serializable
data class BalanceListResponse(
    val code: Int, // 200
    val msg: String, // 查询成功
    val rows: List<Row>,
    val total: Int // 10
) {
    @Serializable
    data class Row(
        val appType: String, // null
        val changeAmount: Int, // 200
        val changeTime: String, // 2021-04-21 10:30:45
        val changeType: String, // 支出
        val event: String, // 缴纳违章罚款
        val id: Int, // 2
        val userId: Int, // 1
        val userName: String // null
    )
}

@Serializable
data class GetFeedbackResponse(
    val code: String, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    @Serializable
    data class Data(
        val appType: String, // movie
        val content: String, // 反馈内容
        val id: Int, // 4
        val title: String, // 发现错误
        val userId: Int // 1
    )
}