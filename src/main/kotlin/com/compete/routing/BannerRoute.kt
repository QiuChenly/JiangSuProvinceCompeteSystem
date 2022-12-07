package com.compete.routing

import com.compete.DataBase.Utils.BannerList
import com.compete.Type.GetBannerResponse
import com.compete.plugins.getQuery
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.bannerRoute() {
    get("/api/rotation/list") {
        val type = getQuery("type")?.toInt() ?: -1
        val num = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10
        println("$type")
        var size = 0
        val lst = transaction {
            val db = BannerList.select { BannerList.type eq type }
            size = db.count().toInt()
            db.limit(pageSize, (num - 1L) * pageSize).map {
                GetBannerResponse.Row(
                    it[BannerList.advImg],
                    it[BannerList.advTitle],
                    it[BannerList.id],
                    it[BannerList.servModule],
                    it[BannerList.sort],
                    it[BannerList.targetId],
                    it[BannerList.type],
                )
            }
        }
        call.respond(
            GetBannerResponse(
                200,
                "查询成功",
                lst,
                size
            )
        )
    }
}