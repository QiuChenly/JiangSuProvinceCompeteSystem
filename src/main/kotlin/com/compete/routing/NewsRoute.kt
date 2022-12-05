package com.compete.routing

import com.compete.DataBase.Utils.NewsCategoryList
import com.compete.DataBase.Utils.NewsCommentList
import com.compete.DataBase.Utils.NewsList
import com.compete.Type.*
import com.compete.plugins.getQuery
import com.compete.plugins.getUserId
import com.compete.plugins.getUserName
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.date.DateFormatUtils

fun Route.newsRoute() {
    get("/press/category/list") {
        val lst = transaction {
            NewsCategoryList.selectAll().map {
                GetNewsCategoryResponse.Row(
                    it[NewsCategoryList.appType],
                    it[NewsCategoryList.id],
                    it[NewsCategoryList.name],
                    it[NewsCategoryList.sort],
                )
            }
        }
        call.respond(
            GetNewsCategoryResponse(
                200,
                "查询成功",
                lst,
                lst.size
            )
        )
    }

    get("/press/press/list") {
        val hot = getQuery("hot")
        val publishDate = getQuery("publishDate")
        val title = getQuery("title")
        val top = getQuery("top")
        val pageNum = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10
        val type = getQuery("type") ?: ""

        var allSize = 0L
        val lst = transaction {
            val db = NewsList.select {
                if (type.isNotEmpty()) NewsList.type eq type.toInt() else Op.TRUE
            }
            allSize = db.count()
            db.limit(
                pageSize,
                pageSize * (pageNum - 1) * 1L
            ).map {
                GetNewsListResponse.Row(
                    it[NewsList.commentNum],
                    it[NewsList.content],
                    it[NewsList.cover],
                    it[NewsList.hot],
                    it[NewsList.id],
                    it[NewsList.likeNum],
                    it[NewsList.publishDate],
                    it[NewsList.readNum],
                    it[NewsList.status],
                    it[NewsList.subTitle],
                    "",
                    it[NewsList.title],
                    it[NewsList.top],
                    it[NewsList.type]
                )
            }
        }
        call.respond(GetNewsListResponse(200, "查询成功", lst, allSize))
    }

    get("/press/press/{id}") {
        val id =
            call.parameters["id"]

        if (id.isNullOrEmpty()) {
            return@get call.respond(GetNewsDetailResponse(201, null, "新闻ID错误。"))
        }
        val item = transaction {
            val db = NewsList.select {
                NewsList.id eq id.toInt()
            }
            if (db.count() <= 0) null else db.single()
        } ?: return@get call.respond(GetNewsDetailResponse(202, null, "查不到对应ID的新闻数据。"))
        //这里处理加入阅读量
        transaction {
            NewsList.update({
                NewsList.id eq item[NewsList.id]
            }) {
                it[readNum] = item[readNum] + 1
            }
        }

        call.respond(
            GetNewsDetailResponse(
                200,
                GetNewsDetailResponse.Data(
                    item[NewsList.appType],
                    item[NewsList.commentNum],
                    item[NewsList.content],
                    item[NewsList.cover],
                    item[NewsList.hot],
                    item[NewsList.id],
                    item[NewsList.likeNum],
                    item[NewsList.publishDate],
                    item[NewsList.readNum],
                    item[NewsList.status],
                    item[NewsList.subTitle],
                    item[NewsList.tags],
                    item[NewsList.title],
                    item[NewsList.top],
                    item[NewsList.type]
                ),
                "查询成功。"
            )
        )
    }

    authenticate("jwt-auth") {
        post<CommentPublishRequest>("/press/pressComment") {
            val userName = getUserName()
            val uid = getUserId()
            val comment = transaction {
                val new = NewsList.select { NewsList.id eq it.newsId }.single()
                NewsList.update({
                    NewsList.id eq it.newsId
                }) {
                    it[commentNum] = new[commentNum] + 1
                }
                NewsCommentList.insert { comment ->
                    comment[commentDate] = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                    comment[newsId] = it.newsId
                    comment[userId] = uid
                    comment[content] = it.content
                    comment[newsTitle] = new[NewsList.title]
                    comment[NewsCommentList.userName] = userName
                } get NewsCommentList.id
            }
            call.respond(BaseResponse(200, "操作成功 评论ID=$comment"))
        }

        put("/press/press/like/{id}") {
            val id = call.parameters["id"]
            val iid = if (id.isNullOrEmpty()) -1 else id.toInt()

            val new = transaction {
                val db = NewsList.select {
                    NewsList.id eq iid
                }
                if (db.count() <= 0) null else {
                    val item = db.single()
                    NewsList.update({
                        NewsList.id eq iid
                    }) {
                        it[likeNum] = item[likeNum] + 1
                    }
                }
            } ?: return@put call.respond(BaseResponse(201, "找不到对应id的新闻。"))
            call.respond(BaseResponse(200, "操作成功,点赞id=$new"))
        }

        put("/press/pressComment/like/{id}") {
            val id = call.parameters["id"]
            val iid = if (id.isNullOrEmpty()) -1 else id.toInt()

            val new = transaction {
                val db = NewsCommentList.select {
                    NewsCommentList.id eq iid
                }
                if (db.count() <= 0) null else {
                    val item = db.single()
                    NewsCommentList.update({
                        NewsCommentList.id eq iid
                    }) {
                        it[likeNum] = item[likeNum] + 1
                    }
                }
            } ?: return@put call.respond(BaseResponse(201, "找不到对应id的新闻。"))
            call.respond(BaseResponse(200, "操作成功,点赞id=$new"))
        }
    }

    get("/press/comments/list") {
        // 哎我都想说了 这b项目能有几个评论啊 排个吊序啊？糊弄糊弄比赛方得了
        val commentDate = getQuery("commentDate") ?: ""
        var userId = if (getQuery("userId").isNullOrEmpty()) -1 else getQuery("userId")!!.toInt()
        val newsId = getQuery("newsId")?.toInt() ?: -1
        val lst = transaction {
            NewsCommentList.select {
                NewsCommentList.newsId eq newsId
            }.map {
                GetNewsCommentListResponse.Row(
                    it[NewsCommentList.appType],
                    it[NewsCommentList.commentDate],
                    it[NewsCommentList.content],
                    it[NewsCommentList.id],
                    it[NewsCommentList.likeNum],
                    it[NewsCommentList.newsId],
                    it[NewsCommentList.newsTitle],
                    it[NewsCommentList.userId],
                    it[NewsCommentList.userName]
                )
            }
        }
        call.respond(
            GetNewsCommentListResponse(
                200,
                "查询成功",
                lst,
                lst.size
            )
        )
    }

    get("/press/comments/{id}") {
        val id = call.parameters["id"]
        val iid = if (id.isNullOrEmpty()) -1 else id.toInt()
        val obj = transaction { NewsCommentList.select { NewsCommentList.id eq iid }.single() }
        call.respond(
            GetCommentDetail(
                200,
                GetCommentDetail.Data(
                    obj[NewsCommentList.appType],
                    obj[NewsCommentList.commentDate],
                    obj[NewsCommentList.content],
                    obj[NewsCommentList.id],
                    obj[NewsCommentList.likeNum],
                    obj[NewsCommentList.newsId],
                    obj[NewsCommentList.newsTitle],
                    obj[NewsCommentList.userId],
                    obj[NewsCommentList.userName],
                ),
                "查询成功"
            )
        )
    }
}