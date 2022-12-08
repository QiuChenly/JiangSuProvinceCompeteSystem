package com.compete.DataBase

import com.compete.DataBase.Utils.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.sqlite.date.DateFormatUtils
import java.util.*


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
                AppealCategory,
                AppealList,
                BusOrder,
                BusLine,
                BusStep,
            )

            generateUserCollection()
            generateNewsCollection()
            generateBannerList()
            generateAppealCategory()
            generateLinesBusSteps()
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
                    it[advTitle] = "测试诉求轮播$i"
                    it[advImg] = "/010-h-${
                        (i % 3).let { if (it == 0) 3 else it }
                    }.jpg"
                    it[servModule] = "诉求"
                    it[targetId] = i
                    it[type] = 3
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

    private fun generateAppealCategory() {
        val size = AppealCategory.selectAll().count()
        if (size <= 0) {
            for (i in 1..8) {
                val img = "/010-Rect-${
                    (i % 3).let {
                        if (it == 0) 3 else it
                    }
                }.jpg"
                AppealCategory.insert {
                    it[searchValue] = ""
                    it[createBy] = ""
                    it[createTime] = ""
                    it[updateBy] = ""
                    it[updateTime] = ""
                    it[remark] = ""
                    it[name] = "诉求类别$i"
                    it[imgUrl] = img
                    it[sort] = i
                } get AppealCategory.id
            }
        }
    }

    fun sub(min: Int = 1, end: Int = 100): Int {
        return (min..end).random()
    }

    private fun generateLinesBusSteps() {
        val size = BusLine.selectAll().count()
        if (size <= 0) {
            val somePoints = LinkedList<String>()
            somePoints.addAll(
                ("莫愁新寓\n" +
                        "汉中门大街东2号线7号线南段(未开通)\n" +
                        "虎踞路·汉中门2号线\n" +
                        "虎踞路·清凉山13号线(未开通)7号线南段(未开通)\n" +
                        "国防园\n" +
                        "虎踞北路·草场门4号线7号线南段(未开通)\n" +
                        "古林公园\n" +
                        "双门楼宾馆5号线(未开通)\n" +
                        "盐仓桥\n" +
                        "四平路广场南9号线(未开通)\n" +
                        "大桥北路隧道南\n" +
                        "弘阳广场S8号线\n" +
                        "弘阳广场北\n" +
                        "泰山新村地铁站S8号线\n" +
                        "宁六路·东大路\n" +
                        "宁六路·丽景路3号线\n" +
                        "浦泗立交南\n" +
                        "六合大道·沿江\n" +
                        "高新医院\n" +
                        "旭日学府北S8号线\n" +
                        "六合大道·何庄路\n" +
                        "龙王山东\n" +
                        "南京信息工程大学S8\n" +
                        "鼓楼公交总站1号线4号线\n" +
                        "中山北路·大方巷\n" +
                        "山西路5号线(未开通)\n" +
                        "中山北路·虹桥5号线(未开通)\n" +
                        "水佐岗\n" +
                        "古平岗西7号线南段(未开通)\n" +
                        "定淮门\n" +
                        "佳盛花园\n" +
                        "南京铁道学院11号线(未开通)\n" +
                        "明发城市广场南\n" +
                        "浦厂小区\n" +
                        "浦珠北路·浦六路\n" +
                        "浦珠北路·三河桥\n" +
                        "柳洲北路南11号线(未开通)\n" +
                        "小柳工业园\n" +
                        "柳洲北路·丽岛路\n" +
                        "柳洲北路·泰达路\n" +
                        "泰山天然居西\n" +
                        "华侨绿洲东\n" +
                        "高新路·东大路\n" +
                        "东大成贤地铁站3号线\n" +
                        "创业新村西\n" +
                        "高新路·新科四路\n" +
                        "南大金陵学院南门东\n" +
                        "高新路·兰山路\n" +
                        "高新路·侨康路\n" +
                        "高新路·侨谊路\n" +
                        "六合大道·何庄路\n" +
                        "龙王山东\n" +
                        "南京信息工程大学S8号线\n" +
                        "大厂·杨庄北\n" +
                        "卸甲甸地铁站S8号线\n" +
                        "六合大道·晓山路\n" +
                        "大厂地铁站S8号线\n" +
                        "范旭东广场西\n" +
                        "葛塘广场南\n" +
                        "葛塘广场北\n" +
                        "葛塘地铁站S8号线\n" +
                        "六合大道·马汊河\n" +
                        "长芦地铁站南S8号线\n" +
                        "方巷小区东\n" +
                        "化工园地铁站S8号线\n" +
                        "沪江商贸城东\n" +
                        "画家村西\n" +
                        "六合开发区地铁站S8号线\n" +
                        "六合大道·虎跃路\n" +
                        "六合大道·龙华路\n" +
                        "龙池地铁站S8号线\n" +
                        "华欧大道·六合大道\n" +
                        "华欧大道·龙群路\n" +
                        "华欧大道·通池路\n" +
                        "茉莉苑\n" +
                        "龙湖半岛\n" +
                        "德邑花园\n" +
                        "文馨花苑北\n" +
                        "文博家园西\n" +
                        "复兴路·长江路\n" +
                        "复兴路·园林西路\n" +
                        "复兴路·建设西路\n" +
                        "机场东路·站前路\n" +
                        "六合北站").split("\n")
            )
            //首先生成线路信息
            for (i in 1..10) {
                val tmp = somePoints.clone() as LinkedList<String>
                val sStart = tmp[sub(0, tmp.size - 1)]
                tmp.remove(sStart)
                val sEnd = tmp[sub(0, tmp.size - 1)]
                tmp.remove(sEnd)

                val lineId = BusLine.insert {
                    it[name] = "${i}号线"
                    it[first] = sStart
                    it[end] = sEnd
                    it[startTime] = "6:30"
                    it[endTime] = "19:45"
                    it[price] = sub(3, 15)
                    it[mileage] = sub(20, 45).toString()
                } get BusLine.id

                val tPoints = sub(10, 40)
                for (j in 1..tPoints) {
                    BusStep.insert {
                        it[linesId] = lineId
                        it[stepsId] = j
                        it[sequence] = j
                        if ((j == 1) or (j == tPoints)) {
                            it[name] = if (j == 1) sStart else sEnd
                        } else {
                            val tStep = tmp[sub(0, tmp.size - 1)]
                            it[name] = tStep
                            tmp.remove(tStep)
                        }
                    }
                }
            }
        }
    }
}

