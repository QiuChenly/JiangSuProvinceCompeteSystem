package com.compete.DataBase.Utils

import com.compete.Type.BalanceListResponse
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.date.DateFormatUtils

enum class UserPermission {
    ADMIN, USER, TEMP
}

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

object BalanceList : Table() {
    val id = integer("id").autoIncrement()
    val appType = varchar("appType", Int.MAX_VALUE).default("")
    val userId = integer("userId") references Users.userId
    val event = varchar("event", Int.MAX_VALUE).default("缴纳违章罚款")
    val changeAmount = integer("changeAmount").default(0)
    val changeType = varchar("changeType", Int.MAX_VALUE).default("支出")
    val userName = varchar("userName", Int.MAX_VALUE).default("")
    val changeTime = long("changeTime")

    override val primaryKey = PrimaryKey(id, name = "balanceId")


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

object FeedbackList : Table() {
    val id = integer("id").autoIncrement()
    val appType = varchar("appType", Int.MAX_VALUE).default("movie")
    val title = varchar("title", Int.MAX_VALUE).default("")
    val content = varchar("content", Int.MAX_VALUE).default("")
    val userId = integer("userId") references Users.userId

    override val primaryKey = PrimaryKey(id, name = "feedbackId")
}

object NewsCategoryList : Table() {
    val id = integer("id").autoIncrement()
    val appType = varchar("appType", Int.MAX_VALUE).default("smart_city")
    val name = varchar("name", Int.MAX_VALUE).default("")
    val sort = integer("sort").default(1)

    override val primaryKey = PrimaryKey(id, name = "newsCategoryId")
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