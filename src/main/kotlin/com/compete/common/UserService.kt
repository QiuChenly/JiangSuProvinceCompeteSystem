package com.compete.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.compete.DataBase.Utils.Users
import com.compete.Type.RequestType
import com.compete.Type.ResponseType
import com.compete.plugins.ChatServer
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.util.*

class UserService : KoinComponent {
    private val ct: ChatServer by inject()

    fun login(user: RequestType.LoginRequest): ResponseType.LoginResponse {
        val u = transaction {
            Users.select { Users.userName eq user.username }
        }
        if (transaction { u.count() } <= 0) return ResponseType.LoginResponse().apply {
            msg = "用户不存在."
            code = 201
        }
        if (transaction { u.first() }[Users.password] != user.password) return ResponseType.LoginResponse().apply {
            msg = "密码错误."
            code = 202
        }
        val token = JWT.create()
            .withAudience(CommonModule.jwtAudience)
            .withIssuer(CommonModule.jwtIssuer)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(CommonModule.jwtSecret))

        transaction {
            Users.update({ Users.userName eq user.username }) {
                it[Users.token] = token
            }
        }
        //管理员登录
        return ResponseType.LoginResponse().apply {
            this.token = token
            msg = "操作成功."
            code = 200
        }
    }

    suspend fun checkToken(token: String): Boolean {
        return false
    }
}

val userService = module {
    single {
        UserService()
    }
}