package com.compete.routing

import com.compete.Type.RequestType
import com.compete.Type.ResponseType
import com.compete.common.UserService
import com.compete.plugins.OPCODE
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent


val userServ: UserService by KoinJavaComponent.inject(UserService::class.java)
val http: HttpClient by KoinJavaComponent.inject(HttpClient::class.java)

fun Route.userRoute() {
    /**
     * 获取用户基本信息和权限级别
     */
    get("/login/{token}") {
        val token = call.parameters["token"]
        if ((token == null) or ((token?.length ?: 0) < 32)) {
            return@get call.respond(ResponseType.LoginResponse().apply {
                code = OPCODE.ERROR_DATA
                msg = "token错误"
            })
        }
        //验证在线数据库
        val valid = userServ.checkToken(token!!)
        call.respond(ResponseType.LoginResponse().apply {
            code = if (valid) OPCODE.TOKEN_VALID else OPCODE.TOKEN_ERROR
            msg = if (valid) "TOKEN_VALID" else "TOKEN_ERROR"
        })
    }

    /**
     * 账户密码登录
     */
    post<RequestType.LoginRequest>("/api/login") {
        val ret = userServ.login(it)
        call.respond(ret)
    }
}