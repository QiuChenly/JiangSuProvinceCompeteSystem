package com.compete.DataBase.Utils

import com.compete.Type.BalanceListResponse
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.date.DateFormatUtils

object Users : Table() {
    val userId = integer("userId").autoIncrement()
    val userName = varchar("userName", Int.MAX_VALUE).uniqueIndex("userName")
    val nickName = varchar("nickName", Int.MAX_VALUE).uniqueIndex("nickName")
    val password = varchar("password", Int.MAX_VALUE)
    val phonenumber = varchar("phonenumber", 11).default("")
    val sex = integer("sex").default(0)
    val email = varchar("email", 100).default("")
    val idCard = varchar("idCard", 18)
    val avatar = varchar("avatar", Int.MAX_VALUE).default("")
    val balance = integer("balance").default(0)
    val score = integer("score").default(0)
    val token = varchar("token", Int.MAX_VALUE).default("")

    override val primaryKey = PrimaryKey(userId, name = "userNameId")

    fun findUser(userName: String) = transaction { Users.select { Users.userName eq userName } }
}

object BalanceList : BaseTable() {
    val appType = str("appType")
    val userId = int("userId") references Users.userId
    val event = str("event").default("缴纳违章罚款")
    val changeAmount = int("changeAmount").default(0)
    val changeType = str("changeType").default("支出")
    val userName = str("userName")
    val changeTime = long("changeTime")

    fun getListByUserId(userId: Int): List<BalanceListResponse.Row> {
        return transaction {
            BalanceList.select {
                BalanceList.userId eq userId
            }.map {
                BalanceListResponse.Row(
                    id = it[BalanceList.id],
                    appType = it[appType],
                    userId = it[BalanceList.userId],
                    event = it[event],
                    changeAmount = it[changeAmount],
                    changeType = it[changeType],
                    userName = it[userName],
                    changeTime = DateFormatUtils.format(it[changeTime], "yyyy-MM-dd HH:mm:ss"),
                )
            }
        }
    }
}

object FeedbackList : BaseTable("feedbackId") {
    val appType = str("appType").default("movie")
    val title = str("title")
    val content = str("content")
    val userId = int("userId") references Users.userId
}

object NewsCategoryList : BaseTable("newsCategoryId") {
    val appType = str("appType").default("smart_city")
    val name = str("name")
    val sort = int("sort").default(1)
}

object NewsList : Table() {
    //        "id": 5,
//        "cover": "/dev-api/profile/upload/image/2021/04/01/c1eb74b2-e96 4-4388-830a-1b606fc9699f.png",
//        "title": "测试新闻标题",
//        "subTitle": "测试新闻子标题",
//        "content": "<p>内容<img src=\"/dev-api/profile/upload/image/202 1/04/07/a9434ccf-5acf-4bf5-a06e-c3457c6762e9.png\"></p>",
//        "status": "Y",
//        "publishDate": "2021-04-01",
//        "tags": null,
//        "commentNum": 1,
//        "likeNum": 2,
//        "readNum": 10,
//        "type": "2",
//        "top": "Y",
//        "hot": "N"
    val id = integer("id").autoIncrement()
    val appType = varchar("appType", Int.MAX_VALUE).default("movie")
    val cover = varchar("cover", Int.MAX_VALUE).default("")
    val title = varchar("title", Int.MAX_VALUE).default("")
    val subTitle = varchar("subTitle", Int.MAX_VALUE).default("")
    val content = varchar("content", Int.MAX_VALUE).default("")
    val status = varchar("status", Int.MAX_VALUE).default("Y")
    val publishDate = varchar("publishDate", Int.MAX_VALUE).default("")
    val tags = varchar("tags", Int.MAX_VALUE).default("null")
    val commentNum = integer("commentNum").default(0)
    val likeNum = integer("likeNum").default(0)
    val readNum = integer("readNum").default(0)
    val type = integer("type")
    val top = varchar("top", Int.MAX_VALUE).default("N")
    val hot = varchar("hot", Int.MAX_VALUE).default("N")

    override val primaryKey = PrimaryKey(id, name = "newsId")
}

