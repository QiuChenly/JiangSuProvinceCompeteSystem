package com.compete.DataBase

import com.compete.DataBase.Utils.BalanceList
import com.compete.DataBase.Utils.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent


class SqliteServer : KoinComponent {
    fun init() {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        transaction {
//            addLogger(StdOutSqlLogger) //生成CURD日志 我不需要看 相信JetBrains

            SchemaUtils.createMissingTablesAndColumns(
                Users,
                BalanceList,
            )

            val userSize = Users.selectAll().count()
            if (userSize <= 0) {
                try {
                    val user1Id = Users.insert {
                        it[userName] = "QiuChenly"
                        it[password] = "123456"
                        it[nickName] = "QiuChenly"
                        it[phonenumber] = "13888888888"
                        it[email] = "QiuChenly@qiuchenly.com"
                        it[score] = 10000
                        it[balance] = 10000
                        it[idCard] = "000000000000000000"
                        it[sex] = 0
                        it[avatar] = ""
                    } get Users.userId

                    val user2Id = Users.insert {
                        it[userName] = "Boss"
                        it[password] = "123456"
                        it[nickName] = "大boss"
                        it[email] = "Boss@qiuchenly.com"
                        it[phonenumber] = "13999999999"
                        it[score] = 10000
                        it[balance] = 10000
                        it[idCard] = "000000000000000000"
                        it[sex] = 0
                        it[avatar] = ""
                    } get Users.userId

                    val user3Id = Users.insert {
                        it[userName] = "test"
                        it[password] = "123456"
                        it[nickName] = "呜呜呜"
                        it[email] = "test@qiuchenly.com"
                        it[phonenumber] = "13982712137"
                        it[score] = -100
                        it[balance] = -200
                        it[idCard] = "000000000000000000"
                        it[sex] = 0
                        it[avatar] = ""
                    } get Users.userId

                    println("$user1Id - $user2Id - $user3Id 插入初始化数据成功。")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("插入初始化数据失败。")
                }
            }
        }
        println("初始化数据库完成。")
    }
}