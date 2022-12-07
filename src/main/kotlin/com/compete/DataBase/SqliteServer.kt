package com.compete.DataBase

import com.compete.DataBase.Utils.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.sqlite.date.DateFormatUtils


class SqliteServer : KoinComponent {
    fun init() {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        transaction {
//            addLogger(StdOutSqlLogger) //生成CURD日志 我不需要看 相信JetBrains
            SchemaUtils.createMissingTablesAndColumns(
                Users,
                BalanceList,
                FeedbackList,
                NewsCategoryList,
                NewsList,
                NewsCommentList,
                BannerList,
            )

            generateUserCollection()
            generateNewsCollection()
            generateBannerList()
        }
        println("初始化数据库完成。")
    }

    private fun generateUserCollection() {
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
                    it[avatar] = "/010-h-1.jpg"
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
                    it[avatar] = "/010-h-1.jpg"
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
                    it[avatar] = "/010-h-1.jpg"
                } get Users.userId

                println("$user1Id - $user2Id - $user3Id 插入初始化数据成功。")
            } catch (e: Exception) {
                e.printStackTrace()
                println("插入初始化数据失败。")
            }
        }
    }

    private fun generateNewsCollection() {
        val size = NewsCategoryList.selectAll().count()
        if (size <= 0) {
            for (i in 1..8) {
                val typeId = NewsCategoryList.insert {
                    it[name] = "新闻类别$i"
                    it[sort] = i
                } get NewsCategoryList.id

                for (j in 1..100) {
                    NewsList.insert {
                        it[cover] = "/010-h-1.jpg"
                        it[title] = "测试新闻${j}标题"
                        it[subTitle] = "测试新闻${j}子标题"
                        it[content] =
                            "<p>内容<img src=\"/010-h-1.jpg\"></p>"
                        it[type] = typeId//todo 此处存疑 不知道这个typeId是不是就是自动生成的id
                        it[publishDate] = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd")
                    }
                }
            }
        }
    }

    private fun generateBannerList() {
        val size = BannerList.selectAll().count()
        if (size <= 0) {
            for (i in 1..10) {
                BannerList.insert {
                    it[sort] = i
                    it[advTitle] = "测试首页轮播$i"
                    it[advImg] = "/010-h-${
                        (i % 3).let { if (it == 0) 3 else it }
                    }.jpg"
                    it[servModule] = "新闻"
                    it[targetId] = i
                    it[type] = 2
                }
                BannerList.insert {
                    it[sort] = i
                    it[advTitle] = "测试引导页轮播$i"
                    it[advImg] = "/010-${
                        (i % 7).let { if (it == 0) 7 else it }
                    }.jpg"
                    it[servModule] = "引导页轮播"
                    it[targetId] = i
                    it[type] = 1
                }
            }
        }
    }
}

