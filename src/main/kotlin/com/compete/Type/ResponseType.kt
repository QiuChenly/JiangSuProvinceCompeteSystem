package com.compete.Type

import com.google.gson.annotations.SerializedName


open class BaseResponse(
    var code: Int? = null,
    var msg: String? = null
)


class ResponseImage : BaseResponse() {
    val image: String = ""
}


class LoginResponse(
    var token: String? = null
) : BaseResponse()


class QueryUserInfoResponse(
    val user: User
) : BaseResponse() {

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


data class BalanceListResponse(
    val code: Int, // 200
    val msg: String, // 查询成功
    val rows: List<Row>,
    val total: Int // 10
) {

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


data class GetFeedbackListResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Int // 1
) {

    data class Row(
        @SerializedName("appType")
        val appType: String, // movie
        @SerializedName("content")
        val content: String, // 反馈内容
        @SerializedName("id")
        val id: Int, // 4
        @SerializedName("title")
        val title: String, // 发现错误
        @SerializedName("userId")
        val userId: Int // 1
    )
}


data class GetFeedbackDetailsResponse(
    @SerializedName("code")
    val code: Int, //  200
    @SerializedName("data")
    val data: Data?,
    @SerializedName("msg")
    val msg: String // 操作成功
) {

    data class Data(
        @SerializedName("appType")
        val appType: String, // movie
        @SerializedName("content")
        val content: String, // 反馈内容
        @SerializedName("id")
        val id: Int, // 4
        @SerializedName("title")
        val title: String, // 发现错误
        @SerializedName("userId")
        val userId: Int // 1
    )
}


data class GetNewsCategoryResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Int // 1
) {

    data class Row(
        @SerializedName("appType")
        val appType: String, // smart_city
        @SerializedName("id")
        val id: Int, // 9
        @SerializedName("name")
        val name: String, // 今日要闻
        @SerializedName("sort")
        val sort: Int // 1
    )
}


data class GetNewsListResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Long // 1
) {

    data class Row(
        @SerializedName("commentNum")
        val commentNum: Int, // 1
        @SerializedName("content")
        val content: String, // <p>内容<img src="/dev-api/profile/upload/image/202 1/04/07/a9434ccf-5acf-4bf5-a06e-c3457c6762e9.png"></p>
        @SerializedName("cover")
        val cover: String, // /dev-api/profile/upload/image/2021/04/01/c1eb74b2-e96 4-4388-830a-1b606fc9699f.png
        @SerializedName("hot")
        val hot: String, // N
        @SerializedName("id")
        val id: Int, // 5
        @SerializedName("likeNum")
        val likeNum: Int, // 2
        @SerializedName("publishDate")
        val publishDate: String, // 2021-04-01
        @SerializedName("readNum")
        val readNum: Int, // 10
        @SerializedName("status")
        val status: String, // Y
        @SerializedName("subTitle")
        val subTitle: String, // 测试新闻子标题
        @SerializedName("tags")
        val tags: String = "", // null
        @SerializedName("title")
        val title: String, // 测试新闻标题
        @SerializedName("top")
        val top: String, // Y
        @SerializedName("type")
        val type: Int // 2
    )
}


data class GetNewsDetailResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("data")
    val data: Data?,
    @SerializedName("msg")
    val msg: String // 操作成功
) {

    data class Data(
        @SerializedName("appType")
        val appType: String, // movie
        @SerializedName("commentNum")
        val commentNum: Int, // null
        @SerializedName("content")
        val content: String, // <p>企鹅王请问<img src="/dev-api/profile/upload/image /2021/04/07/a9434ccf-5acf-4bf5-a06e-c3457c6762e9.png"></p>
        @SerializedName("cover")
        val cover: String, // /dev-api/profile/upload/image/2021/04/01/c1eb74b2-e964- 4388-830a-1b606fc9699f.png
        @SerializedName("hot")
        val hot: String, // N
        @SerializedName("id")
        val id: Int, // 5
        @SerializedName("likeNum")
        val likeNum: Int, // 3
        @SerializedName("publishDate")
        val publishDate: String, // 2021-04-01
        @SerializedName("readNum")
        val readNum: Int, // null
        @SerializedName("status")
        val status: String, // Y
        @SerializedName("subTitle")
        val subTitle: String, // 123123123
        @SerializedName("tags")
        val tags: String = "", // null
        @SerializedName("title")
        val title: String, // 驱蚊器无去
        @SerializedName("top")
        val top: String, // Y
        @SerializedName("type")
        val type: Int // 2
    )
}


