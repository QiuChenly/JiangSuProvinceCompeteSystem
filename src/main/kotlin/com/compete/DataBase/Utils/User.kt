package com.compete.DataBase.Utils

import org.jetbrains.exposed.sql.Table

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
}