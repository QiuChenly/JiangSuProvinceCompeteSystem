package com.compete.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.compete.DataBase.Utils.BalanceList
import com.compete.DataBase.Utils.Users
import com.compete.Type.*
import com.compete.plugins.ChatServer
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.util.*

class UserService : KoinComponent {
    private val ct: ChatServer by inject()

    fun login(user: LoginRequest): LoginResponse {
        val u = transaction {
            Users.select { Users.userName eq user.username }
        }
        if (transaction { u.count() } <= 0) return LoginResponse().apply {
            msg = "用户不存在."
            code = 201
        }
        if (transaction { u.first() }[Users.password] != user.password) return LoginResponse().apply {
            msg = "密码错误."
            code = 202
        }
        val token = JWT.create().withAudience(CommonModule.jwtAudience).withIssuer(CommonModule.jwtIssuer)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + (60000L * 60 * 24 * 31)))
            .sign(Algorithm.HMAC256(CommonModule.jwtSecret))

        transaction {
            Users.update({ Users.userName eq user.username }) {
                it[Users.token] = token
            }
        }
        //管理员登录
        return LoginResponse().apply {
            this.token = token
            msg = "操作成功."
            code = 200
        }
    }

    /**
     * 注册用户
     */
    fun addUser(user: RegisterRequest): BaseResponse {
        //检查是否有同名用户
        val size = transaction {
            Users.select { (Users.userName eq user.userName) or (Users.nickName eq user.nickName) }.count()
        }
        if (size > 0) {
            return BaseResponse(code = 201, msg = "用户已存在或昵称重复！请重新检查用户信息。")
        }
        val userId = transaction {
            Users.insert {
                it[userName] = user.userName
                it[password] = user.password
                it[nickName] = user.nickName
                it[email] = user.email
                it[phonenumber] = user.phonenumber
                it[score] = 0
                it[balance] = 0
                it[idCard] = user.idCard
                it[sex] = user.sex
                it[avatar] = user.avatar
            } get Users.userId
        }
        return BaseResponse(code = 200, msg = "操作成功,用户ID是 $userId")
    }

    /**
     * 查找用户并返回基本信息
     */
    fun findUserBasicInfo(userName: String): QueryUserInfoResponse {
        val user = transaction { Users.findUser(userName).single() }

        return QueryUserInfoResponse(
            QueryUserInfoResponse.User(
                userId = user[Users.userId],
                userName = user[Users.userName],
                nickName = user[Users.nickName],
                email = user[Users.email],
                phonenumber = user[Users.phonenumber],
                sex = user[Users.sex],
                avatar = user[Users.avatar],
                idCard = user[Users.idCard],
                balance = user[Users.balance],
                score = user[Users.score],
            )
        ).apply {
            code = 200
            msg = "操作成功"
        }
    }

    fun modifyUserInfo(userName: String, user: ModifyRequest): BaseResponse {
        if (transaction {
                Users.findUser(userName).count()
            } > 0) return BaseResponse(code = 201, msg = "nickName重复,请修改后再试!")

        val id = transaction {
            Users.update({
                Users.userName eq userName
            }) {
                it[email] = user.email
                it[idCard] = user.idCard
                it[nickName] = user.nickName
                it[phonenumber] = user.phonenumber
                it[sex] = user.sex
            }
        }
        return BaseResponse(code = 200, msg = "操作成功 id=$id")
    }


    /**
     * 修改用户密码
     * 注意：此处并没有对修改密码后的用户token进行注销 反正比赛也不需要这么严格的要求 无所谓啦
     */
    fun modifyPassword(userName: String, pwd: PasswordModifyRequest): BaseResponse {
        val user = transaction { Users.findUser(userName).single() }
        if (user[Users.password] != pwd.oldPassword) return BaseResponse(code = 201, msg = "密码错误。")
        transaction {
            Users.update({ Users.userName eq userName }) {
                it[password] = pwd.newPassword
            }
        }
        return BaseResponse(code = 200, msg = "修改成功。")
    }

    fun rechargeBalance(
        userName: String,
        money: Int,
        mEvent: String = "余额充值",
        mChangeType: String = "收入"
    ): BaseResponse {
        val user = transaction { Users.findUser(userName).single() }
        val currentBalance = user[Users.balance]
        val balanceId = transaction {

            //check user amount for pay price
            if (money < 0) {
                if ((currentBalance <= 0) or (currentBalance < money * -1)) {
                    return@transaction null
                }
            }

            Users.update({
                Users.userName eq userName
            }) {
                it[balance] = currentBalance + money
            }
            BalanceList.insert {
                it[BalanceList.userName] = user[Users.userName]
                it[userId] = user[Users.userId]
                it[appType] = "balanceList"
                it[event] = mEvent
                it[changeAmount] = money
                it[changeType] = mChangeType
                it[changeTime] = Date(System.currentTimeMillis()).time
            } get BalanceList.id
        }
        return if (balanceId == null) BaseResponse(201, "余额不足请充值")
        else BaseResponse(200, "操作成功。账单编号 $balanceId")
    }

    fun balanceList(userName: String): BalanceListResponse {
        val user = transaction { Users.findUser(userName).single() }
        val uid = user[Users.userId]
        val lst = BalanceList.getListByUserId(uid)
        return BalanceListResponse(
            200,
            "操作成功",
            lst,
            lst.size
        )
    }
}

val userService = module {
    single {
        UserService()
    }
}