data class GetNewsCommentListResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Int // 1
) {

    data class Row(
        @SerializedName("appType")
        val appType: String, // smart_city
        @SerializedName("commentDate")
        val commentDate: String, // 2021-05-11 17:30:25
        @SerializedName("content")
        val content: String, // 支持
        @SerializedName("id")
        val id: Int, // 8
        @SerializedName("likeNum")
        val likeNum: Int, // 0
        @SerializedName("newsId")
        val newsId: Int, // 28
        @SerializedName("newsTitle")
        val newsTitle: String, // iPhone 13 再爆猛料：不止刘海屏有望缩小，超大杯或 将搭载 LTPO 屏
        @SerializedName("userId")
        val userId: Int, // 2
        @SerializedName("userName")
        val userName: String // test01
    )
}


data class GetCommentDetail(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("msg")
    val msg: String // 操作成功
) {

    data class Data(
        @SerializedName("appType")
        val appType: String, // living
        @SerializedName("commentDate")
        val commentDate: String, // 2021-05-11 17:30:25
        @SerializedName("content")
        val content: String, // 支持
        @SerializedName("id")
        val id: Int, // 8
        @SerializedName("likeNum")
        val likeNum: Int, // 0
        @SerializedName("newsId")
        val newsId: Int, // 28
        @SerializedName("newsTitle")
        val newsTitle: String, // 卓创资讯：猪价放量急跌 多地猪价破“10”
        @SerializedName("userId")
        val userId: Int, // 2
        @SerializedName("userName")
        val userName: String // test01
    )
}


data class GetBannerResponse(
    @SerializedName("code")
    val code: Int, //  200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Int // 1
) {

    data class Row(
        @SerializedName("advImg")
        val advImg: String, // http://152.136.210.130:7777/profile/upload/image/202 1/04/26/183e63c6-a59d-4551-a5b4-7055ff7a9575.jpg
        @SerializedName("advTitle")
        val advTitle: String, // 测试首页轮播
        @SerializedName("id")
        val id: Int, // 14
        @SerializedName("servModule")
        val servModule: String, // 新闻
        @SerializedName("sort")
        val sort: Int, // 1
        @SerializedName("targetId")
        val targetId: Int, // 1
        @SerializedName("type")
        val type: Int // 2
    )
}

data class GetAppealCategoryResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // 查询成功
    @SerializedName("rows")
    val rows: List<Row>,
    @SerializedName("total")
    val total: Int // 1
) {
    data class Row(
        @SerializedName("createBy")
        val createBy: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: Int, // 4
        @SerializedName("imgUrl")
        val imgUrl: String, // /dev-api/profile/upload/image/2022/02/24/aed84 b53-f416-4af1-be5e-e70a9ddb8027.png
        @SerializedName("name")
        val name: String, // 衣食住行
        @SerializedName("params")
        val params: Params = Params(),
        @SerializedName("remark")
        val remark: String,
        @SerializedName("searchValue")
        val searchValue: String,
        @SerializedName("sort")
        val sort: Int, // 1
        @SerializedName("updateBy")
        val updateBy: String,
        @SerializedName("updateTime")
        val updateTime: String
    ) {
        class Params
    }
}

data class GetAppealSuggestionBannerResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("msg")
    val msg: String // 操作成功
) {
    data class Data(
        @SerializedName("id")
        val id: Int, // 4
        @SerializedName("imgUrl")
        val imgUrl: String, // /dev-api/profile/upload/image/2022/02/24/8d1e9174-7333-44c4-8d17-a64579c2677f.jpg
        @SerializedName("sort")
        val sort: Int, // 1
        @SerializedName("title")
        val title: String // 测试图片
    )
}

data class GetAppealDetails(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("data")
    val data: Data?,
    @SerializedName("msg")
    val msg: String // 操作成功
) {
    data class Data(
        @SerializedName("appealCategoryId")
        val appealCategoryId: Int, // 4
        @SerializedName("appealCategoryName")
        val appealCategoryName: String, // 衣食住行
        @SerializedName("content")
        val content: String, // 因为疫情，物价是在太高了，请处理一下。
        @SerializedName("createTime")
        val createTime: String, // 2022-02-24 15:38:50
        @SerializedName("detailResult")
        val detailResult: String, // 以让他们降价了
        @SerializedName("id")
        val id: Int, // 4
        @SerializedName("imgUrl")
        val imgUrl: String, // /dev-api/profile/upload/image/2022/02/24/82a57055- 3298-4b74-94e0-d2a222d3d19e.png
        @SerializedName("state")
        val state: String, // 1
        @SerializedName("title")
        val title: String, // 这是一个诉求
        @SerializedName("undertaker")
        val undertaker: String, // 物价局
        @SerializedName("userId")
        val userId: Int // 105
    )
}

data class GetMyAppealList(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("rows")
    val rows: List<GetAppealDetails.Data>,
    @SerializedName("msg")
    val msg: String, // 操作成功
    @SerializedName("total")
    val total:Int,
)