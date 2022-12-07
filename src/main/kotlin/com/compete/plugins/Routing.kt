package com.compete.plugins

import com.compete.DataBase.Utils.Users
import com.compete.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


fun PipelineContext<*, ApplicationCall>.getUserName(): String =
    call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()

fun PipelineContext<*, ApplicationCall>.getUserId() =
    transaction { Users.select { Users.userName eq getUserName() }.single()[Users.userId] }

fun PipelineContext<*, ApplicationCall>.getQuery(key: String) = call.request.queryParameters[key]

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
//            application.log.trace(it.buildText())
        }

        static("/") {
            defaultResource("/index.html", "static")
            resources("static")
            resources("video")
        }

        route("/base") {
            baseRoute()
        }

        route("/prod-api") {
            userRoute()
            feedBack()
            newsRoute()
            bannerRoute()
            fileUploadRoute()
        }
    }
}

class AuthenticationException : RuntimeException()

class AuthorizationException : RuntimeException()