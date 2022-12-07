package com.compete.Type

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
)

data class RegisterRequest(
    val avatar: String, // /profile/2020/10/26/27e7fd58-0972-4dbf-941c-590624e6a886.png
    val email: String, // David@163.com
    val idCard: String, // 210113199808242137
    val nickName: String, // 大卫
    val password: String, // 123456
    val phonenumber: String, // 15840669812
    val sex: Int, // 0
    val userName: String // David
)

data class ModifyRequest(
    val email: String, // lixl@163.com
    val idCard: String, // 210882199807251656
    val nickName: String, // 大卫王
    val phonenumber: String, // 15898125461
    val sex: Int // 0
)

data class PasswordModifyRequest(
    val newPassword: String,
    val oldPassword: String
)

data class FeedbackUploadRequest(
    @SerializedName("content")
    val content: String, // 反馈内容
    @SerializedName("title")
    val title: String // 发现错误
)

data class CommentPublishRequest(
    @SerializedName("content")
    val content: String, // 新闻评论测试
    @SerializedName("newsId")
    val newsId: Int // 5
)