package com.compete.routing

import com.compete.DataBase.Utils.AppealCategory
import com.compete.DataBase.Utils.AppealList
import com.compete.DataBase.Utils.BannerList
import com.compete.Type.*
import com.compete.plugins.getQuery
import com.compete.plugins.getUserId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.date.DateFormatUtils

// 诉求分类
fun Route.appealRoute() {
    /**
     * 获取全部诉求分类 分页查询
     */
    get("/api/gov-service-hotline/appeal-category/list") {
        val pageNum = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10
        var total = 0
        val lst = transaction {
            val db = AppealCategory.selectAll()
            total = db.count().toInt()
            db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                GetAppealCategoryResponse.Row(
                    it[AppealCategory.createBy],
                    it[AppealCategory.createTime],
                    it[AppealCategory.id],
                    it[AppealCategory.imgUrl],
                    it[AppealCategory.name],
                    GetAppealCategoryResponse.Row.Params(),
                    it[AppealCategory.remark],
                    it[AppealCategory.searchValue],
                    it[AppealCategory.sort],
                    it[AppealCategory.updateBy],
                    it[AppealCategory.updateTime],
                )
            }
        }
        call.respond(
            GetAppealCategoryResponse(
                200,
                "查询成功",
                lst,
                total
            )
        )


    }

    get("/api/gov-service-hotline/ad-banner/list") {
        val lst = transaction {
            BannerList.select { BannerList.type eq 3 }.map {
                GetAppealSuggestionBannerResponse.Data(
                    it[BannerList.id],
                    it[BannerList.advImg],
                    it[BannerList.sort],
                    it[BannerList.advTitle],
                )
            }
        }
        call.respond(
            GetAppealSuggestionBannerResponse(
                200,
                lst,
                "查询成功"
            )
        )
    }

    get("/api/gov-service-hotline/appeal/{id}") {
        val id = call.parameters["id"]?.toInt() ?: -1

        val data = transaction {
            val db = AppealList.select { AppealList.id eq id }
            if (db.count() > 0) {
                val sin = db.single()
                GetAppealDetails.Data(
                    sin[AppealList.appealCategoryId],
                    sin[AppealList.appealCategoryName],
                    sin[AppealList.content],
                    DateFormatUtils.format(sin[AppealList.createTime], "yyyy-MM-dd HH:mm:ss"),
                    sin[AppealList.detailResult],
                    sin[AppealList.id],
                    sin[AppealList.imgUrl],
                    sin[AppealList.state],
                    sin[AppealList.title],
                    sin[AppealList.undertaker],
                    sin[AppealList.userId],
                )
            } else null
        }
        call.respond(
            GetAppealDetails(
                if (data == null) 201 else 200,
                data,
                if (data == null) "该诉求不存在。" else "查询成功"
            )
        )

    }

    get("/api/gov-service-hotline/appeal/list"){
        val appealCategoryId = getQuery("appealCategoryId")?.toInt() ?: -1
        val pageNum = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10

        var total = 0
        val lst = transaction {
            val db = AppealList.select { AppealList.appealCategoryId eq appealCategoryId }
            total = db.count().toInt()
            db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                val sin = it
                GetAppealDetails.Data(
                    sin[AppealList.appealCategoryId],
                    sin[AppealList.appealCategoryName],
                    sin[AppealList.content],
                    DateFormatUtils.format(sin[AppealList.createTime], "yyyy-MM-dd HH:mm:ss"),
                    sin[AppealList.detailResult],
                    sin[AppealList.id],
                    sin[AppealList.imgUrl],
                    sin[AppealList.state],
                    sin[AppealList.title],
                    sin[AppealList.undertaker],
                    sin[AppealList.userId],
                )
            }
        }
        call.respond(
            GetMyAppealList(
                200,
                lst,
                "查询成功",
                total
            )
        )
    }

    authenticate("jwt-auth") {
        post<UserUploadAppeal>("/api/gov-service-hotline/appeal") {
            val uid = getUserId()
            val data = it
            //check same title
            val lst = transaction {
                val has = AppealList.select { AppealList.title eq data.title }.count()
                if (has <= 0) {
                    val cate = AppealCategory.select { AppealCategory.id eq data.appealCategoryId }.single()
                    //不存在 创建一个
                    val iid = AppealList.insert { obj ->
                        obj[userId] = uid
                        obj[appealCategoryId] = data.appealCategoryId
                        obj[appealCategoryName] = cate[AppealCategory.name]
                        obj[title] = data.title
                        obj[content] = data.content
                        obj[undertaker] = data.undertaker
                        obj[imgUrl] = data.imgUrl
                        obj[createTime] = System.currentTimeMillis()
                    } get AppealList.id
                    0
                } else {
                    -1
                }
            }
            call.respond(
                when (lst) {
                    -1 -> {
                        BaseResponse(
                            201,
                            "刁民不准重复提交相同标题！你以为政府机构信箱是垃圾桶啊？官爷的系统你也敢乱来，给我跪好了！"
                        )
                    }

                    0 -> {
                        BaseResponse(
                            200,
                            "提交成功,领导忙着开会呢,反馈已阅."
                        )
                    }

                    else -> {
                        BaseResponse(
                            203,
                            "猪脑过载,服务器出现异常。"
                        )
                    }
                }
            )
        }

        get("/api/gov-service-hotline/appeal/my-list") {
            val pageNum = getQuery("pageNum")?.toInt() ?: 1
            val pageSize = getQuery("pageSize")?.toInt() ?: 10
            val uid = getUserId()

            var total = 0
            val lst = transaction {
                val db = AppealList.select { AppealList.userId eq uid }
                total = db.count().toInt()

                db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                    val sin = it
                    GetAppealDetails.Data(
                        sin[AppealList.appealCategoryId],
                        sin[AppealList.appealCategoryName],
                        sin[AppealList.content],
                        DateFormatUtils.format(sin[AppealList.createTime], "yyyy-MM-dd HH:mm:ss"),
                        sin[AppealList.detailResult],
                        sin[AppealList.id],
                        sin[AppealList.imgUrl],
                        sin[AppealList.state],
                        sin[AppealList.title],
                        sin[AppealList.undertaker],
                        sin[AppealList.userId],
                    )
                }
            }
            call.respond(
                GetMyAppealList(
                    200,
                    lst,
                    "查询成功",
                    total
                )
            )
        }
    }
}