object NewsCommentList : Table() {
    val id = integer("id").autoIncrement()
    val commentDate = varchar("commentDate", Int.MAX_VALUE).default("")
    val newsId = integer("newsId") references NewsList.id
    val userId = integer("userId") references Users.userId
    val appType = varchar("appType", Int.MAX_VALUE).default("living")
    val likeNum = integer("likeNum").default(0)
    val content = varchar("content", Int.MAX_VALUE).default("")
    val userName = varchar("userName", Int.MAX_VALUE).default("")
    val newsTitle = varchar("newsTitle", Int.MAX_VALUE).default("")
    override val primaryKey = PrimaryKey(id, name = "newsCommentId")
}


object BannerList : Table() {
    val id = integer("id").autoIncrement()
    val sort = integer("sort").default(1)
    val advTitle = varchar("advTitle", Int.MAX_VALUE).default("")
    val advImg = varchar("advImg", Int.MAX_VALUE).default("")
    val servModule = varchar("servModule", Int.MAX_VALUE).default("")
    val targetId = integer("targetId").default(1)
    val type = integer("type").default(1)

    override val primaryKey = PrimaryKey(id, name = "bannerId")
}

// val advTitle = varchar("advTitle", Int.MAX_VALUE).default("")
// val targetId = integer("targetId").default(0)

object AppealCategory : Table() {
    val id = integer("id").autoIncrement()
    val searchValue = varchar("searchValue", Int.MAX_VALUE).default("")
    val createBy = varchar("createBy", Int.MAX_VALUE).default("")
    val createTime = varchar("createTime", Int.MAX_VALUE).default("")
    val updateBy = varchar("updateBy", Int.MAX_VALUE).default("")
    val updateTime = varchar("updateTime", Int.MAX_VALUE).default("")
    val remark = varchar("remark", Int.MAX_VALUE).default("")
    val name = varchar("name", Int.MAX_VALUE).default("")
    val imgUrl = varchar("imgUrl", Int.MAX_VALUE).default("")
    val sort = integer("sort").default(0)

    override val primaryKey = PrimaryKey(id, name = "appealCategoryId")
}

object AppealList : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("userId") references Users.userId
    val appealCategoryId = integer("appealCategoryId") references AppealCategory.id
    val appealCategoryName = varchar("appealCategoryName", Int.MAX_VALUE).default("")
    val title = varchar("title", Int.MAX_VALUE).default("")
    val content = varchar("content", Int.MAX_VALUE).default("")
    val undertaker = varchar("undertaker", Int.MAX_VALUE).default("")
    val imgUrl = varchar("imgUrl", Int.MAX_VALUE).default("")

    //TODO 可笑的是API文档里甚至没有这个字段的解释 笑嘻了 什么杀软文档 这b水平回家种地吧 别整nmb后台开发了 纯纯大水b一个 拉低行业平均水平
    //gsh_appeal_state 你给我解释下这个系统字典在哪里 别说写在数据库里 你怎么不把nm写到数据库里然后用POST查找你吗？
    val state = varchar("state", Int.MAX_VALUE).default("0")//自定义 0-未处理 1-已处理 2-处理中
    val detailResult = varchar("detailResult", Int.MAX_VALUE).default("")
    val createTime = long("createTime")

    override val primaryKey = PrimaryKey(id, name = "appealListId")
}

open class BaseTable(val pKey: String? = null) : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id, name = pKey)

    /**
     * 字符类型
     */
    fun str(nameId: String, default: String = "") = varchar(nameId, Int.MAX_VALUE).default(default)

    /**
     * int 类型
     */
    fun int(nameId: String, default: Int = 0) = integer(nameId).default(default)
}

object BusOrder : BaseTable() {
    val orderNum = str("orderNum")
    val path = str("path")
    val start = str("start")
    val end = str("end")
    val price = int("price")
    val userName = str("userName")
    val userTel = str("userTel")
    val userId = int("userId")

    /**
     * 未支付0 已支付1
     */
    val status = int("status")
    val paymentType = str("paymentType").default("电子支付")
    val payTime = str("payTime")
}

object BusLine:BaseTable(){
    /**
     * 路线名称
     */
    val name = str("name")
    val first = str("first")
    val end = str("end")
    val startTime = str("startTime")
    val endTime = str("endTime")
    val price = int("price")

    /**
     * 里程
     */
    val mileage = str("mileage")
}

object BusStep:BaseTable(){
    /**
     * 路线 id
     */
    val linesId = int("linesId") references BusLine.id
    /**
     * 站点 id
     */
    val stepsId = int("stepsId")
    val name = str("name").default("xxxx街道")
    /**
     * 站点顺序
     */
    val sequence = int("sequence")
}