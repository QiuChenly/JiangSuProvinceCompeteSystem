package com.compete.routing

import com.compete.Type.LoginRequest
import com.compete.Type.ModifyRequest
import com.compete.Type.PasswordModifyRequest
import com.compete.Type.RegisterRequest
import com.compete.common.UserService
import com.compete.plugins.getQuery
import com.compete.plugins.getUserName
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent


val userServ: UserService by KoinJavaComponent.inject(UserService::class.java)
val http: HttpClient by KoinJavaComponent.inject(HttpClient::class.java)

fun Route.userRoute() {
    /**
     * 账户密码登录
     */
    post<LoginRequest>("/api/login") {
        val ret = userServ.login(it)
        call.respond(ret)
    }

    post<RegisterRequest>("/api/register") {
        call.respond(userServ.addUser(it))
    }


    authenticate("jwt-auth") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respond(
                hashMapOf(
                    "code" to "200",
                    "msg" to "Hello, $username! Token is expired at $expiresAt ms."
                )
            )
        }

        get("/api/common/user/getInfo") {
            val userName = getUserName()
            val user = userServ.findUserBasicInfo(userName)
            call.respond(user)
        }

        put<ModifyRequest>("/api/common/user") {
            val userName = getUserName()
            call.respond(userServ.modifyUserInfo(userName, it))
        }

        put<PasswordModifyRequest>("/api/common/user/resetPwd") {
            val name = getUserName()
            call.respond(userServ.modifyPassword(name, it))
        }

        post("/api/common/balance/recharge") {
            val money = call.parameters["money"]?.toInt() ?: 0
            call.respond(userServ.rechargeBalance(getUserName(), money))
        }

        get("/api/common/balance/list") {
            val pageNum = getQuery("pageNum")?.toInt() ?: 0
            val pageSize = getQuery("pageSize")?.toInt() ?: 0
            call.respond(userServ.balanceList(getUserName()))
        }
    }
}