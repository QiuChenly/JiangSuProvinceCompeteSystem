package com.compete.DataBase.Utils

import com.compete.Type.BalanceListResponse
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.date.DateFormatUtils
import java.util.*

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
    val changeTime = long("changeTime").default(Date(System.currentTimeMillis()).time)

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