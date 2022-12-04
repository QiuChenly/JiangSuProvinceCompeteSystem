package com.compete.plugins

import com.compete.common.UserService
import com.compete.routing.baseRoute
import com.compete.routing.userRoute
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden)
        }

        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            when (cause) {
                is BadRequestException -> {
                    call.respond(
                        mapOf(
                            "code" to "-1",
                            "msg" to cause.stackTraceToString()
                        )
                    )
                    return@exception
                }

                else -> {
                    call.respond(
                        mapOf(
                            "code" to "-2",
                            "msg" to "数据解析错误"
                        )
                    )
                }
            }
        }
    }

    routing {
        trace {
            application.log.trace(it.buildText())
        }

        static("/") {
            resources("static")
            resources("video")
        }

        get("/") {
            call.respondRedirect("/index.html")
        }

        route("/base") {
            baseRoute()
        }

        route("/prod-api") {
            userRoute()
        }
    }
}

class AuthenticationException : RuntimeException()

class AuthorizationException : RuntimeException()