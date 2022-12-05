package com.compete.routing

import com.compete.DataBase.Utils.FeedbackList
import com.compete.Type.BaseResponse
import com.compete.Type.FeedbackUploadRequest
import com.compete.Type.GetFeedbackDetailsResponse
import com.compete.Type.GetFeedbackListResponse
import com.compete.plugins.getQuery
import com.compete.plugins.getUserId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.feedBack() {
    authenticate("jwt-auth") {
        post<FeedbackUploadRequest>("/api/common/feedback") { uploads ->
            val uid = getUserId()
            val id = transaction {
                FeedbackList.insert {
                    it[userId] = uid
                    it[content] = uploads.content
                    it[title] = uploads.title
                } get FeedbackList.id
            }
            call.respond(BaseResponse(200, "操作成功,反馈id是$id"))
        }

        get("/api/common/feedback/list") {
            val title = getQuery("title") ?: ""
            val uid = getUserId()

            val lst = transaction {
                FeedbackList.select {
                    FeedbackList.userId eq uid and if (title.isEmpty()) Op.TRUE else FeedbackList.title eq title
                }.map {
                    GetFeedbackListResponse.Row(
                        it[FeedbackList.appType],
                        it[FeedbackList.content],
                        it[FeedbackList.id],
                        it[FeedbackList.title],
                        it[FeedbackList.userId],
                    )
                }
            }
            call.respond(GetFeedbackListResponse(200, "查询成功.", lst, lst.size))
        }

        get("/api/common/feedback/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(GetFeedbackDetailsResponse(201, null, "id错误。"))
            val item = transaction {
                FeedbackList.select { FeedbackList.id eq id.toInt() }.single()
            }
            val ret = GetFeedbackDetailsResponse(
                200,
                GetFeedbackDetailsResponse.Data(
                    item[FeedbackList.appType],
                    item[FeedbackList.content],
                    item[FeedbackList.id],
                    item[FeedbackList.title],
                    item[FeedbackList.userId],
                ),
                "查询成功"
            )
            call.respond(ret)
        }
    }
}