package com.compete.Type

import kotlinx.serialization.SerialName
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
data class GetFeedbackListResponse(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("msg")
    val msg: String, // 查询成功
    @SerialName("rows")
    val rows: List<Row>,
    @SerialName("total")
    val total: Int // 1
) {
    @Serializable
    data class Row(
        @SerialName("appType")
        val appType: String, // movie
        @SerialName("content")
        val content: String, // 反馈内容
        @SerialName("id")
        val id: Int, // 4
        @SerialName("title")
        val title: String, // 发现错误
        @SerialName("userId")
        val userId: Int // 1
    )
}

@Serializable
data class GetFeedbackDetailsResponse(
    @SerialName("code")
    val code: Int, //  200
    @SerialName("data")
    val data: Data?,
    @SerialName("msg")
    val msg: String // 操作成功
) {
    @Serializable
    data class Data(
        @SerialName("appType")
        val appType: String, // movie
        @SerialName("content")
        val content: String, // 反馈内容
        @SerialName("id")
        val id: Int, // 4
        @SerialName("title")
        val title: String, // 发现错误
        @SerialName("userId")
        val userId: Int // 1
    )
}

@Serializable
data class GetNewsCategoryResponse(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("msg")
    val msg: String, // 查询成功
    @SerialName("rows")
    val rows: List<Row>,
    @SerialName("total")
    val total: Int // 1
) {
    @Serializable
    data class Row(
        @SerialName("appType")
        val appType: String, // smart_city
        @SerialName("id")
        val id: Int, // 9
        @SerialName("name")
        val name: String, // 今日要闻
        @SerialName("sort")
        val sort: Int // 1
    )
}

@Serializable
data class GetNewsListResponse(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("msg")
    val msg: String, // 查询成功
    @SerialName("rows")
    val rows: List<Row>,
    @SerialName("total")
    val total: Long // 1
) {
    @Serializable
    data class Row(
        @SerialName("commentNum")
        val commentNum: Int, // 1
        @SerialName("content")
        val content: String, // <p>内容<img src="/dev-api/profile/upload/image/202 1/04/07/a9434ccf-5acf-4bf5-a06e-c3457c6762e9.png"></p>
        @SerialName("cover")
        val cover: String, // /dev-api/profile/upload/image/2021/04/01/c1eb74b2-e96 4-4388-830a-1b606fc9699f.png
        @SerialName("hot")
        val hot: String, // N
        @SerialName("id")
        val id: Int, // 5
        @SerialName("likeNum")
        val likeNum: Int, // 2
        @SerialName("publishDate")
        val publishDate: String, // 2021-04-01
        @SerialName("readNum")
        val readNum: Int, // 10
        @SerialName("status")
        val status: String, // Y
        @SerialName("subTitle")
        val subTitle: String, // 测试新闻子标题
        @SerialName("tags")
        val tags: String = "", // null
        @SerialName("title")
        val title: String, // 测试新闻标题
        @SerialName("top")
        val top: String, // Y
        @SerialName("type")
        val type: Int // 2
    )
}

@Serializable
data class GetNewsDetailResponse(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("data")
    val data: Data?,
    @SerialName("msg")
    val msg: String // 操作成功
) {
    @Serializable
    data class Data(
        @SerialName("appType")
        val appType: String, // movie
        @SerialName("commentNum")
        val commentNum: Int, // null
        @SerialName("content")
        val content: String, // <p>企鹅王请问<img src="/dev-api/profile/upload/image /2021/04/07/a9434ccf-5acf-4bf5-a06e-c3457c6762e9.png"></p>
        @SerialName("cover")
        val cover: String, // /dev-api/profile/upload/image/2021/04/01/c1eb74b2-e964- 4388-830a-1b606fc9699f.png
        @SerialName("hot")
        val hot: String, // N
        @SerialName("id")
        val id: Int, // 5
        @SerialName("likeNum")
        val likeNum: Int, // 3
        @SerialName("publishDate")
        val publishDate: String, // 2021-04-01
        @SerialName("readNum")
        val readNum: Int, // null
        @SerialName("status")
        val status: String, // Y
        @SerialName("subTitle")
        val subTitle: String, // 123123123
        @SerialName("tags")
        val tags: String = "", // null
        @SerialName("title")
        val title: String, // 驱蚊器无去
        @SerialName("top")
        val top: String, // Y
        @SerialName("type")
        val type: Int // 2
    )
}

@Serializable
data class GetNewsCommentListResponse(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("msg")
    val msg: String, // 查询成功
    @SerialName("rows")
    val rows: List<Row>,
    @SerialName("total")
    val total: Int // 1
) {
    @Serializable
    data class Row(
        @SerialName("appType")
        val appType: String, // smart_city
        @SerialName("commentDate")
        val commentDate: String, // 2021-05-11 17:30:25
        @SerialName("content")
        val content: String, // 支持
        @SerialName("id")
        val id: Int, // 8
        @SerialName("likeNum")
        val likeNum: Int, // 0
        @SerialName("newsId")
        val newsId: Int, // 28
        @SerialName("newsTitle")
        val newsTitle: String, // iPhone 13 再爆猛料：不止刘海屏有望缩小，超大杯或 将搭载 LTPO 屏
        @SerialName("userId")
        val userId: Int, // 2
        @SerialName("userName")
        val userName: String // test01
    )
}

@Serializable
data class GetCommentDetail(
    @SerialName("code")
    val code: Int, // 200
    @SerialName("data")
    val `data`: Data,
    @SerialName("msg")
    val msg: String // 操作成功
) {
    @Serializable
    data class Data(
        @SerialName("appType")
        val appType: String, // living
        @SerialName("commentDate")
        val commentDate: String, // 2021-05-11 17:30:25
        @SerialName("content")
        val content: String, // 支持
        @SerialName("id")
        val id: Int, // 8
        @SerialName("likeNum")
        val likeNum: Int, // 0
        @SerialName("newsId")
        val newsId: Int, // 28
        @SerialName("newsTitle")
        val newsTitle: String, // 卓创资讯：猪价放量急跌 多地猪价破“10”
        @SerialName("userId")
        val userId: Int, // 2
        @SerialName("userName")
        val userName: String // test01
    )
}