package com.compete.routing

import com.compete.DataBase.Utils.BusLine
import com.compete.DataBase.Utils.BusOrder
import com.compete.DataBase.Utils.BusStep
import com.compete.DataBase.Utils.Users
import com.compete.Type.*
import com.compete.common.UserService
import com.compete.plugins.getQuery
import com.compete.plugins.getUserId
import com.compete.plugins.getUserName
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.java.KoinJavaComponent
import org.sqlite.date.DateFormatUtils
import java.util.*

fun Route.smartBusRoute() {
    val userServ: UserService by KoinJavaComponent.inject(UserService::class.java)

    authenticate("jwt-auth") {
        post<BusPay>("/api/bus/pay") { data ->
            val uid = getUserId()
            val ret = transaction {
                //check user self orders and it exists
                val db = BusOrder.select {
                    BusOrder.orderNum eq data.orderNum
                    BusOrder.userId eq uid
                    BusOrder.status eq 0
                }
                if (db.count() <= 0)
                    return@transaction null
                val item = db.single()

                //开始扣款
                val state = userServ.rechargeBalance(getUserName(), -1 * item[BusOrder.price], "巴士订单", "支出")
                if (state.code == 201) return@transaction -1
                BusOrder.update({
                    BusOrder.orderNum eq data.orderNum
                    BusOrder.userId eq uid
                    BusOrder.status eq 0
                }) {
                    it[status] = 1
                    it[paymentType] = data.paymentType
                    it[payTime] = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                }
            }
            call.respond(
                when (ret) {
                    null -> BaseResponse(
                        201,
                        "订单不存在或订单无效!"
                    )

                    -1 -> BaseResponse(
                        202,
                        "余额不足，请充值后再支付订单!"
                    )

                    else -> BaseResponse(
                        200,
                        "操作成功,操作码:$ret"
                    )
                }
            )
        }

        get("/api/bus/order/list") {
            val uid = getUserId()
            val pageNum = getQuery("pageNum")?.toInt() ?: 1
            val pageSize = getQuery("pageSize")?.toInt() ?: 8
            var total = 0
            val lst = transaction {
                val db = BusOrder.select { BusOrder.userId eq uid }
                total = db.count().toInt()
                db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                    GetOrderListResponse.Row(
                        it[BusOrder.end],
                        it[BusOrder.id],
                        it[BusOrder.orderNum],
                        it[BusOrder.path],
                        it[BusOrder.payTime],
                        it[BusOrder.paymentType],
                        it[BusOrder.price],
                        it[BusOrder.start],
                        it[BusOrder.status],
                        it[BusOrder.userId],
                        it[BusOrder.userName],
                        it[BusOrder.userTel],
                    )
                }
            }
            call.respond(
                GetOrderListResponse(
                    200,
                    "查询完成",
                    lst,
                    total
                )
            )
        }

        post<BusOrderRequest>("/api/bus/order") { data ->
            val uid = getUserId()
            val orderNum = transaction {
                val phone = Users.select { Users.userId eq uid }.single()[Users.phonenumber]
                BusOrder.insert {
                    val orderNum = Random().nextInt(100000000)
                    it[BusOrder.orderNum] = orderNum.toString()
                    it[start] = data.start
                    it[end] = data.end
                    it[path] = data.path
                    it[price] = data.price
                    //笑死 写的什么垃圾文档 用户传递订单状态到后端？蚂蚁金服看了你的文档都直呼内行。
                    it[status] = 0//data.status //我强制后端让他成为0未支付状态
                    it[userName] = getUserName()
                    it[userId] = getUserId()
                    it[userTel] = phone
                } get BusOrder.orderNum
            }

            call.respond(
                BusOrderResponse(
                    200,
                    "操作成功",
                    orderNum
                )
            )
        }
    }

    get("/api/bus/stop/list") {
        val linesId = getQuery("linesId")?.toInt() ?: -1
        val pageNum = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10

        var total = 0
        val lst = transaction {
            val db = BusStep.select { BusStep.linesId eq linesId }
            total = db.count().toInt()
            db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                GetBusStepsResponse.Row(
                    it[BusStep.linesId],
                    it[BusStep.name],
                    it[BusStep.sequence],
                    it[BusStep.stepsId],
                )
            }
        }

        call.respond(
            GetBusStepsResponse(
                200,
                "查询成功",
                lst,
                total
            )
        )

    }

    get("/api/bus/line/list") {
        val pageNum = getQuery("pageNum")?.toInt() ?: 1
        val pageSize = getQuery("pageSize")?.toInt() ?: 10
        var total = 0
        val lst = transaction {
            val db = BusLine.selectAll()
            total = db.count().toInt()
            db.limit(pageSize, (pageNum - 1L) * pageSize).map {
                GetLinesListResponse.Row(
                    it[BusLine.end],
                    it[BusLine.endTime],
                    it[BusLine.first],
                    it[BusLine.id],
                    it[BusLine.mileage],
                    it[BusLine.name],
                    it[BusLine.price],
                    it[BusLine.startTime]
                )
            }
        }
        call.respond(
            GetLinesListResponse(
                200,
                "查询成功",
                lst,
                total
            )
        )
    }

    get("/api/bus/line/{id}") {
        val id = call.parameters["id"]?.toInt() ?: -1
        val row = transaction {
            val db = BusLine.select { BusLine.id eq id }

            if (db.count() <= 0) null
            else {
                val it = db.single()
                GetLinesListResponse.Row(
                    it[BusLine.end],
                    it[BusLine.endTime],
                    it[BusLine.first],
                    it[BusLine.id],
                    it[BusLine.mileage],
                    it[BusLine.name],
                    it[BusLine.price],
                    it[BusLine.startTime]
                )
            }
        }
        call.respond(
            if (row == null) BaseResponse(201, "线路查询错误,请检查线路是否正确。")
            else GetLinesDetail(200, "查询成功", row)
        )
    }
}